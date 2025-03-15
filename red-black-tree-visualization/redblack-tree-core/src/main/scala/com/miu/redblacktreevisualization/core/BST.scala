package com.miu.redblacktreevisualization.core

import com.miu.redblacktreevisualization.core.BST.*
import com.miu.redblacktreevisualization.core.BST.Violation.*

sealed trait BST {

  def updatedColor(newColor: Color): BST

  def updatedLeft(left : => BST): BST

  def updatedRight(right : => BST): BST

  def updatedParent(parent : => Node): BST

  def parent: Option[Node]

  def findNode(value: Int): Option[Node]

  def color: Color

  def isRoot: Boolean = parent.isEmpty

  def root: Node

  def insert(value: Int): BST //NOTE: because insert use updateLeft/updateRight, then return type is forced to be BST
  
  def resolve(violation: Violation): BST = 

    def resolveLStraightGPC(root: Node, grandparent: Node): BST =
      if root == grandparent then {
        grandparent.left match { // boiler-plate. Can improve by re-design BST
          case nodeP: Node =>
            val updatedG = grandparent.updatedColor(Color.Red).updatedLeft(Empty(grandparent)) // Right of G is changed from P to Empty
            val updatedRoot = BST.root(nodeP.value, Some(nodeP.left), Some(updatedG)) // root value became P.value, left of P become G, keep right of P the same
            updatedRoot
          case _ => 
            root
        }
      } else ???

    def resolveRStraightGPC(root: Node, grandparent: Node): BST =
      if root == grandparent then {
        grandparent.right match { // boiler-plate. Can improve by re-design BST
          case nodeP: Node =>
            val updatedG = grandparent.updatedColor(Color.Red).updatedRight(Empty(grandparent)) // Right of G is changed from P to Empty
            val updatedRoot = BST.root(nodeP.value, Some(updatedG), Some(nodeP.right)) // root value became P.value, left of P become G, keep right of P the same
            updatedRoot
          case _ => 
            root
        }
      } else ???

    violation match {
      case Violation.RedRoot(Node(parent, value, color, left, right)) =>
        lazy val node: Node = Node(parent, value, Color.Black, left.updatedParent(node), right.updatedParent(node))
        node
  
      case Violation.RedUncle(node) => 
        // what should we do?
        // change uncle and parent to black
        // change grand parent G to red
        // repeat
        node.parent.flatMap(_.parent) match
          case Some(grandparent) =>
            //TODO: solve case grandparent is not root
            if grandparent.isRoot then
              grandparent
                .updatedRight(grandparent.right.updatedColor(Color.Black))
                .updatedLeft(grandparent.left.updatedColor(Color.Black))
            else this
            
          case _ =>
            this

      case Violation.RStraightGPC(node) =>
        node.parent.flatMap(_.parent) match
           case Some(grandparent) =>
             resolveRStraightGPC(this.root, grandparent)
           case _ =>
             // TODO: no GGP
             this
      case Violation.LStraightGPC(node) =>
        node.parent.flatMap(_.parent) match
           case Some(grandparent) =>
             resolveLStraightGPC(this.root, grandparent)
           case _ =>
             // TODO: no GGP
             this
    }

  def uncle: Option[Node] = 
   this.parent match
      case None => None
      case Some(parent) =>
        parent.parent match
          case None => None
          case Some(grandparent) =>
            if parent == grandparent.left then grandparent.right match
                  case uncle: Node => Some(uncle)
                  case _ => None
            else grandparent.left match
              case uncle: Node => Some(uncle)
              case _ => None
  
  def violation(lastInsertedValue: Int): Option[Violation] = {
    // Find the last inserted node,
    // check if it's parent is red, if it doesn't have a parent, return RedRoot
    // if parent is red, check if it's uncle is red
    // if it's uncle is red, return RedUncle
    //

    // NOTE: we assume that lastInsertedValue is RED
    findNode(lastInsertedValue) match
      case None => None
      case Some(node) =>
        // Check for RedRoot violation
        if node.isRoot && node.color == Color.Red then
          Some(Violation.RedRoot(node))
        else node.parent match
          case None => None
          case Some(parent) if parent.color == Color.Red =>
            // Check for RedUncle violation
           node.uncle match
              case Some(uncle) if uncle.color == Color.Red =>
                Some(Violation.RedUncle(node))
              case _ =>
                // Check for StraightGPC violation
                parent.parent match {
                  case Some(grandparent) if parent == grandparent.left =>
                    if (node == parent.left) then Some(LStraightGPC(node)) else Some(BendedGPC(node))
                  case Some(grandparent) if parent == grandparent.right =>
                    if (node == parent.right) then Some(RStraightGPC(node)) else Some(BendedGPC(node))
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

    def root(value: Int, left: Option[BST] = None, right: Option[BST] = None): Node =
      lazy val result: Node = Node(None, value, Color.Black, left.map(_.updatedParent(result)).getOrElse(BST.Empty(result)), right.map(_.updatedParent(result)).getOrElse(BST.Empty(result)))
      result

    enum Color {
      case Red, Black
    }

    enum Violation {
      override def toString(): String = {
        this match {
          case RedUncle(_) => "RedUncle"
          case LStraightGPC(_) => "LStraightGPC"
          case RStraightGPC(_) => "RStraightGPC"
          case BendedGPC(_) => "BendedGPC"
          case RedRoot(_) => "RedRoot"
        }
      }
      def node: Node
      case RedUncle(node: Node)
      case LStraightGPC(node: Node)
      case RStraightGPC(node: Node)
      case BendedGPC(node: Node)
      case RedRoot(node: Node)
    }
    case class Empty(private val _parent: Node) extends BST {
      override def root: Node =
        _parent.root

      override def updatedColor(newColor: Color): BST = this //Empty can't change its color

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

      override def root: Node =
        parent  match {
          case None =>
            this
          case Some(p) =>
            p.root
        }

      override def findNode(v: Int): Option[Node] = 
        if v == value then Some(this)
        else if v < value then left.findNode(v)
        else right.findNode(v)

      override def updatedParent(newParent: => Node): BST = {
        lazy val result: Node = new Node(Some(newParent), value, color, left.updatedParent(result), right.updatedParent(result))
        result
      }

      override def updatedLeft(newLeft : => BST): BST = {
        lazy val result: Node = new Node(parent, value, color, newLeft.updatedParent(result), right.updatedParent(result))
        result
     }

      override def updatedRight(newRight : => BST): BST = {
        lazy val result: Node = new Node(parent, value, color, left.updatedParent(result), newRight.updatedParent(result))
        result
      }

      def updatedColor(newColor: Color): BST =
        lazy val result: Node = new Node(parent, value, newColor, left.updatedParent(result), right.updatedParent(result))
        result

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
