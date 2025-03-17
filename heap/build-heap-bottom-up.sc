
def checkHeap(xs: Array[Int]): Boolean = {
  (xs.size to 2 by -1).forall(k =>
      xs(k - 1) <= xs(k/2 - 1)
  )
}

def buildHeap(xs: Array[Int]): Unit = {

   val n = xs.length

   def swap(a: Int, b: Int): Unit =
     xs(a) = xs(a) + xs(b)
     xs(b) = xs(a) - xs(b)
     xs(a) = xs(a) - xs(b)

   def resolve(k: Int): Unit = {
     val i = k - 1
     if (2*k + 1 <= n) {

       if xs(i) < xs(2*k - 1) then {
         swap(i, 2*k - 1)
         resolve(2*k)
      }
       if xs(i) < xs(2*k) then {
         swap(i, 2*k)
         resolve(2*k + 1)
       }

     } else if (2*k <= n){
       if xs(i) < xs(2*k - 1) then {
         swap(i, 2*k - 1)
         resolve(2*k)
       }
     }
   }

   for {
     k <- n to 1 by -1
   } yield resolve(k)

}

val input=  Array(1, 3, 4, 4, 5, 7, 9, 11, 13, 13, 17)


buildHeap(input)
println(input.mkString(" "))
assert(input(0) == 17)
assert(input(1) == 13)
assert(checkHeap(input))


val input2=  Array(1,  4, 9, 11, 4,  13, 13, 17, 5, 7, 3)
buildHeap(input2)
assert(checkHeap(input2))
