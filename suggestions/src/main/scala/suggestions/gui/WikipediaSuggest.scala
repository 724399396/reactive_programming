package suggestions
package gui

import scala.collection.mutable.ListBuffer
import scala.swing._
import scala.util.{ Try, Success, Failure }
import scala.swing.event._
import swing.Swing._
import javax.swing.UIManager
import Orientation._
import rx.lang.scala.Observable
import rx.lang.scala.Subscription
import observablex._
import search._

object WikipediaSuggest extends SimpleSwingApplication with ConcreteSwingApi with ConcreteWikipediaApi {

  {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch {
      case t: Throwable =>
    }
  }

  def top = new MainFrame {

    /* gui setup */

    title = "Query Wikipedia"
    minimumSize = new Dimension(900, 600)

    val button = new Button("Get") {
      icon = new javax.swing.ImageIcon(javax.imageio.ImageIO.read(this.getClass.getResourceAsStream("/suggestions/wiki-icon.png")))
    }
    val searchTermField = new TextField
    val suggestionList = new ListView(ListBuffer[String]())
    val status = new Label(" ")
    val editorpane = new EditorPane {
      import javax.swing.border._
      border = new EtchedBorder(EtchedBorder.LOWERED)
      editable = false
      peer.setContentType("text/html")
    }

    contents = new BoxPanel(orientation = Vertical) {
      border = EmptyBorder(top = 5, left = 5, bottom = 5, right = 5)
      contents += new BoxPanel(orientation = Horizontal) {
        contents += new BoxPanel(orientation = Vertical) {
          maximumSize = new Dimension(240, 900)
          border = EmptyBorder(top = 10, left = 10, bottom = 10, right = 10)
          contents += new BoxPanel(orientation = Horizontal) {
            maximumSize = new Dimension(640, 30)
            border = EmptyBorder(top = 5, left = 0, bottom = 5, right = 0)
            contents += searchTermField
          }
          contents += new ScrollPane(suggestionList)
          contents += new BorderPanel {
            maximumSize = new Dimension(640, 30)
            add(button, BorderPanel.Position.Center)
          }
        }
        contents += new ScrollPane(editorpane)
      }
      contents += status
    }

    val eventScheduler = SchedulerEx.SwingEventThreadScheduler

    /**
      * Observables
      * You may find the following methods useful when manipulating GUI elements:
      *  `myListView.listData = aList` : sets the content of `myListView` to `aList`
      *  `myTextField.text = "react"` : sets the content of `myTextField` to "react"
      *  `myListView.selection.items` returns a list of selected items from `myListView`
      *  `myEditorPane.text = "act"` : sets the content of `myEditorPane` to "act"
      */

    // TO IMPLEMENT
    val searchTerms: Observable[String] = searchTermField.textValues

    // TO IMPLEMENT
    val suggestions: Observable[Try[List[String]]] = searchTerms.concatRecovered(t => wikiSuggestResponseStream(t))


    // TO IMPLEMENT
    val suggestionSubscription: Subscription =  suggestions.observeOn(eventScheduler) subscribe {
      x => x match {
        case Success(s) => suggestionList.listData = s
        case Failure(e) => status.text = e.toString
      }
    }

    // TO IMPLEMENT
    val selections: Observable[String] = {
      Observable(observer => {
        button.clicks.subscribe(
          value => {
            suggestionList.selection.items.toList match {
              case x :: xs => observer.onNext(x)
              case Nil => status.text = "Nothing selected! Please select a search term."
            }
          },
          error => observer.onError(error),
          () => observer.onCompleted)
      })
    }

    // TO IMPLEMENT
    val pages: Observable[Try[String]] = selections.concatRecovered(t => wikiPageResponseStream(t))

    // TO IMPLEMENT
    val pageSubscription: Subscription = pages.observeOn(eventScheduler) subscribe {
      x => x match {
        case Success(s) => editorpane.text = s
        case Failure(e) => status.text = "Error: " + e.getMessage()
      }
    }

  }

}


trait ConcreteWikipediaApi extends WikipediaApi {
  def wikipediaSuggestion(term: String) = Search.wikipediaSuggestion(term)
  def wikipediaPage(term: String) = Search.wikipediaPage(term)
}


trait ConcreteSwingApi extends SwingApi {
  type ValueChanged = scala.swing.event.ValueChanged
  object ValueChanged {
    def unapply(x: Event) = x match {
      case vc: ValueChanged => Some(vc.source.asInstanceOf[TextField])
      case _ => None
    }
  }
  type ButtonClicked = scala.swing.event.ButtonClicked
  object ButtonClicked {
    def unapply(x: Event) = x match {
      case bc: ButtonClicked => Some(bc.source.asInstanceOf[Button])
      case _ => None
    }
  }
  type TextField = scala.swing.TextField
  type Button = scala.swing.Button
}