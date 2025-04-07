import scala.collection.mutable.PriorityQueue
import scala.collection.mutable.Set

object Solution {

    def dfs(edges: Array[Array[Int]], n: Int, start: Int): Array[Option[Int]] = {
        val order:  Ordering[(Int, Int)] = Ordering.by(_._2)
        val distances = Array.fill(n)(Option.empty[Int])
        val visisted  = Set(start)
        while(visisted.size < n){

        }
        distances
    }

    def networkDelayTime(times: Array[Array[Int]], n: Int, k: Int): Int = {
      val distances = dfs(times, n, k)
      if distances.exists(_.isEmpty) then -1 else distances.map(_.get).maxOption.getOrElse(-1)
    }
}


val input1 = Array(
  Array(2,1,1),
  Array(2,3,1),
  Array(3,4,1)
)

val input2 = Array(
    Array(1,2,1)
) 

val input3 = Array(
  Array(1,2,1),
  Array(2,1,3)
)


val input4 = Array(
     Array(1,2,1), Array(2,3,2), Array(1,3,4)
  )


// println(Solution.networkDelayTime(input1, 4, 2))
// println(Solution.networkDelayTime(input2, 2, 1))
// println(Solution.networkDelayTime(input2, 2, 2))
// println(Solution.networkDelayTime(input3, 2, 2))
println(Solution.networkDelayTime(input4, 3, 1))
