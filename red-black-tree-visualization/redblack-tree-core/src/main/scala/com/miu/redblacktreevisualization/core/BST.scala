package com.miu.redblacktreevisualization.core

import com.miu.redblacktreevisualization.core.BST.*
import com.miu.redblacktreevisualization.core.BST.Violation.*
import scala.annotation.threadUnsafe

sealed trait BST {

  def label: Option[String]

  def updatedColor(newColor: Color): BST

  def updatedLeft(left: => BST): BST

  def updatedWhere(key: Int, updater: Node => Node): BST

  def updatedRight(right: => BST): BST

  def updatedParent(parent: => Node): BST

  def parent: Option[Node]

  def findNode(value: Int): Option[Node]

  def color: Color

  def isRoot: Boolean = parent.isEmpty

  def isEmpty: Boolean

  def withLabel(newLabel: String): BST

  def isRed: Boolean = this.color == Color.Red

  def root: Node

  def insert(value: Int): BST // NOTE: because insert use updateLeft/updateRight, then return type is forced to be BST

  def resolve(violation: Violation): BST =

    def resolveLStraightGPC(root: Node, grandparent: Node): BST =
      if root == grandparent then {
        grandparent.left match { // boiler-plate. Can improve by re-design BST
          case nodeP: Node =>
            val updatedG = grandparent.updatedColor(Color.Red).updatedLeft(Empty(grandparent)) // Right of G is changed from P to Empty
            val updatedRoot =
              BST.root(nodeP.value, Some(nodeP.left), Some(updatedG)) // root value became P.value, left of P become G, keep right of P the same
            updatedRoot
          case _ =>
            root
        }
      } else this

    def resolveRStraightGPC(root: Node, grandparent: Node): BST =
      if root == grandparent then {
        grandparent.right match { // boiler-plate. Can improve by re-design BST
          case nodeP: Node =>
            val updatedG = grandparent.updatedColor(Color.Red).updatedRight(Empty(grandparent)) // Right of G is changed from P to Empty
            val updatedRoot =
              BST.root(nodeP.value, Some(updatedG), Some(nodeP.right)) // root value became P.value, left of P become G, keep right of P the same
            updatedRoot
          case _ =>
            root
        }
      } else this

    violation match {

      case Violation.RedRoot(root) =>
        lazy val node: Node = Node(parent, root.value, Color.Black, label, root.left.updatedParent(root), root.right.updatedParent(root))
        node

      case Violation.RedUncle(node) =>
        // what should we do?
        // change uncle and parent to black
        // change grand parent G to red
        // repeat
        node.parent.flatMap(_.parent) match
          case Some(grandparent) =>
            // TODO: solve case grandparent is not root
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
      case  Violation.BendedGPC(node, isLR) =>
        // child take care parent and grandparent
        node.parent.flatMap(_.parent) match {
          case Some(grandparent) =>
            val updateG : Node => Node = gnode => {
              ???
            }
            this
          case _ =>
            this
        }
        this
    }

  def uncle: Option[Node] =
    this.parent match
      case None => None
      case Some(parent) =>
        parent.parent match
          case None => None
          case Some(grandparent) =>
            if parent == grandparent.left then
              grandparent.right match
                case uncle: Node => Some(uncle)
                case _           => None
            else
              grandparent.left match
                case uncle: Node => Some(uncle)
                case _           => None

  def violation(lastInsertedValue: Int): Option[Violation] =
    // Find the last inserted node,
    // check if it's parent is red, if it doesn't have a parent, return RedRoot
    // if parent is red, check if it's uncle is red
    // if it's uncle is red, return RedUncle
    //

    // NOTE: we assume that lastInsertedValue is RED
    findNode(lastInsertedValue) match
      case None       => None
      case Some(node) =>
        // Check for RedRoot violation
        if node.isRoot && node.color == Color.Red then Some(Violation.RedRoot(node))
        else
          node.parent match
            case None                                      => None
            case Some(parent) if parent.color == Color.Red =>
              // Check for RedUncle violation
              node.uncle match
                case Some(uncle) if uncle.color == Color.Red =>
                  Some(Violation.RedUncle(node))
                case _ =>
                  // Check for StraightGPC violation
                  parent.parent match {
                    case Some(grandparent) if parent == grandparent.left =>
                      if (node == parent.left) then Some(LStraightGPC(node)) else Some(BendedGPC(node, isLR = true))
                    case Some(grandparent) if parent == grandparent.right =>
                      if (node == parent.right) then Some(RStraightGPC(node)) else Some(BendedGPC(node, isLR = false))
                    case _ =>
                      None
                  }
            case _ => None

  def isValid: Boolean =
    def checkBlackHeight(node: BST): Option[Int] =
      node match
        case Node(_, _, color, _, left, right) =>
          // Check if both children have same black height
          (checkBlackHeight(left), checkBlackHeight(right)) match
            case (Some(lh), Some(rh)) if lh == rh =>
              // If node is red, both children must be black
              if color == Color.Red then
                (left, right) match
                  case (Node(_, _, Color.Black, _, _, _), Node(_, _, Color.Black, _, _, _)) => Some(lh)
                  case (Empty, Empty)                                                       => Some(lh)
                  case _                                                                    => None
              else Some(lh + 1) // Count black node
            case _ => None
        case _ => Some(1)

    // Root must be black
    this match
      case Empty(_)                         => true
      case Node(_, _, Color.Black, _, _, _) => checkBlackHeight(this).isDefined
      case _                                => false

}

object BST {

  def leaf(parent: Node, value: Int, label: Option[String] = None, color: Color = Color.Red): Node =
    lazy val result: Node = Node(Some(parent), value, color, label, BST.Empty(result), BST.Empty(result))
    result

  def root(value: Int, left: Option[BST] = None, right: Option[BST] = None): Node =
    lazy val result: Node = Node(
      None,
      value,
      Color.Red,
      None,
      left.map(_.updatedParent(result)).getOrElse(BST.Empty(result)),
      right.map(_.updatedParent(result)).getOrElse(BST.Empty(result))
    )
    result

  enum Color {
    case Red, Black
  }

  enum Violation {
    override def toString(): String =
      this match {
        case RedUncle(_)     => "RedUncle"
        case LStraightGPC(_) => "LStraightGPC"
        case RStraightGPC(_) => "RStraightGPC"
        case BendedGPC(_, isLR)    => s"${if isLR then "LR" else "RL"}BendedGPC"
        case RedRoot(_)      => "RedRoot"
      }

    def resolveDetail: String =
      this match {
        case RedUncle(_)     => 
          "Change Parent P, Uncle U to BLACK Change Grand Parent G to RED (Recursively do it until no two consecutive reds)"
        case LStraightGPC(_) => "Grandparent(G) is old, now Parent(P) will take care both Child(C) and Grandparent(G)"
        case RStraightGPC(_) => "Grandparent(G) is old, now Parent(P) will take care both Child(C) and Grandparent(G)"
        case BendedGPC(_, isLR)    => s"Child(C) will take care of its Parent(P) and Grandparent(G)"
        case RedRoot(_)      => "Change root color to Black"
      }
    def node: Node
    case RedUncle(node: Node)
    case LStraightGPC(node: Node)
    case RStraightGPC(node: Node)
    case BendedGPC(node: Node, isLR: Boolean)
    case RedRoot(node: Node)
  }
  case class Empty(private val _parent: Node) extends BST {

    override def updatedWhere(key: Int, updater: Node => Node): BST = this

    override def withLabel(newLabel: String): BST = this

    override def isEmpty: Boolean = true

    override val label: Option[String] = None

    override def root: Node =
      _parent.root

    override def updatedColor(newColor: Color): BST = this // Empty can't change its color

    override def parent: Option[Node] = Some(_parent)

    override def findNode(value: Int): Option[Node] = None

    override def updatedParent(parent: => Node): BST = Empty(parent)

    override def color: Color                     = Color.Red
    override def updatedLeft(left: => BST): BST   = this
    override def updatedRight(right: => BST): BST = this
    override def insert(value: Int): BST =
      parent match {
        case None =>
          BST.root(value)
        case Some(p) =>
          BST.leaf(p, value, p.label.map(_ => "C")) // NOTE: link the leaf directly to parent of empty
      }
    override def toString: String = "NIL"
  }

  class Node(p: => Option[Node], val value: Int, override val color: Color, override val label: Option[String], l: => BST, r: => BST) extends BST {

    override def updatedWhere(key: Int, updater: Node => Node): BST =
      if key == value then updater(this) 
        else if  key < value then updatedLeft(left.updatedWhere(key, updater)) 
            else updatedRight(right.updatedWhere(key, updater))

    override def isEmpty: Boolean = false

    lazy val parent: Option[Node] = p // NOTE: lazy to avoid stack overflow

    lazy val left: BST = l // NOTE: lazy to avoid stack overflow

    lazy val right: BST = r // NOTE: lazy to avoid stack overflow

    override def equals(that: Any): Boolean = that match {
      case sameType: Node =>
        sameType.value == value // TODO: is this enough?
      case _ => false
    }

    override def withLabel(newLabel: String): Node =
      lazy val result: Node = new Node(parent, value, color, Some(newLabel), left.updatedParent(result), right.updatedParent(result))
      result

    override def root: Node =
      parent match {
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
      lazy val result: Node = new Node(Some(newParent), value, color, label, left.updatedParent(result), right.updatedParent(result))
      result
    }

    override def updatedLeft(newLeft: => BST): BST = {
      lazy val result: Node = new Node(parent, value, color, label, newLeft.updatedParent(result), right.updatedParent(result))
      result
    }

    override def updatedRight(newRight: => BST): BST = {
      lazy val result: Node = new Node(parent, value, color, label, left.updatedParent(result), newRight.updatedParent(result))
      result
    }

    def updatedColor(newColor: Color): BST =
      lazy val result: Node = new Node(parent, value, newColor, label, left.updatedParent(result), right.updatedParent(result))
      result

    def insert(newValue: Int): BST =
      if newValue == value then this
      else if newValue < value then
        left match {
          case Leaf(node) if node.isRed =>
            val updated = withLabel("G").updatedLeft(node.withLabel("P").insert(newValue))
            if right.isRed then updated.updatedRight(right.withLabel("U"))
            else updated
          case _ =>
            updatedLeft(left.insert(newValue))
        }
      else
        right match
          case Leaf(node) if node.isRed =>
            val updated = withLabel("G").updatedRight(node.withLabel("P").insert(newValue))
            if left.isRed
            then updated.updatedLeft(left.withLabel("U"))
            else updated
          case _ =>
            updatedRight(right.insert(newValue))

    private def toStringWithIndent(indent: String): String = {
      val nodeStr = s"${color.toString.toLowerCase}($value)"
      val leftStr = left match
        case Empty(_)   => s"\n${indent}  └─ NIL"
        case node: Node => s"\n${indent}  └─ ${node.toStringWithIndent(indent + "  ")}"
      val rightStr = right match
        case Empty(_)   => s"\n${indent}  └─ NIL"
        case node: Node => s"\n${indent}  └─ ${node.toStringWithIndent(indent + "  ")}"
      s"$nodeStr$leftStr$rightStr"
    }

    override def toString: String = toStringWithIndent("")
  }

  object Node {
    def unapply(node: Node): Option[(Option[Node], Int, Color, Option[String], BST, BST)] =
      Some((node.parent, node.value, node.color, node.label, node.left, node.right))
  }

  object Leaf {
    def unapply(node: Node): Option[Node] =
      if node.left.isEmpty && node.right.isEmpty then Some(node) else None
  }

}
