package de.htwg.view

import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, Label, TextField}
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
import de.htwg.controller.OpEnum.{buy, end, enter, n, pay, redo, undo, y}
import de.htwg.controller.{AdditionalActionsState, BuyHouseState, BuyPropertyState, ConfirmBuyHouseState, EndTurnState, GameState, JailState, MovingState, PropertyDecisionState, RollingState, StartTurnState}
import scalafx.collections.ObservableBuffer
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration
import scala.util.Random
import scalafx.scene.image.{Image, ImageView}


object GUI extends JFXApp3 with Observer {
  private var gameController: Option[Controller] = None
  private var boardPanel: Option[BoardPanel] = None

  private lazy val rollDiceButton = new Button("Würfeln")
  private lazy val buyPropertyButton = new Button("Kaufen")
  private lazy val endTurnButton = new Button("Zug beenden")
  private lazy val payJailFineButton = new Button("Kaution zahlen")
  private lazy val confirmBuyHouseButton = new Button("Bestätigen")
  private lazy val declineBuyHouseButton = new Button("Abbrechen")
  private lazy val diceLabel = new Label("🎲") {
    style = "-fx-font: bold 32pt sans-serif; -fx-text-fill: white;"
  }
  val diceImages = (1 to 6).map(i =>
    new Image(getClass.getResourceAsStream(s"/dice-$i.png"))
  ).toArray
  private lazy val diceImageView1 = new ImageView {
    fitWidth = 48
    fitHeight = 48
    image = diceImages(0)
  }
  private lazy val diceImageView2 = new ImageView {
    fitWidth = 48
    fitHeight = 48
    image = diceImages(0)
  }
  private lazy val turnInfoLabel = new Label {
    style = "-fx-font: normal 14pt sans-serif; -fx-text-fill: white; -fx-background-color: #333333; -fx-padding: 10px;"
    wrapText = true
    maxWidth = Double.MaxValue
  }
  private lazy val
  playersInfoLabel = new Label {
    style = "-fx-font: normal 14pt sans-serif; -fx-text-fill: white; -fx-background-color: #333333; -fx-padding: 10px;"
    wrapText = true
    maxWidth = Double.MaxValue
  }

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
    val mainLayout = new HBox {
      spacing = 20
      padding = Insets(10)

      val leftColumn = new VBox {
        alignment = Pos.Center
        spacing = 20

        boardPanel.foreach(panel => {
          children += panel
          VBox.setVgrow(panel, Priority.Always)
        })

        children += turnInfoLabel
        children += createButtonPanel()
      }

      val rightColumn = new VBox {
        alignment = Pos.TopRight
        spacing = 10
        minWidth = 250

        children += new Label {
          text = "SPIELER ÜBERSICHT"
          style = "-fx-font: bold 18pt sans-serif; -fx-text-fill: white;"
        }
        children += playersInfoLabel
      }

      children = Seq(leftColumn, rightColumn)
      HBox.setHgrow(leftColumn, Priority.Always)
    }


    stage = new JFXApp3.PrimaryStage {
      title = "Monopoly"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = mainLayout
      }
    }
    updatePlayersInfo()
    updateButtonStates()
  }

  private def updatePlayersInfo(): Unit = {
    gameController.foreach { ctrl =>
      val players = ctrl.game.players
      val currentPlayer = ctrl.currentPlayer

      val playersInfoBuilder = new StringBuilder()

      players.foreach { player =>
        val currentMarker = if (player == currentPlayer) "▶ " else ""
        val playerName = player.name

        playersInfoBuilder.append(
          s"$currentMarker $playerName\n" +
            s"   Position: ${player.position}\n" +
            s"   Guthaben: ${player.balance}€\n\n"
        )
      }

      playersInfoLabel.text = playersInfoBuilder.toString()
    }
  }

  def createButtonPanel(): HBox = {
    new HBox {
      alignment = Pos.Center
      spacing = 15
      padding = Insets(10)

      rollDiceButton.minWidth = 100
      rollDiceButton.minHeight = 40
      rollDiceButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #5cb85c; -fx-text-fill: white;"
      rollDiceButton.onAction = _ => {
        gameController.foreach(_.handleInput(enter))
        gameController.foreach(_.handleInput(enter))
        gameController.foreach(_.handleInput(enter))
          val timeline = new Timeline {
            var count = 0
            val maxRolls = 19
            keyFrames = KeyFrame(Duration(60), onFinished = _ => {
              val value1 = Random.nextInt(6)
              val value2 = Random.nextInt(6)
              diceImageView1.image = diceImages(value1)
              diceImageView2.image = diceImages(value2)
              count += 1
              if (count >= maxRolls) {
                stop()
                gameController.foreach { ctrl =>
                  val turnInfo = ctrl.getTurnInfo
                  val d1 = if (turnInfo.diceRoll1 > 0) turnInfo.diceRoll1 - 1 else 0
                  val d2 = if (turnInfo.diceRoll2 > 0) turnInfo.diceRoll2 - 1 else 0
                  diceImageView1.image = diceImages(d1)
                  diceImageView2.image = diceImages(d2)
                  ctrl.handleInput(enter)
                  ctrl.handleInput(enter)
                  ctrl.handleInput(enter)
                }
              }
            })
            cycleCount = maxRolls
          }
          timeline.playFromStart()
        }

      buyPropertyButton.minWidth = 100
      buyPropertyButton.minHeight = 40
      buyPropertyButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #f0ad4e; -fx-text-fill: white;"
      buyPropertyButton.onAction = _ => {
        gameController.foreach(_.handleInput(y))
        gameController.foreach(_.handleInput(enter))
        gameController.foreach(_.handleInput(enter))
        gameController.foreach(_.handleInput(enter))
      }

      endTurnButton.minWidth = 120
      endTurnButton.minHeight = 40
      endTurnButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #d9534f; -fx-text-fill: white;"
      endTurnButton.onAction = _ => {
        gameController.foreach(_.handleInput(n))
        gameController.foreach(_.handleInput(end))
        gameController.foreach(_.handleInput(enter))
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
        gameController.foreach(_.handleInput(enter))
        gameController.foreach(_.handleInput(enter))
      }

      declineBuyHouseButton.minWidth = 120
      declineBuyHouseButton.minHeight = 40
      declineBuyHouseButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #d9534f; -fx-text-fill: white;"
      declineBuyHouseButton.onAction = _ => {
        gameController.foreach(_.handleInput(end))
      }

      children = Seq(
        diceImageView1, diceImageView2, rollDiceButton, buyPropertyButton, endTurnButton, payJailFineButton
      )
    }
  }

  private def updateButtonStates(): Unit = {
    gameController.foreach { ctrl =>
      val currentState = ctrl.state

      currentState match {
        case StartTurnState() =>
          rollDiceButton.disable = false
          endTurnButton.disable = true
          buyPropertyButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case RollingState(_) =>
          rollDiceButton.disable = false
          buyPropertyButton.disable = true
          endTurnButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case JailState() =>
          rollDiceButton.disable = false
          payJailFineButton.disable = false
          endTurnButton.disable = true
          buyPropertyButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case MovingState(_) =>
          rollDiceButton.disable = true
          buyPropertyButton.disable = true
          endTurnButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case PropertyDecisionState(_) =>
          buyPropertyButton.disable = false
          endTurnButton.disable = false
          rollDiceButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case BuyPropertyState(_) =>
          rollDiceButton.disable = true
          buyPropertyButton.disable = true
          endTurnButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case AdditionalActionsState(_) =>
          endTurnButton.disable = false
          rollDiceButton.disable = true
          buyPropertyButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true

        case BuyHouseState(_) =>
          confirmBuyHouseButton.disable = false
          declineBuyHouseButton.disable = false
          rollDiceButton.disable = true
          buyPropertyButton.disable = true
          endTurnButton.disable = true
          payJailFineButton.disable = true

        case ConfirmBuyHouseState(_, _) =>
          confirmBuyHouseButton.disable = false
          declineBuyHouseButton.disable = false
          rollDiceButton.disable = true
          buyPropertyButton.disable = true
          endTurnButton.disable = true
          payJailFineButton.disable = true

        case EndTurnState() =>
          rollDiceButton.disable = true
          buyPropertyButton.disable = true
          endTurnButton.disable = true
          payJailFineButton.disable = true
          confirmBuyHouseButton.disable = true
          declineBuyHouseButton.disable = true
      }
    }
  }

  private def updateTurnInfo(): Unit = {
    gameController.foreach { ctrl =>
      val turnInfo = ctrl.getTurnInfo
      val infoBuilder = new StringBuilder()

      turnInfo.diceRoll1 match {
        case 0 =>
        case _ => infoBuilder.append(s"Würfelergebnis: ${turnInfo.diceRoll1} und ${turnInfo.diceRoll2} (Summe: ${turnInfo.diceRoll1 + turnInfo.diceRoll2})\n")
      }

      turnInfo.landedField.foreach(field =>
        infoBuilder.append(s"Gelandet auf: ${field.name}\n")
      )

      turnInfo.boughtProperty.foreach(property =>
        infoBuilder.append(s"Gekaufte Immobilie: ${property.name}\n")
      )

      turnInfo.builtHouse.foreach(property =>
        infoBuilder.append(s"Haus gebaut auf: ${property.name}\n")
      )

      (turnInfo.paidRent, turnInfo.rentPaidTo) match {
        case (Some(rent), Some(owner)) =>
          infoBuilder.append(s"Miete bezahlt: ${rent}€ an ${owner.name}\n")
        case _ =>
      }

      turnInfoLabel.text = infoBuilder.toString()
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
    Platform.runLater {
      boardPanel.foreach(_.buildBoard())
      updateTurnInfo()
      updateButtonStates()
      updatePlayersInfo()
    }
  }
}