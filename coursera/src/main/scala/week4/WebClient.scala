package week4

import java.util.concurrent.Executor

import akka.actor.Props
import com.ning.http.client.AsyncHttpClient
import week4.AsyncWebClient.BadStatus

import scala.concurrent.{Future, Promise}


/**
  * Created by weili on 16-1-14.
  */
class WebClient {
  def get(url: String)(implicit executor: Executor): Future[String] = Future.failed(new IllegalArgumentException)
}

object AsyncWebClient extends WebClient {

  val client = new AsyncHttpClient()

  override def get(url: String)(implicit executor: Executor): Future[String] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[String]()
    f.addListener(new Runnable {
      def run() = {
        val res = f.get()
        if (res.getStatusCode < 400)
          p.success(res.getResponseBodyExcerpt(131072))
        else
          p.failure(BadStatus(res.getStatusCode))
      }
    }, executor)
    p.future
  }

  def shutdown(): Unit = {
    client.close()
  }

  case class BadStatus(code: Int) extends Throwable
}

object FakeWebClient extends WebClient {
  val firstLink = "http://www.rkuhn.info/1"

  val bodies = Map(
    firstLink ->
      """<html>
        |  <head><title>Page 1</title></head>
        |  <body>
        |    <h1>A Link</h1>
        |    <a href = "http://wkuhn.info/2">click here</a>
        |  </body>
        |<html>
      """.stripMargin)

  val links = Map(firstLink -> Seq("http://rkuhn.info/2"))

    override def get(url: String)(implicit exec: Executor): Future[String] =
      bodies get url match {
        case None => Future.failed(BadStatus(404))
        case Some(body) => Future.successful(body)
      }
}


