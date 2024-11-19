package io.github.zerumi.core

import kotlin.math.log2

fun entropy(t: List<Pair<*, *>>) = -t.groupingBy { it }.eachCount().map {
    it.value.toFloat() / t.size * log2(it.value.toFloat() / t.size)
}.sum()

fun conditionalEntropy(t: List<Pair<*, *>>) = t.groupBy { it.first }.map {
    it.value.size / t.size.toFloat() * entropy(it.value)
}.sum()

data class DecisionTreeNode<T>(
    var feature: T?,
    val children: HashMap<Any?, DecisionTreeNode<T>>,
    val `class`: Any?
)

interface DTDataset<T> {
    fun getClassVector(): List<*>
    fun getFeatureDescriptionVector(): List<T>
    fun getFeatureVectors(): List<List<*>>
}

fun <T> buildTree(dataset: DTDataset<T>): DecisionTreeNode<T> {
    val root = DecisionTreeNode<T>(
        feature = null,
        children = hashMapOf(),
        `class` = if (dataset.getFeatureVectors().isEmpty())
            dataset.getClassVector().groupingBy { it }.eachCount().maxBy { it.value }.key
        else null
    )

    if (root.`class` != null) return root

    val featToEntropy = dataset.getFeatureVectors().map {
        it to conditionalEntropy(it.zip(dataset.getClassVector()))
    }.maxBy { it.second }.first

    root.feature = dataset.getFeatureDescriptionVector()[dataset.getFeatureVectors().indexOf(featToEntropy)]

    val otherFeats = dataset.getFeatureVectors().toMutableList()
    otherFeats.remove(featToEntropy)

    val featToIndexes = featToEntropy.withIndex().groupBy({ it.value }, { it.index })

    for (feat in featToIndexes) {
        val indexes = feat.value

        val otherFeatsToFeat = otherFeats.map { of -> indexes.map { of[it] } }

        val newDataset = object : DTDataset<T> {
            override fun getFeatureDescriptionVector(): List<T> =
                dataset.getFeatureDescriptionVector().minus(root.feature!!)

            override fun getClassVector(): List<*> = indexes.map { dataset.getClassVector()[it] }

            override fun getFeatureVectors(): List<List<*>> = otherFeatsToFeat
        }

        root.children[feat.key] = buildTree(newDataset)
    }

    return root
}
