package com.miu.redblacktreevisualization.core

import org.scalatest.funsuite.AnyFunSuite
import com.miu.redblacktreevisualization.core.BST.Color
import com.miu.redblacktreevisualization.core.BST.Violation

class BSTSuite extends AnyFunSuite {

  test("Node with.Empty parent is root") {
    //LOL: this test case is redudandant after I create more smarter constructor 
    val tree2 = BST.root(10)
    assert(tree2.isRoot)
  }

  test("Find node"){
    lazy val g = BST.root(5).updatedRight(p)
    lazy val p: BST.Node = BST.Node(None, 17, Color.Red, BST.Empty(p), c)
    lazy val c: BST.Node = BST.leaf(p, 42)
    assert(g.findNode(42).contains(c))
  }

  test("Violation : No red uncle straight GPC"){
    // Create tree manually, usually we don't do this, we insert/delete instead
    lazy val g = BST.root(5).updatedRight(p)
    lazy val p: BST.Node = BST.Node(None, 17, Color.Red, BST.Empty(p), c)
    lazy val c: BST.Node = BST.leaf(p, 42)

    val expectedViolation = Violation.StraightGPC(c)
    assert(g.violation(42).contains(expectedViolation))
  }

  test("Violation : No red uncle bended GPC"){
    lazy val g = BST.root(5).updatedRight(p)
    lazy val p: BST.Node = BST.Node(None, 17, Color.Red,c, BST.Empty(p))
    lazy val c: BST.Node = BST.leaf(p, 10)
    val expectedViolation = Violation.BendedGPC(c)
    assert(g.violation(10).contains(expectedViolation))
  }

  test("Resolve violation: Red uncle"){
    val lastInsertedNode = 50
    val tree = BST.root(42).insert(17).insert(47).insert(lastInsertedNode)
    val violation = tree.violation(lastInsertedNode)
    assert(violation.nonEmpty)
    assert(tree.resolve(violation.get).violation(lastInsertedNode).isEmpty)
  }

}
