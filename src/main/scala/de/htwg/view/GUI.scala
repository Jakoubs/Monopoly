package de.htwg.view

import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import de.htwg.controller.Controller
import de.htwg.model.{BoardField, GoField, GoToJailField, JailField, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.model.PropertyField.Color as PropertyColor
import javafx.beans
import scalafx.application.Platform
import de.htwg.util.util.Observer
import de.htwg.Monopoly // Import the Monopoly object to access gameController
import scalafx.Includes.jfxScene2sfx
import scalafx.scene.SceneIncludes.jfxScene2sfx

object GUI extends JFXApp3 with Observer {
  var controller: Option[Controller] = None
  val numRows = 11
  val numCols = 11

  val topw = 120
  val toph = 180
  val sidew = 180
  val sideh = 120
  val edges = 180

  val contentFieldStyle =
    "-fx-font: normal bold 16pt sans-serif;" +
      "-fx-background-color: #4A4A4A;" +
      "-fx-text-fill: white;" +
      "-fx-border-radius: 8;" +
      "-fx-background-radius: 8;" +
      "-fx-alignment: center;"

  // We no longer need boardGrid as a var, as we will replace the scene content directly
  // private var boardGrid: GridPane = _

  override def start(): Unit = {
    // Retrieve the controller once JavaFX is initialized
    Monopoly.gameController match {
      case Some(ctrl) =>
        this.controller = Some(ctrl)
        ctrl.add(this) // Add the GUI as an observer to the controller
      case None =>
        println("Error: Controller not set in Monopoly.main before GUI launch.")
        // Optionally, exit the application if the controller is essential
        // Platform.exit()
        return // Exit start method if controller is missing
    }

    // Initial setup of the stage and scene
    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Variable Content Grid"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = buildBoard() // Set initial content
      }
    }
    // No need to call updateBoard() here, as content is set in the stage setup.
    // The initial call to buildBoard() will populate the scene.
  }

  private def buildBoard(): GridPane = {
    val gridPane = new GridPane {
      padding = Insets(20)
      alignment = Pos.Center
      hgap = 10
      vgap = 10

      def getFieldName(idx: Int): String =
        controller.map { c =>
          if (idx >= 0 && idx < c.game.board.fields.length) {
            c.game.board.fields(idx).name
          } else {
            s"Invalid Index ($idx)"
          }
        }.getOrElse("Controller not set")

      def getExtra(idx: Int): String = {
        controller.map { c =>
          if (idx >= 0 && idx < c.game.board.fields.length) {
            val field = c.game.board.fields(idx)
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
        }.getOrElse("")
      }

      def getPlayerOnField(idx: Int): String = {
        controller.map { c =>
          val playersOnField = c.game.players.filter(_.position == idx + 1)
          if (playersOnField.nonEmpty) {
            playersOnField.map(_.name).mkString(" ")
          } else {
            ""
          }
        }.getOrElse("")
      }

      def getColor(idx: Int): String = {
        controller.map { c =>
          if (idx >= 0 && idx < c.game.board.fields.length) {
            val field = c.game.board.fields(idx)
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
        }.getOrElse("grey")
      }

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
            text = s"${boardIndex + 1}\n${getFieldName(boardIndex)}\n${getExtra(boardIndex)}\n${getPlayerOnField(boardIndex)}"
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
    gridPane
  }

  def updateBoard(): Unit = {
    // Update the scene's content to a new GridPane. This is the key change.
    Platform.runLater {
      if (stage != null && stage.scene.value != null) {
        stage.scene.value.content = buildBoard()
      }
    }
  }

  // setController is still useful if you need to set the controller later or dynamically,
  // but for initial setup, 'start' method handles it.
  def setController(ctrl: Controller): Unit = {
    this.controller = Some(ctrl)
    // No longer adding observer here, as it's done in start()
    // No longer updating board here, as it's done in start() or via update()
  }

  override def update(): Unit = {
    updateBoard()
  }
}