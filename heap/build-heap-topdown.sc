// which case we use this? A: when n (size of elements) is undefined
class MaxHeap(size: Int) {
  private val storage =  Array.fill(size)(-1)
  private var lastPosition: Int = 1 // 1 is root, 2 is left child of root, 3 is right child of root and so on...

  def isValid: Boolean =
    (2 to lastPosition).forall(k =>
        storage.lift(k - 1).forall(value => value < storage(k/2 - 1))
    )

  def show(): Unit = {
    println(s"${storage.mkString(" ")}")
  }

  def top: Int = storage(0)

  private def swap(a: Int, b: Int): Unit = {
    storage(a) = storage(a) + storage(b)
    storage(b) = storage(a) - storage(b)
    storage(a) = storage(a) - storage(b)
  }

  private def upHeap(k: Int): Unit = {
    if (k > 1){ // mean node k has a parent
      val index = k - 1
      val parentIndex = k/2 - 1
      if (storage(parentIndex) < storage(index)){
        swap(parentIndex, index)
        upHeap(k/2)
      }
    }
  }

  def insert(value : Int): Unit = {
    println(s"insert $value")
    storage(lastPosition - 1) = value
    upHeap(lastPosition)
    lastPosition = lastPosition + 1 // extend the array as we just added 1 element to Complete Binary Tree
  }
}

val h1 = new MaxHeap(50)
val xs = Array(56, 39, 14, 18, 75)
h1.show()
for {x <- xs} yield h1.insert(x)
h1.show()
assert(h1.isValid)
assert(h1.top == 75)
