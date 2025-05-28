// src/main/scala/de/htwg/view/BoardPanel.scala
package de.htwg.view

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import de.htwg.controller.Controller
import de.htwg.model.{BoardField, GoField, GoToJailField, JailField, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.model.PropertyField.Color as PropertyColor

// BoardPanel is a class that takes a Controller as a dependency
// It extends GridPane because it is a GridPane that represents the board
class BoardPanel(controller: Controller) extends GridPane {

  // Constants (can be made configurable if needed)
  private val numRows = 11
  private val numCols = 11

  private val scaleFactor = .5
  private val topw = 120 * scaleFactor
  private val toph = 180 * scaleFactor
  private val sidew = 180 * scaleFactor
  private val sideh = 120 * scaleFactor
  private val edges = 180 * scaleFactor
  private val fontsize = (8 * scaleFactor*0.7).toString

  private val contentFieldStyle =
    "-fx-font: normal bold "+fontsize+"pt sans-serif;" +
      "-fx-background-color: #4A4A4A;" +
      "-fx-text-fill: white;" +
      "-fx-border-radius: 8;" +
      "-fx-background-radius: 8;" +
      "-fx-alignment: center;"

  // Initialize the GridPane properties
  padding = Insets(20)
  alignment = Pos.Center
  hgap = 10
  vgap = 10

  // Immediately build the board when an instance of BoardPanel is created
  buildBoard()

  // Helper functions - these now access the 'controller' instance passed to the class
  private def getFieldName(idx: Int): String = {
    if (idx >= 0 && idx < controller.game.board.fields.length) {
      controller.game.board.fields(idx).name
    } else {
      s"Invalid Index ($idx)"
    }
  }

  private def getExtra(idx: Int): String = {
    if (idx >= 0 && idx < controller.game.board.fields.length) {
      val field = controller.game.board.fields(idx)
      field match {
        case pf: PropertyField =>
          pf.owner match {
            case Some(owner) => s" ${owner.name} ${pf.house.amount}🏠"
            case None => ""
          }
        case ts: TrainStationField =>
          ts.owner match {
            case Some(owner) => s" ${owner.name}"
            case None => ""
          }
        case uf: UtilityField =>
          uf.owner match {
            case Some(owner) => s" ${owner.name}"
            case None => ""
          }
        case _ => ""
      }
    } else {
      ""
    }
  }

  private def getPlayerOnField(idx: Int): String = {
    val playersOnField = controller.game.players.filter(_.position == idx + 1)
    if (playersOnField.nonEmpty) {
      playersOnField.map(_.name).mkString(" ")
    } else {
      ""
    }
  }

  private def getColor(idx: Int): String = {
    if (idx >= 0 && idx < controller.game.board.fields.length) {
      val field = controller.game.board.fields(idx)
      field match {
        case pf: PropertyField =>
          pf.color match {
            case PropertyColor.Brown => "brown"
            case PropertyColor.LightBlue => "lightblue"
            case PropertyColor.Pink => "deeppink"
            case PropertyColor.Orange => "orange"
            case PropertyColor.Red => "red"
            case PropertyColor.Yellow => "gold"
            case PropertyColor.Green => "limegreen"
            case PropertyColor.DarkBlue => "darkblue"
            case _ => "grey"
          }
        case ts: TrainStationField =>
          "black"
        case _ => "grey"
      }
    } else {
      "grey"
    }
  }

  // Method to build the board content
  def buildBoard(): Unit = {
    // Clear existing children before rebuilding to avoid duplicates
    children.clear()

    // Edges (Corners)
    add(new Label {
      text = s"1\n${getFieldName(0)}\n${getPlayerOnField(0)}"
      prefWidth = edges
      prefHeight = edges
      style = contentFieldStyle
    }, 0, 0)

    add(new Label {
      text = s"11\n${getFieldName(10)}\n${getPlayerOnField(10)}"
      prefWidth = edges
      prefHeight = edges
      style = contentFieldStyle
    }, 10, 0)

    add(new Label {
      text = s"21\n${getFieldName(20)}\n${getPlayerOnField(20)}"
      prefWidth = edges
      prefHeight = edges
      style = contentFieldStyle
    }, 10, 10)

    add(new Label {
      text = s"31\n${getFieldName(30)}\n${getPlayerOnField(30)}"
      prefWidth = edges
      prefHeight = edges
      style = contentFieldStyle
    }, 0, 10)

    // TOP ROW (Fields 1-9) - Stripe at the bottom
    for {
      col <- 1 until numCols - 1
      boardIndex = col
    } add(new VBox {
      val stripeHeight = toph / 5
      val contentHeight = toph - stripeHeight

      children = Seq(
        new Label {
          text = s"${boardIndex + 1}\n${getFieldName(boardIndex)}\n${getPlayerOnField(boardIndex)}"
          prefWidth = topw
          prefHeight = contentHeight
          style = contentFieldStyle
        },
        new Label {
          prefHeight = stripeHeight
          prefWidth = topw
          style = s"-fx-background-color: ${getColor(boardIndex)}; -fx-text-fill: white; -fx-alignment: center;"
          text = getExtra(boardIndex)
        }
      )
    }, col, 0)

    // SIDE RIGHT (Fields 11-19) - Stripe at the left
    for {
      row <- 1 until numRows - 1
      boardIndex = 10 + row
    } add(new HBox {
      val stripeWidth = sidew / 5
      val contentWidth = sidew - stripeWidth

      children = Seq(
        new Label {
          prefWidth = stripeWidth
          prefHeight = sideh
          style = s"-fx-background-color: ${getColor(boardIndex)}; -fx-text-fill: white; -fx-alignment: center;"
          text = getExtra(boardIndex)
        },
        new Label {
          text = s"${boardIndex + 1}\n${getFieldName(boardIndex)}\n${getPlayerOnField(boardIndex)}"
          prefWidth = contentWidth
          prefHeight = sideh
          style = contentFieldStyle
        }
      )
    }, 10, row)

    // BOTTOM ROW (Fields 21-29) - Reversed order - Stripe at the top
    for {
      col <- 1 until numCols - 1
      boardIndex = 20 + (numCols - 1 - col)
    } add(new VBox {
      val stripeHeight = toph / 5
      val contentHeight = toph - stripeHeight

      children = Seq(
        new Label {
          prefHeight = stripeHeight
          prefWidth = topw
          style = s"-fx-background-color: ${getColor(boardIndex)}; -fx-text-fill: white; -fx-alignment: center;"
          text = getExtra(boardIndex)
        },
        new Label {
          text = s"${boardIndex + 1}\n${getFieldName(boardIndex)}\n${getPlayerOnField(boardIndex)}"
          prefWidth = topw
          prefHeight = contentHeight
          style = contentFieldStyle
        }
      )
    }, col, 10)

    // SIDE LEFT (Fields 31-39) - Reversed order - Stripe at the right
    for {
      row <- 1 until numRows - 1
      boardIndex = 30 + (numRows - 1 - row)
    } add(new HBox {
      val stripeWidth = sidew / 5
      val contentWidth = sidew - stripeWidth

      children = Seq(
        new Label {
          text = s"${boardIndex + 1}\n${getFieldName(boardIndex)}\n${getPlayerOnField(boardIndex)}"
          prefWidth = contentWidth
          prefHeight = sideh
          style = contentFieldStyle
        },
        new Label {
          prefWidth = stripeWidth
          prefHeight = sideh
          style = s"-fx-background-color: ${getColor(boardIndex)}; -fx-text-fill: white; -fx-alignment: center;"
          text = getExtra(boardIndex)
        }
      )
    }, 0, row)
  }
}