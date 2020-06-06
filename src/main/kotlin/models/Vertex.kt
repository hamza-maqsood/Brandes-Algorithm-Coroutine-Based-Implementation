package models

data class Vertex(
    val id: String,
    val predecessorsList: MutableList<Vertex> = mutableListOf(),
    var betweennessCentralityValue: Double = 0.toDouble(),
    var sigmaValue: Double = 0.toDouble(),
    var deltaValue: Double = 0.toDouble(),
    var distance: Double = 0.toDouble(),
    var isVisited: Boolean = false
)