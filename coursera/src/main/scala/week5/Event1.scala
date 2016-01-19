package week5

import akka.actor.{ActorPath, ActorRef}
import akka.persistence.{AtLeastOnceDelivery, PersistentActor}

/**
  * Created by weili on 16-1-19.
  */
object Event1 {

}

case class NewPost(text: String ,id: Long)
case class BlogPosted(id: Long)
case class BlogNotPosted(id: Long, reason: String)

sealed trait Event
case class PostCreated(text: String) extends Event
case object QuotaReached extends Event

case class State(posts: Vector[String], disabled: Boolean) {
  def update(e: Event): State = {
    case PostCreated(text) => copy(posts = posts :+ text)
    case QuotaReached => copy(disabled = true)
  }
}

case class PostPublished(text: String, id: Long)

class UserProcessor(publisher: ActorPath)
      extends PersistentActor with AtLeastOnceDelivery {
  var state = State(Vector.empty, false)

  def receiveCommand = {
    case NewPost(text,id) =>
      if (state.disabled) sender ! BlogNotPosted(id, "quota reached")
      else {
        persist(PostCreated(text)) { e =>
          deliver(publisher)(PostPublished(text, _))
          sender ! BlogPosted(id)
        }
      }
    case PostPublished(id: Long) => confirmDelivery(id)
  }

  def receiveRecover = {
    case e: Event => updateState(e)
  }

  override def persistenceId: String = ???

  def updateState(e: Event) = {
    case PostCreated(text) => deliver(publisher)(PostPublished(text,_))
    case PostPublished(id: Long) => confirmDelivery(id)
  }
}

class Publisher extends PersistentActor {
  var expectedId: Long = 0
  def receiveCommand = {
    case PostPublished(text,id) =>
      if (id > expectedId) () // ignore, not yet ready for that
      else if (id < expectedId) sender ! PostPublished(_,id)
      else {
        persist(PostPublished(_,id)) {e =>
            sender ! e
            expectedId += 1
        }
      }
  }

  override def persistenceId: String = ???

  def receiveRecover = {
    case PostPublished(_, id: Long) => expectedId = id + 1
  }
}