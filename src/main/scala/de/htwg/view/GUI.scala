// src/main/scala/de/htwg/view/GUI.scala
package de.htwg.view

import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.scene.paint.Color
import de.htwg.controller.Controller
import de.htwg.util.util.Observer
import de.htwg.Monopoly
import scalafx.Includes.*
import scalafx.application.Platform
import de.htwg.model.BoardField
import de.htwg.view.BoardPanel
import de.htwg.model.{Dice, GoField, GoToJailField, JailField, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.controller.OpEnum
import de.htwg.controller.OpEnum.{buy, end, enter, n, pay, y}
import de.htwg.controller.{AdditionalActionsState, BuyHouseState, BuyPropertyState, ConfirmBuyHouseState, EndTurnState, GameState, JailState, MovingState, PropertyDecisionState, RollingState, StartTurnState}

object GUI extends JFXApp3 with Observer {
  private var gameController: Option[Controller] = None
  private var boardPanel: Option[BoardPanel] = None

  // --- Change these to lazy val ---
  private lazy val rollDiceButton = new Button("Würfeln")
  private lazy val buyPropertyButton = new Button("Kaufen")
  private lazy val endTurnButton = new Button("Zug beenden")
  private lazy val payJailFineButton = new Button("Kaution zahlen")
  private lazy val confirmBuyHouseButton = new Button("Bestätigen")
  private lazy val declineBuyHouseButton = new Button("Abbrechen")
  // ---------------------------------

  override def start(): Unit = {
    Monopoly.gameController match {
      case Some(ctrl) =>
        gameController = Some(ctrl)
        ctrl.add(this)
        boardPanel = Some(new BoardPanel(ctrl))
      case None =>
        println("Error: Controller not set in Monopoly.main before GUI launch. Exiting.")
        Platform.exit()
        return
    }

    val rootLayout = new VBox {
      alignment = Pos.Center
      padding = Insets(10)
      spacing = 20

      boardPanel.foreach(panel => {
        children += panel
        VBox.setVgrow(panel, Priority.Always)
      })

      children += createButtonPanel()
    }

    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Monopoly"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = rootLayout
      }
    }
    // Set initial button states based on the controller's initial state
    updateButtonStates()
  }

  private def createButtonPanel(): HBox = {
    new HBox {
      alignment = Pos.Center
      spacing = 15
      padding = Insets(10)

      rollDiceButton.minWidth = 100
      rollDiceButton.minHeight = 40
      rollDiceButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #5cb85c; -fx-text-fill: white;"
      rollDiceButton.onAction = _ => {
        gameController.foreach(_.handleInput(enter))
      }

      buyPropertyButton.minWidth = 100
      buyPropertyButton.minHeight = 40
      buyPropertyButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #f0ad4e; -fx-text-fill: white;"
      buyPropertyButton.onAction = _ => {
        //gameController.foreach(_.handleInput(enter))
        gameController.foreach(_.handleInput(y))
      }

      endTurnButton.minWidth = 120
      endTurnButton.minHeight = 40
      endTurnButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #d9534f; -fx-text-fill: white;"
      endTurnButton.onAction = _ => {
        gameController.foreach(_.handleInput(n))
        gameController.foreach(_.handleInput(end))
      }

      payJailFineButton.minWidth = 120
      payJailFineButton.minHeight = 40
      payJailFineButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #7289DA; -fx-text-fill: white;"
      payJailFineButton.onAction = _ => {
        gameController.foreach(_.handleInput(pay))
      }

      confirmBuyHouseButton.minWidth = 120
      confirmBuyHouseButton.minHeight = 40
      confirmBuyHouseButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #5cb85c; -fx-text-fill: white;"
      confirmBuyHouseButton.onAction = _ => {
        gameController.foreach(_.handleInput(buy))
      }

      declineBuyHouseButton.minWidth = 120
      declineBuyHouseButton.minHeight = 40
      declineBuyHouseButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #d9534f; -fx-text-fill: white;"
      declineBuyHouseButton.onAction = _ => {
        gameController.foreach(_.handleInput(end))
      }

      children = Seq(rollDiceButton, buyPropertyButton, endTurnButton, payJailFineButton, confirmBuyHouseButton, declineBuyHouseButton)
    }
  }

  private def updateButtonStates(): Unit = {
    gameController.foreach { ctrl =>
      val currentState = ctrl.state
      println(s"Current GUI State: $currentState")

      rollDiceButton.disable = true
      buyPropertyButton.disable = true
      endTurnButton.disable = true
      payJailFineButton.disable = true
      confirmBuyHouseButton.disable = true
      declineBuyHouseButton.disable = true

      currentState match {
        case StartTurnState() =>
          gameController.foreach(_.handleInput(enter))
          rollDiceButton.disable = false
          endTurnButton.disable = true

        case RollingState() =>
        // No buttons enabled during automatic rolling
          rollDiceButton.disable = false

        case JailState() =>
          rollDiceButton.disable = false
          payJailFineButton.disable = false
          endTurnButton.disable = true

        case MovingState(_) =>
          gameController.foreach(_.handleInput(enter))

        // Buttons are typically enabled by the *next* state after movement
        // For now, keep disabled

        case PropertyDecisionState(_) =>
          buyPropertyButton.disable = false
          endTurnButton.disable = false // For declining to buy


        case BuyPropertyState(_) =>
          gameController.foreach(_.handleInput(enter))

        // No direct button interaction here, it's an action state


        case AdditionalActionsState(_) =>
          //rollDiceButton.disable = !ctrl.currentPlayer. // Check if current player can roll again (e.g., if they rolled doubles)
          endTurnButton.disable = false

        case BuyHouseState(_) =>
        // Usually, a dialog for selecting property/house count. No direct buttons here.

        case ConfirmBuyHouseState(_, _) =>
          confirmBuyHouseButton.disable = false
          declineBuyHouseButton.disable = false

        case EndTurnState() =>
          gameController.foreach(_.handleInput(enter))

        // This state transitions immediately, so no buttons are needed.
      }
    }
  }

  def updateBoard(): Unit = {
    Platform.runLater {
      boardPanel.foreach(_.buildBoard())
      updateButtonStates()
    }
  }

  def setController(ctrl: Controller): Unit = {
    gameController = Some(ctrl)
    ctrl.add(this)
  }

  override def update(): Unit = {
    updateBoard()
  }
}