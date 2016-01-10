package tutorial.webapp

import scala.scalajs.js.JSApp
import org.scalajs.dom
import dom.document
import org.scalajs.jquery.jQuery

import scala.scalajs.js.annotation.JSExport

/**
  * Created by weili on 16-1-9.
  */
object TutorialApp extends JSApp {
  def main(): Unit = {
//    appendPar(document.body, "Hello World")
//    appendPar2("Hello jQuery")
    jQuery(setupUI _)
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  def appendPar2(message: String): Unit = {
    jQuery("body").append("<p>[message]</p>")
  }

  @JSExport
  def addClickedMessage(): Unit = {
    appendPar(document.body, "You clicked the button!")
  }

  def setupUI(): Unit = {
    jQuery("#click-me-button").click(addClickedMessage _)
    jQuery("body").append("<p>Hello World")
  }
}
