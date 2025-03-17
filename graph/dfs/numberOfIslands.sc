def numIslands(grid: Array[Array[Char]]): Int = {
    // number of island is total connected component in grapth
    // do DFS to find all connected components
    // visisted : set ID: 
    // NOTE : the way we access the array should be grid(y)(x)

    var visisted = Set.empty[Int]
    var totalConnectedComponents = 0
    for {
      y <- 0 until grid.size
      x <- 0 until grid(0).size if grid(y)(x) == '1' && !visisted(pointId(x, y))
    } yield {
      println(s"start: ($x, $y)")
      val start = (x, y)
      visisted = dfs(start, grid, visisted)
      totalConnectedComponents = totalConnectedComponents + 1
    }
    // println(s"grid m: ${grid.size}")
    totalConnectedComponents
}

def pointId(x: Int, y: Int): Int =
  (x+y)*(x + y + 1)/2 + y


def dfs(start: (Int, Int), grid: Array[Array[Char]], visisted: Set[Int]): Set[Int] =
  def go(vertex: (Int, Int), stack: List[(Int, Int)], visistedList: Set[Int]): Set[Int]  =
     val (x, y) = vertex
     val updatedVisistedList = visistedList + pointId(x, y)
     val rightNeighbor = grid(y).lift(x + 1).filter(_ == '1').map(_ => (x + 1, y))
     val leftNeighbor = grid(y).lift(x - 1).filter(_ == '1').map(_ => (x - 1, y))
     val bottomNeighbor = grid.lift(y + 1).map(_.apply(x)).filter(_ == '1').map(_ => (x, y + 1))
     val topNeighbor = grid.lift(y - 1).map(_.apply(x)).filter(_ == '1').map(_ => (x, y - 1))
     // println(s"leftNeighbor $leftNeighbor")
     println(s"right (${x + 1}, $y): $rightNeighbor")
     val neighbors = List(rightNeighbor, bottomNeighbor, leftNeighbor, topNeighbor)
       .collect{case Some((x, y)) if !visistedList.contains(pointId(x, y)) => (x, y)}

     if (neighbors.isEmpty){
       if stack.isEmpty then updatedVisistedList else go(stack.head, stack.tail, updatedVisistedList)
     } else {
       go(neighbors.head, neighbors.tail ++ stack, updatedVisistedList)
     }
  go(start, List.empty, visisted)


val input4: Array[Array[Char]] = Array(
  Array('1', '0', '1', '1', '1'),
  Array('1', '0', '1', '0', '1'),
  Array('1', '1', '1', '0', '1')
)

println(numIslands(input4)) //expected 1




val input1 =  Array(
  Array('1', '1', '1', '1', '0'),
  Array('1', '1', '0', '1', '0'),
  Array('1', '1', '0', '0', '0'),
  Array('0', '0', '0', '0', '0')
)

// println(numIslands(input1))

val input2: Array[Array[Char]] = Array(
  Array('1', '1', '0', '0', '0'),
  Array('1', '1', '0', '0', '0'),
  Array('0', '0', '1', '0', '0'),
  Array('0', '0', '0', '1', '1')
)

// println(numIslands(input2))

val input3: Array[Array[Char]] = Array(
  Array('1', '1', '1'),
  Array('0', '1', '0'),
  Array('1', '1', '1')
)

// println(numIslands(input3))
