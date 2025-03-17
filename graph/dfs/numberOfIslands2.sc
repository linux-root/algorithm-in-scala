def numIslands(grid: Array[Array[Char]]): Int = {
    var totalConnectedComponents = 0
    for {
      y <- 0 until grid.size
      x <- 0 until grid(0).size if grid(y)(x) == '1'
    } yield {
      println(s"start: ($x, $y)")
      val start = (x, y)
      dfs(start, grid)
      totalConnectedComponents = totalConnectedComponents + 1
    }
    totalConnectedComponents
}

def dfs(start: (Int, Int), grid: Array[Array[Char]]): Unit =
  def go(vertex: (Int, Int), stack: List[(Int, Int)]): Unit  =
     val (x, y) = vertex
     grid(y)(x) = 'X'
     val rightAdjacency = grid(y).lift(x + 1).filter(_ == '1').map(_ => (x + 1, y))
     val leftAdjacency = grid(y).lift(x - 1).filter(_ == '1').map(_ => (x - 1, y))
     val bottomAdjacency = grid.lift(y + 1).map(_.apply(x)).filter(_ == '1').map(_ => (x, y + 1))
     val topAdjacency = grid.lift(y - 1).map(_.apply(x)).filter(_ == '1').map(_ => (x, y - 1))
     val adjacencies = List(rightAdjacency, leftAdjacency, topAdjacency, bottomAdjacency)
       .collect{case Some(xy) => xy}

     if (adjacencies.isEmpty){
       if stack.isEmpty then () else go(stack.head, stack.tail)
     } else {
       go(adjacencies.head, adjacencies.tail ++ stack)
     }
  go(start, List.empty)


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
