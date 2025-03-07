
//TODO: review this
def medianIndex(arr: Array[Int], start: Int, stop: Int): Int = {
  println(s"start$start stop$stop")
  val x = arr(start)
  
  val m = (start + stop) / 2
  val y = arr(m)
  
  val z = arr(stop)
  
  if ((x - y) * (z - x) >= 0) // x >= y and x <= z OR x <= y and x >= z
    start
  else if ((y - x) * (z - y) >= 0) // y >= x and y <= z OR y <= x and y >= z
    m
  else
    stop
}

def swap(xs: Array[Int], p1: Int, p2: Int): Unit =
  xs(p1) = xs(p1) + xs(p2)
  xs(p2) = xs(p1) - xs(p2)
  xs(p1) = xs(p1) - xs(p2)
  
def quickSelect(xs: Array[Int], k: Int): Int =
  def go(start: Int, end: Int, position: Int): Int =
    if (start > end) then xs(start) //TODO: Q: why? is it correct?
    else {
        val pivotIndex = medianIndex(xs, start, end)
        val pivot = xs(pivotIndex)
        swap(xs, pivotIndex, end)

        var i = 0 
        var j = end - 1 
        //FIXME: Q: do we need to check if j < 0 or i > end ?
        //
        while(i <= j){
          if (xs(i) >= pivot && xs(j) <= pivot) then {
            ???
          } else if (xs(i) < pivot) {
            i = i + 1
          } else {
            j = j - 1
          }
        }
        swap(xs, i, end)
        val `|L|` = i - start
        val `|G|`  = end - i  //TODO: double check

        if (k > `|L|` && k <= `|L|` + 1){ //TODO: @: why + 1 ?
          pivot
        } else if (k <= `|L|`){
          go(start, pivotIndex - 1, k)
        } else {
          go(pivotIndex + 1, end, k - `|L|` - 1)
        }
    }

  go(0, xs.size - 1, k)


assert(quickSelect(Array(2,1), 1) == 1)
assert(quickSelect(Array(1,2), 1) == 1)
println(quickSelect(Array(1,2, 3, 5, 7), 2))
