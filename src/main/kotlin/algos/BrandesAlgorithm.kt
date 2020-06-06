package algos

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import models.Graph
import models.Vertex
import java.util.*

/**
 * This implementation is more or less the same as specified in the
 * original Brandes 2001 paper.
 */
object BrandesAlgorithm{

    suspend fun executeBrandesAlgorithm(graph: Graph) {

        val mutex = Mutex()
        // for all the vertices, make the BC value zero
        graph.vertices.forEach { vertex ->
            vertex.betweennessCentralityValue = 0.toDouble()
        }


        (0 until graph.vertices.size).map {
            coroutineScope {
                async(Dispatchers.IO) {
                    val s = graph.vertices[it]
                    val stack: Stack<Vertex> = Stack()
                    graph.vertices.forEach { t ->
                        t.sigmaValue = 0.toDouble()
                        t.distance = (-1).toDouble()
                        t.predecessorsList.clear()
                    }
                    s.sigmaValue = 1.toDouble()
                    s.distance = 0.toDouble()
                    val queue: Queue<Vertex> = LinkedList()
                    queue.offer(s)
                    while (queue.isNotEmpty()) {
                        val v = queue.remove()
                        stack.push(v)
                        for (w in graph.getNeighbours(v.id)) {
                            // check if w is found for the first time
                            if (w.distance < 0) {
                                queue.offer(w)
                                w.distance = v.distance + 1
                            }
                            // shortest path to w via v?
                            if (w.distance == v.distance + 1) {
                                w.sigmaValue += v.sigmaValue
                                w.predecessorsList.add(v)
                            }
                        }
                    }
                    for (v in graph.vertices) v.deltaValue = 0.toDouble()
                    // stack S returns vertices in order of non-increasing distance form s
                    while (stack.isNotEmpty()) {
                        val w = stack.pop()
                        w.predecessorsList.forEach { v ->
                            v.deltaValue += (v.sigmaValue / w.sigmaValue) * (1.toDouble() + w.deltaValue)
                        }
                        // protect each increment with lock
                        if (w != s) mutex.withLock { w.betweennessCentralityValue += w.deltaValue }
                    }
                }
            }
        }.awaitAll()
        graph.vertices.forEach { vertex ->
            vertex.betweennessCentralityValue /= 2
        }
    }
}