package week2

/**
  * Created by liwei on 2016/1/3.
  */
trait Subscribe {
  def handle(pub: Publisher)
}
