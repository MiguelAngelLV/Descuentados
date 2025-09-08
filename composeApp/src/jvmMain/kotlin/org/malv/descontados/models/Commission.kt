package org.malv.descontados.models

data class Commission(
    val commission: Double,
    val year: Int,
    val month: Int,
    val diff: Double,
    val orders: Int,
    val items: Int,
)
