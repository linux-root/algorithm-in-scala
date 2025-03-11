package com.miu.redblacktreevisualization.core

import org.scalatest.funsuite.AnyFunSuite
import com.miu.redblacktreevisualization.core.BST.Color
import com.miu.redblacktreevisualization.core.BST.Violation

class BSTSuite extends AnyFunSuite {

  test("Node with empty parent is root") {
    val parent = None
    val tree1 = BST.Empty(parent)
    val tree2 = BST.root(10)
    assert(tree1.isRoot)
    assert(tree2.isRoot)
  }

  test("Find node"){
    lazy val g = BST.root(5).updatedRight(p)
    lazy val p: BST.Node = BST.Node(None, 17, Color.Red, BST.empty(p), c)
    lazy val c: BST.Node = BST.leaf(p, 42)

    println(g.toString())
    assert(g.findNode(42).contains(c))
  }

  test("Violation : No red uncle straight GPC"){
    // Create tree manually, usually we don't do this, we insert/delete instead
    lazy val g = BST.root(5).updatedRight(p)
    lazy val p: BST.Node = BST.Node(None, 17, Color.Red, BST.empty(p), c)
    lazy val c: BST.Node = BST.leaf(p, 42)

    val expectedViolation = Violation.StraightGPC(c)
    assert(g.violation(42).contains(expectedViolation))
  }

    println(g.toString())
    val expectedViolation = Violation.StraightGPC(c)
    assert(g.violation(42).contains(expectedViolation))
  }

}
