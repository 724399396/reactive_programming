package week4

import akka.actor.{Props, Status, Actor}
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import akka.pattern.pipe

/**
  * Created by weili on 16-1-14.
  */
class Getter(url: String, depth: Int) extends Actor {
  import Getter._

  implicit val exec = context.dispatcher

  def client: WebClient = AsyncWebClient

  client get url pipeTo self

  def receive = {
    case body: String =>
      for (link <- findLinks(body))
        context.parent ! Controller.Check(link, depth)
      stop()
    case _: Status.Failure => stop()
    case Abort => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }

  def findLinks(body: String): Iterator[String] = {
    val document = Jsoup.parse(body, url)
    val links = document.select("a[href]")
    for {
      link <- links.iterator().asScala
    } yield link.absUrl("href")
  }

  def fakeGetter(url: String, depth: Int): Props =
    Props(new Getter(url, depth) {
       override def client = FakeWebClient
    })
}

object Getter {
  case object Done
  case object Abort
}
