package models

data class Edge (
    val source: String,
    val destination: String,
    val weight: Int = 1
)