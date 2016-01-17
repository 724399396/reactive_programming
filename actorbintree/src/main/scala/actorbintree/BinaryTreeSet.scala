/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package actorbintree

import actorbintree.BinaryTreeNode.CopyFinished
import akka.actor._
import scala.collection.immutable.Queue

object BinaryTreeSet {

  trait Operation {
    def requester: ActorRef
    def id: Int
    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /** Request with identifier `id` to insert an element `elem` into the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to check whether an element `elem` is present
    * in the tree. The actor at reference `requester` should be notified when
    * this operation is completed.
    */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to remove the element `elem` from the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection*/
  case object GC

  /** Holds the answer to the Contains request with identifier `id`.
    * `result` is true if and only if the element is present in the tree.
    */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply
  
  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

  case object CleanJob
}


class BinaryTreeSet extends Actor {
  import BinaryTreeSet._
  import BinaryTreeNode._

  def createRoot: ActorRef = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))

  var root = createRoot

  // optional
  var pendingQueue = Queue.empty[Operation]

  // optional
  def receive = running

  val running: Receive = {
    case GC =>
      val newRoot = createRoot
      root ! CopyTo(newRoot)
      context become gc(newRoot)
    case msg: Operation =>
      if (pendingQueue.isEmpty)
        root ! msg
      else
        pendingQueue = pendingQueue enqueue msg
    case CleanJob =>
      pendingQueue.foreach(root ! _)
      pendingQueue = Queue.empty
  }

  def gc(newRoot: ActorRef): Receive = {
    case BinaryTreeNode.CopyFinished =>
      context stop root
      root = newRoot
      context become running
      self ! CleanJob
    case msg: Operation =>
      pendingQueue = pendingQueue enqueue msg
  }


  // optional
  /** Handles messages while garbage collection is performed.
    * `newRoot` is the root of the new binary tree where we want to copy
    * all non-removed elements into.
    */
  def garbageCollecting(newRoot: ActorRef): Receive = ???

}

object BinaryTreeNode {
  trait Position

  case object Left extends Position
  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean) = Props(classOf[BinaryTreeNode],  elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor {
  import BinaryTreeNode._
  import BinaryTreeSet._

  var subtrees = Map[Position, ActorRef]()
  var removed = initiallyRemoved

  // optional
  def receive = normal

  // optional
  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive = {
    case Insert(req,id,ele) =>
      if (elem == ele) {
        removed = false
        req ! OperationFinished(id)
      }
      if (elem < ele) {
        subtrees get Right match {
          case None =>
            subtrees += Right -> context.actorOf(Props(new BinaryTreeNode(ele, false)))
            req ! OperationFinished(id)
          case Some(x) =>
            x ! Insert(req,id,ele)
        }
      }
      if (elem > ele) {
        subtrees get Left match {
          case None =>
            subtrees += Left -> context.actorOf(Props(new BinaryTreeNode(ele, false)))
            req ! OperationFinished(id)
          case Some(x) =>
            x ! Insert(req,id,ele)
        }
      }
    case Remove(req, id, ele) =>
      if (elem == ele) {
        removed = true
        req ! OperationFinished(id)
      }
      if (elem < ele) {
        subtrees get Right match {
          case None =>
            req ! OperationFinished(id)
          case Some(x) =>
            x ! Remove(req,id,ele)
        }
      }
      if (elem > ele) {
        subtrees get Left match {
          case None =>
            req ! OperationFinished(id)
          case Some(x) =>
            x ! Remove(req,id,ele)
        }
      }
    case Contains(req,id,ele) =>
      if (elem == ele) {
        req ! ContainsResult(id, !removed)
      }
      if (elem < ele) {
        subtrees get Right match {
          case None =>
            req ! ContainsResult(id, false)
          case Some(x) =>
            x ! Contains(req,id,ele)
        }
      }
      if (elem > ele) {
        subtrees get Left match {
          case None =>
            req ! ContainsResult(id, false)
          case Some(x) =>
            x ! Contains(req,id,ele)
        }
      }
    case CopyTo(newActor) =>
      val subRef = subtrees.values.toSet
      context become copying(subRef + self, false)
      subRef.foreach(_ ! CopyTo(newActor))
      if (!removed)
        newActor ! Insert(self, -1, elem)
      self ! CopyFinished
  }

  // optional
  /** `expected` is the set of ActorRefs whose replies we are waiting for,
    * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
    */
  def copying(expected: Set[ActorRef], insertConfirmed: Boolean): Receive = {
    case CopyFinished =>
      val newSet = expected - sender
      if (newSet.isEmpty) {
        context.parent ! CopyFinished
        context become normal
      } else {
        context become copying(newSet, false)
      }
  }

}
