case class Item(value: Int, weight: Int)
//TODO: knapscak with a single array
def knapsack(a: Array[Item], wmax: Int): Int = 
  def go(i: Int, row: Array[Int]): Int =
    println(s"current: ${row.mkString(" | ")}")
    if i >= a.size then row.last
    else {
      val w = a(i)
      val updatedRow = (1 to wmax).map(weight =>
          val weightIndex = weight - 1 // [0, wmax - 1]
          if weight >= w.weight then { // can accommodate
             val withoutW = row(weightIndex)
             val withW = w.value + row.lift(weightIndex - w.weight).getOrElse(0)
             Math.max(withW, withoutW)
          } else row(weightIndex)
      ).toArray
      println(s"updated: ${updatedRow.mkString(" | ")}")
      go(i + 1, updatedRow)
    }

  go(0, Array.fill(wmax)(0))


// val input = Array(
//   Item(15, 2),
//   Item(12, 5),
//   Item(9, 3),
//   Item(16, 4),
//   Item(17, 6))
// val wmax = 12

// println(knapsack(input, wmax))
//
val input = Array(
  Item(25, 5),
  Item(12, 6),
  Item(24, 8),
  Item(16, 2),
  Item(28, 7))
val wmax = 20

println("max value is: " + knapsack(input, wmax))
