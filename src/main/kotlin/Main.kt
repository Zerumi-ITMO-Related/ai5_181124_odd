package io.github.zerumi

import io.github.zerumi.core.DTDataset
import io.github.zerumi.core.DecisionTreeNode
import io.github.zerumi.core.buildTree
import io.github.zerumi.csv.DatasetEntry
import io.github.zerumi.csv.readCsv
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main() {
    val dataset = readCsv(
        {}::class.java.classLoader.getResourceAsStream("DATA.csv")
            ?: throw IllegalArgumentException("Resource not found")
    )

    val entries = dataset.entries.shuffled()

    val testDataSize = (entries.size * 0.6f).roundToInt()
    val testData = dataset.entries.take(testDataSize)

    val randomFeatures = (0..<30).toList().shuffled().take(sqrt(30.0f).roundToInt())

    val dtds = object : DTDataset<Int> {
        override fun getClassVector(): List<*> = testData.extractClasses()

        override fun getFeatureDescriptionVector(): List<Int> = randomFeatures

        override fun getFeatureVectors(): List<List<*>> = randomFeatures.map {
            testData.extractFeature(it)
        }
    }

    val tree = buildTree(dtds)

    val data = entries.takeLast(entries.size - testDataSize)

    val calculatedData = data.map { treeTraverse(tree, it) }

    println("Calculated grade: ")
    println(calculatedData.take(15))
    println("Real grade: ")
    println(data.take(15).map { it.grade })

    val calculatedFloats = calculatedData.map { (it.toString().toFloatOrNull() ?: 0.0f) > 3.0f }
    val realFloats = data.map { it.grade > 3.0f }

    val matches = calculatedFloats.zip(realFloats).count { it.first == it.second }
    val accuracy = matches / data.size.toFloat()

    println("Total matches: $matches / ${data.size} -> accuracy: $accuracy")

    val truePositive = calculatedFloats.zip(realFloats).count { it.first && it.second }
    val falsePositive = calculatedFloats.zip(realFloats).count { it.first && !it.second }
    val falseNegative = calculatedFloats.zip(realFloats).count { !it.first && it.second }

    val precision = truePositive / (truePositive + falsePositive).toFloat()
    val recall = truePositive / (truePositive + falseNegative).toFloat()

    println("Precision: $precision; Recall: $recall")
}

fun treeTraverse(tree: DecisionTreeNode<Int>, it: DatasetEntry): Any? {
    val currentFeature = tree.feature ?: return tree.`class`
    val currentEntryFeature = it.features[currentFeature]
    val currentLeaf = tree.children[currentEntryFeature]
    return treeTraverse(currentLeaf ?: return null, it)
}

private fun List<DatasetEntry>.extractClasses(): List<Float> = this.map { it.grade }

private fun List<DatasetEntry>.extractFeature(index: Int): List<Float> = this.map { it.features[index] }
