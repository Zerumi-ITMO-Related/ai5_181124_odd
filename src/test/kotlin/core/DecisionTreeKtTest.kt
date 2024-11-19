package core

import io.github.zerumi.core.DTDataset
import io.github.zerumi.core.buildTree
import io.github.zerumi.core.conditionalEntropy
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DecisionTreeKtTest {

    enum class Outlook {
        SUNNY, OVERCAST, RAIN
    }

    enum class Class {
        N, P
    }

    private val testData = listOf(
        Outlook.SUNNY to Class.N,
        Outlook.SUNNY to Class.N,
        Outlook.OVERCAST to Class.P,
        Outlook.RAIN to Class.P,
        Outlook.RAIN to Class.P,
        Outlook.RAIN to Class.N,
        Outlook.OVERCAST to Class.P,
        Outlook.SUNNY to Class.N,
        Outlook.SUNNY to Class.P,
        Outlook.RAIN to Class.P,
        Outlook.SUNNY to Class.P,
        Outlook.OVERCAST to Class.P,
        Outlook.OVERCAST to Class.P,
        Outlook.RAIN to Class.N,
    )

    @Test
    fun conditionalEntropy() {
        check(testData.size == 14)

        assertEquals(0.693536f, conditionalEntropy(testData), 1e-6f)
    }

    @Test
    fun buildTreeTest() {
        val dataset = object : DTDataset<String> {
            override fun getFeatureDescriptionVector(): List<String> = listOf("Outfit")

            override fun getClassVector(): List<*> = testData.map { it.second }

            override fun getFeatureVectors(): List<List<*>> = listOf(testData.map { it.first })
        }

        buildTree(dataset)
    }
}
