// src/main/scala/de/htwg/view/BoardPanel.scala
package de.htwg.view

import de.htwg.controller.IController
import scalafx.scene.text.Font
import scalafx.geometry.{HPos, Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import de.htwg.model.modelBaseImple.PropertyField.Color as PropertyColor
import de.htwg.model.modelBaseImple.{BoardField, GoField, GoToJailField, JailField, PropertyField, TaxField, TrainStationField, UtilityField}
import scalafx.scene.image.{Image, ImageView}

class BoardPanel(controller: IController) extends GridPane {

  private val numRows = 11
  private val numCols = 11

  private val scaleFactor = .44
  private val topw = 120 * scaleFactor
  private val toph = 180 * scaleFactor
  private val sidew = 180 * scaleFactor
  private val sideh = 120 * scaleFactor
  private val edges = 180 * scaleFactor
  private val fontsize = (24*scaleFactor).toString

  private val contentFieldStyle =
    "-fx-font: normal bold "+fontsize+"pt sans-serif;" +
      "-fx-background-color: #4A4A4A;" +
      "-fx-text-fill: white;" +
      "-fx-border-radius: 8;" +
      "-fx-background-radius: 8;" +
      "-fx-alignment: center;"

  padding = Insets(20)
  alignment = Pos.Center
  hgap = 10
  vgap = 10

  buildBoard()

  private def getFieldName(idx: Int): String = {
    if (idx >= 0 && idx < controller.board.fields.length) {
      controller.board.fields(idx).name
    } else {
      s"Invalid Index ($idx)"
    }
  }

  private def getExtra(idx: Int): String = {
    if (idx >= 0 && idx < controller.board.fields.length) {
      val field = controller.board.fields(idx)
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

  private def getExtraSide(idx: Int): String = {
    if (idx >= 0 && idx < controller.board.fields.length) {
      val field = controller.board.fields(idx)
      field match {
        case pf: PropertyField =>
          pf.owner match {
            case Some(owner) => s" ${owner.name}\n ${pf.house.amount}\n🏠"
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
    val playersOnField = controller.players.filter(_.position == idx + 1)
    if (playersOnField.nonEmpty) {
      playersOnField.map(_.name).mkString(" ")
    } else {
      ""
    }
  }

  private def getColor(idx: Int): String = {
    if (idx >= 0 && idx < controller.board.fields.length) {
      val field = controller.board.fields(idx)
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

  def buildBoard(): Unit = {
    children.clear()
    try {
      val monopolyLogo = new Image(getClass.getResourceAsStream("/image/center2.png"))
      val logoView = new ImageView(monopolyLogo) {
        fitWidth = 500
        fitHeight = 500
        preserveRatio = true
      }
      add(logoView, 1, 1, numCols - 2, numRows - 2)
      GridPane.setHalignment(logoView, HPos.Center)
    } catch {
      case e: Exception => println("Could not load monopoly.png: " + e.getMessage)
    }
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

    for {
      col <- 1 until numCols - 1
      boardIndex = col
    } add(new VBox {
      val stripeHeight = toph / 4
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
      val stripeWidth = sidew / 4
      val contentWidth = sidew - stripeWidth

      children = Seq(
        new Label {
          prefWidth = stripeWidth
          prefHeight = sideh
          style = s"-fx-background-color: ${getColor(boardIndex)}; -fx-text-fill: white; -fx-alignment: center;"
          text = getExtraSide(boardIndex)
        },
        new Label {
          text = s"${boardIndex + 1}\n${getFieldName(boardIndex)}\n${getPlayerOnField(boardIndex)}"
          prefWidth = contentWidth
          prefHeight = sideh
          style = contentFieldStyle
        }
      )
    }, 10, row)

    for {
      col <- 1 until numCols - 1
      boardIndex = 20 + (numCols - 1 - col)
    } add(new VBox {
      val stripeHeight = toph / 4
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
      val stripeWidth = sidew / 4
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
          text = getExtraSide(boardIndex)
        }
      )
    }, 0, row)
  }
}