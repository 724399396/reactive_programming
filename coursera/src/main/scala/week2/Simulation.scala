package week2

/**
  * Created by liwei on 2016/1/3.
  */
trait Simulation {
  type Action = () => Unit
  case class Event(time: Int, action: Action)

  private var curTime: Int = 0
  def getCurTime() = curTime

  type Agenda = List[Event]
  private var agenda: Agenda = List()

  private def loop(): Unit = agenda match {
    case action :: rest =>
      agenda = rest
      curTime = action.time
      action.action()
      loop()
    case Nil =>
  }

  private def insert(e: Event, eventList: Agenda): Agenda = eventList match {
    case x :: xs =>
      if (e.time < x.time) e :: eventList else x :: insert(e, eventList)
    case Nil => List(e)
  }

  def afterDelay(delay: Int)(action: => Unit): Unit = {
    val e = Event(curTime + delay, () => action)
    agenda = insert(e, agenda)
  }

  def run(): Unit = {
    afterDelay(0) {
      println(s"Simulated started at $curTime")
    }
    loop()
  }
}
