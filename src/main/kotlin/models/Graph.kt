package models

data class Graph (
    val vertices: MutableList<Vertex>,
    val edges: List<Edge>
) {
    fun getNeighbours(vertexID: String) : List<Vertex> {
        val targets = mutableSetOf<String>().apply {
            for (e in edges)
                if (e.source == vertexID)
                    add(e.destination)
        }
        return mutableSetOf<Vertex>().apply {
            vertices.forEach {
                if (targets.contains(it.id))
                    add(it)
            }
        }.toList()
    }
}