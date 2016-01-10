import rx.lang.scala.Observable
import scala.concurrent.duration._
import scala.language.postfixOps

object Main {

  def main(args: Array[String]): Unit = {
    val tickets: Observable[Long] = Observable.interval(1 seconds)

    val events = tickets.filter(_ % 2 == 0)

    val buf = events.slidingBuffer(2, 1)

    val s = buf.subscribe(println(_))

    readLine()

    s.unsubscribe()
  }
}