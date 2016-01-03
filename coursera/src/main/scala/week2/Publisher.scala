package week2

/**
  * Created by liwei on 2016/1/3.
  */
trait Publisher {
  private var subsribers: Set[Subscribe] = Set()

  def subscribe(sub: Subscribe): Unit = {
    subsribers += sub
  }

  def unsubscribe(sub: Subscribe): Unit = {
    subsribers -= sub
  }

  def publish(): Unit = {
    subsribers foreach(_.handle(this))
  }
}
