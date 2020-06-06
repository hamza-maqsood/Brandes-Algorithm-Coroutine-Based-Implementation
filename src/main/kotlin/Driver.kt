import algos.BrandesAlgorithm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.Edge
import models.Graph
import models.Vertex
import java.io.File
import java.io.FileNotFoundException

fun main() {
    println("Starting Brandes Algorithm")
    val filename = "facebook_combined.txt"
    try {
        val lines: List<String> = with(filename) {
            return@with File("src/main/kotlin/$this").bufferedReader().readLines()
        }
        val map = mutableMapOf<String, MutableList<String>>()
        // construct the network between vertices
        for (line in lines) {
            val (source: String, destination: String) = line.split(" ", limit = 2)
            map[source] = (map[source] ?: mutableListOf()).apply { add(destination) }
        }

        val vertices = mutableListOf<Vertex>()
        val edges = mutableSetOf<Edge>()
        // construct the graph
        map.forEach { (source, destinations) ->
            vertices.add(Vertex(id = source))
            for (destination in destinations)
                edges.add(Edge(source, destination))
        }
        val graph = Graph(vertices = vertices, edges = edges.toList())
        // here GlobalScope is used but you can change the scope here according to the needs
        GlobalScope.launch {
            BrandesAlgorithm.executeBrandesAlgorithm(graph)
        }
        println("Finished Processing...")
        println("Save to a file or print? (save/print)")
        val input = readLine()
        input?.let {
            when (it.toLowerCase()) {
                "save" -> {
                    File("results.txt").bufferedWriter().use { out ->
                        for (vertex in vertices)
                            out.write("vertex: ${vertex.id} -> BC: ${vertex.betweennessCentralityValue}\n")
                    }
                    println("saved!")
                }
                "print" -> {
                    for (vertex in vertices)
                        println("vertex: ${vertex.id} -> BC: ${vertex.betweennessCentralityValue}")
                }
            }
        }
    } catch (e: FileNotFoundException) {
        println("$filename not found in the present directory!")
    } catch (e: Exception) {
        println("It seems that $filename is not correctly parsed!: $e")
    }
}
