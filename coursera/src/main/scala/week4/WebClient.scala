package week4

import java.util.concurrent.Executor

import com.ning.http.client.AsyncHttpClient

import scala.concurrent.{Future, Promise}


/**
  * Created by weili on 16-1-14.
  */
object WebClient {

  val client = new AsyncHttpClient()

  def get(url: String)(implicit executor: Executor): Future[String] = {
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
