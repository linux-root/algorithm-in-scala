package com.miu.redblacktreevisualization.core

import com.miu.redblacktreevisualization.core.BST.*
import com.miu.redblacktreevisualization.core.BST.Violation.StraightGPC
import com.miu.redblacktreevisualization.core.BST.Violation.BendedGPC

sealed trait BST {
  def updatedLeft(left : => BST): BST

  def updatedRight(right : => BST): BST

  def updatedParent(parent : => Node): BST

  def parent: Option[Node]

  def findNode(value: Int): Option[Node]

  def color: Color

  def isRoot: Boolean = parent.isEmpty

  def insert(value: Int): BST //NOTE: because insert use updateLeft/updateRight, then return type is forced to be BST
  
  def resolve(violation: Violation): BST = 
    violation match {
      case Violation.RedRoot(Node(parent, value, color, left, right)) =>
        //TODO: update parent color
        // update current node color to black
        lazy val node: Node = Node(parent, value, Color.Black, left.updatedParent(node), right.updatedParent(node))
        node
  
      case _ => 
        //TODO: implement
        this
    }
  
  def violation(lastInsertedValue: Int): Option[Violation] = {
    // Find the last inserted node,
    // check if it's parent is red, if it doesn't have a parent, return RedRoot
    // if parent is red, check if it's uncle is red
    // if it's uncle is red, return RedUncle
    //
    def getUncle(node: Node): Option[Node] = node.parent match
      case None => None
      case Some(parent) => parent.parent match
        case None => None
        case Some(grandparent) =>
          if grandparent.left == parent then grandparent.right match
            case uncle @ Node(_, _, _, _, _) => Some(uncle)
            case Empty(_) => None
          else grandparent.left match
            case uncle @ Node(_, _, _, _, _) => Some(uncle)
            case Empty(_) => None

    // NOTE: we assume that lastInsertedValue is RED
    findNode(lastInsertedValue) match
      case None => None
      case Some(node) =>
        // Check for RedRoot violation
        if node.parent.isEmpty && node.color == Color.Red then
          Some(Violation.RedRoot(node))
        else node.parent match
          case None => None
          case Some(parent) if parent.color == Color.Red =>
            // Check for RedUncle violation
            getUncle(node) match
              case Some(uncle) if uncle.color == Color.Red =>
                Some(Violation.RedUncle(node))
              case _ =>
                // Check for StraightGPC violation
                parent.parent match {
                  case Some(grandparent) if parent == grandparent.left =>
                    if (node == parent.left) then Some(StraightGPC(node)) else Some(BendedGPC(node))
                  case Some(grandparent) if parent == grandparent.right =>
                    if (node == parent.right) then Some(StraightGPC(node)) else Some(BendedGPC(node))
                  case _ =>
                    None
                }
          case _ => None
  }
  
  def isValid: Boolean =
    def checkBlackHeight(node: BST): Option[Int] =
      node match
        case Node(_, _, color, left, right) =>
          // Check if both children have same black height
          (checkBlackHeight(left), checkBlackHeight(right)) match
            case (Some(lh), Some(rh)) if lh == rh =>
              // If node is red, both children must be black
              if color == Color.Red then
                (left, right) match
                  case (Node(_, _, Color.Black, _, _), Node(_, _, Color.Black, _, _)) => Some(lh)
                  case (Empty, Empty) => Some(lh)
                  case _ => None
              else Some(lh + 1)  // Count black node
            case _ => None
        case _ => Some(1)
  
    // Root must be black
    this match
      case Empty(_) => true
      case Node(_, _, Color.Black, _, _) => checkBlackHeight(this).isDefined
      case _ => false
  
}


object BST {

    def leaf(parent: Node, value: Int, color: Color = Color.Red): Node =
      lazy val result: Node = Node(Some(parent), value, color, BST.Empty(result), BST.Empty(result))
      result

    def root(value: Int): Node =
      lazy val result: Node = Node(None, value, Color.Black, BST.Empty(result), BST.Empty(result))
      result

    enum Color {
      case Red, Black
    }

    enum Violation {
      def node: Node
      case RedUncle(node: Node)
      case StraightGPC(node: Node)
      case BendedGPC(node: Node)
      case RedRoot(node: Node)
    }
    case class Empty(private val _parent: Node) extends BST {

      override def parent: Option[Node] = Some(_parent)

      override def findNode(value: Int): Option[Node] = None

      override def updatedParent(parent: => Node): BST = Empty(parent) 

      override def color: Color = Color.Red
      override def updatedLeft(left : => BST): BST = this
      override def updatedRight(right : => BST): BST = this
      override def insert(value: Int): BST = 
        parent match {
          case None =>
            BST.root(value)
          case Some(p) =>
            BST.leaf(p, value) // NOTE: link the leaf directly to parent of empty
        }
      override def toString: String = "NIL"
    }
    
    class Node(p: => Option[Node], val value: Int, override val color: Color, l: => BST, r: => BST) extends BST {
      lazy val parent: Option[Node] = p // NOTE: lazy to avoid stack overflow

      lazy val left: BST = l // NOTE: lazy to avoid stack overflow

      lazy val right: BST = r // NOTE: lazy to avoid stack overflow


      override def equals(that: Any): Boolean = that match {
        case sameType: Node =>
          sameType.value == value //TODO: is this enough?
        case _ => false
      }

      override def findNode(v: Int): Option[Node] = 
        if v == value then Some(this)
        else if v < value then left.findNode(v)
        else right.findNode(v)

      override def updatedParent(newParent: => Node): BST = {
        lazy val result: Node = new Node(Some(newParent), value, color, left.updatedParent(result), right.updatedParent(result))
        result
      }

      def updatedLeft(newLeft : => BST): BST = {
        lazy val result: Node = new Node(parent, value, color, newLeft.updatedParent(result), right.updatedParent(result))
        result
     }

      def updatedRight(newRight : => BST): BST = {
        lazy val result: Node = new Node(parent, value, color, left.updatedParent(result), newRight.updatedParent(result))
        result
      }

      def insert(newValue: Int): BST = {
         if newValue == value then this
            else if newValue < value then updatedLeft(left.insert(newValue))
            else updatedRight(right.insert(newValue))
      }

      private def toStringWithIndent(indent: String): String = {
        val nodeStr = s"${color.toString.toLowerCase}($value)"
        val leftStr = left match
          case Empty(_) => s"\n${indent}  └─ NIL"
          case node: Node => s"\n${indent}  └─ ${node.toStringWithIndent(indent + "  ")}"
        val rightStr = right match
          case Empty(_) => s"\n${indent}  └─ NIL"
          case node: Node => s"\n${indent}  └─ ${node.toStringWithIndent(indent + "  ")}"
        s"$nodeStr$leftStr$rightStr"
      }

      override def toString: String = toStringWithIndent("")
    }

    object Node {
      def unapply(node: Node): Option[(Option[Node], Int, Color, BST, BST)] = {
        Some((node.parent, node.value, node.color, node.left, node.right))
      }
    }


}
