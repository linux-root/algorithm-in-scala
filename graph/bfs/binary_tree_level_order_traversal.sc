 class TreeNode(_value: Int = 0, _left: TreeNode = null, _right: TreeNode = null) {
   var value: Int = _value
   var left: TreeNode = _left
   var right: TreeNode = _right
 }

object Solution {

    def traversal(node: TreeNode,
                  queue: List[TreeNode],
                  visisted: Set[Int],
                  result: List[List[Int]]): List[List[Int]] =

      if node == null && queue.isEmpty then result
          else if node == null then {
            //TODO: remove queue.last from queue, adding all it's unvisited child to queue and 
            // mark this visisted + addding it to result
            // adding all to this layer before moving on
            ???
          } else {
            // create layer and move on
            println("start")
            Nil
          }

    def levelOrder(root: TreeNode): List[List[Int]] = {
      traversal(root, List.empty, Set.empty, List.empty)
    }
}
