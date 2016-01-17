package week4

import akka.actor.Actor

/**
  * Created by weili on 16-1-15.
  */
class Toggle extends Actor {
  def happy: Receive = {
    case "How are you?" =>
      sender ! "happy"
      context become sad
  }

  def sad: Receive = {
    case "How are you?" =>
      sender ! "sad"
      context become happy
  }

  def receive = happy
}
