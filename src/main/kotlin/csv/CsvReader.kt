package io.github.zerumi.csv

import java.io.InputStream

data class DatasetEntry(
    val stId: String,
    val features: List<Float>,
    val cId: Float,
    val grade: Float,
)

data class Dataset(
    val labels: List<String>, val entries: List<DatasetEntry>
)

fun readCsv(inputStream: InputStream): Dataset {
    val reader = inputStream.bufferedReader()
    val header = reader.readLine().split(',')

    return Dataset(header, reader.lineSequence().filter { it.isNotBlank() }.map {
        val dataRow = it.split(',')

        return@map DatasetEntry(
            stId = dataRow.first(),
            features = dataRow.subList(1, 31).map { num -> num.toFloat() },
            cId = dataRow[31].toFloat(),
            grade = dataRow[32].toFloat()
        )
    }.toList())
}

private operator fun <E> List<E>.component6(): E = this[5]

private operator fun <E> List<E>.component7(): E = this[6]

private operator fun <E> List<E>.component8(): E = this[7]

private operator fun <E> List<E>.component9(): E = this[8]
