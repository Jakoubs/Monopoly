package de.htwg.view

import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, Button, ComboBox, Label, TextField, TextInputDialog}
import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.scene.paint.Color
import de.htwg.util.util.{Observable, Observer}
import de.htwg.Monopoly
import de.htwg.controller.IController
import de.htwg.model.IPlayer
import scalafx.Includes.*
import scalafx.application.Platform
import de.htwg.view.BoardPanel
import de.htwg.controller.controllerBaseImpl.OpEnum.{buy, end, enter, n, pay, y}
import de.htwg.controller.controllerBaseImpl.{AdditionalActionsState, BuyHouseState, BuyPropertyState, ConfirmBuyHouseState, EndTurnState, GameState, JailState, MovingState, OpEnum, PropertyDecisionState, RollingState, StartTurnState}
import de.htwg.model.modelBaseImple.{BoardField, Dice, GoField, GoToJailField, JailField, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.{Image, ImageView}
import scalafx.util.Duration
import scalafx.geometry.Insets
import scalafx.stage.Modality

import scala.util.{Random, Try}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

object GUI extends JFXApp3 with Observer {
  private var gameController: Option[IController] = None
  private var boardPanel: Option[BoardPanel] = None

  private lazy val buyHouseButton = new Button("Haus")
  private lazy val rollDiceButton = new Button("Würfeln")
  private lazy val buyPropertyButton = new Button("Kaufen")
  private lazy val refusePropertyButton = new Button("Passen")
  private lazy val endTurnButton = new Button("Zug beenden")
  private lazy val payJailFineButton = new Button("Kaution zahlen")
  private lazy val confirmBuyHouseButton = new Button("Bestätigen")
  private lazy val declineBuyHouseButton = new Button("Abbrechen")
  private lazy val saveButton = new Button("save")
  private lazy val loadButton = new Button("load")
  private lazy val tradeButton = new Button("trade")

  private lazy val propertyDecisionButtons = new HBox {
    spacing = 0
    children = Seq(buyPropertyButton, refusePropertyButton)
  }

  private lazy val houseConfirmationButtons = new HBox {
    spacing = 15
    alignment = Pos.Center
    children = Seq(confirmBuyHouseButton, declineBuyHouseButton)
  }

  private lazy val turnInfoLabel = new Label {
    style = "-fx-font: normal 14pt sans-serif; -fx-text-fill: white; -fx-background-color: #333333; -fx-padding: 10px;"
    wrapText = true
    maxWidth = Double.MaxValue
  }
  private lazy val playersInfoLabel = new Label {
    style = "-fx-font: normal 14pt sans-serif; -fx-text-fill: white; -fx-background-color: #333333; -fx-padding: 10px;"
    wrapText = true
    maxWidth = Double.MaxValue
  }
  val diceImages = (1 to 6).map(i =>
    new Image(getClass.getResourceAsStream(s"/image/dice-$i.png"))).toArray
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

  // Pfad zum Haus-Icon für den Dialog
  val houseIconPath = "/image/Haus.png"

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
        saveButton.minWidth = 100
        saveButton.minHeight = 40
        saveButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #ffffff; -fx-text-fill: black;"
        saveButton.onAction = _ => {}

        loadButton.minWidth = 100
        loadButton.minHeight = 40
        loadButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #ffffff; -fx-text-fill: black;"
        loadButton.onAction = _ => {}

        tradeButton.minWidth = 100
        tradeButton.minHeight = 40
        tradeButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #ffffff; -fx-text-fill: black;"
        tradeButton.onAction = _ => {
          new Alert(AlertType.Error) {
          initOwner(stage)
          title = "Fehler"
          headerText = "Aktion nicht möglich"
          contentText = "Der Handel ist noch nicht implementiert."
        }.showAndWait()}

        children += playersInfoLabel
        children += saveButton
        children += loadButton
        children += tradeButton
      }

      children = Seq(leftColumn, rightColumn)
      HBox.setHgrow(leftColumn, Priority.Always)
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Monopoly"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = mainLayout
        stylesheets.add(getClass.getResource("/style/dark_mode.css").toExternalForm)
      }
    }
    updatePlayersInfo()
    updateButtonStates()
  }

  private def updatePlayersInfo(): Unit = {
    gameController.foreach { ctrl =>
      val players = ctrl.players
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
      spacing = 15 // General spacing for other buttons
      padding = Insets(10)

      rollDiceButton.minWidth = 100
      rollDiceButton.minHeight = 40
      rollDiceButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #5cb85c; -fx-text-fill: white;"
      rollDiceButton.onAction = _ => {
        gameController.foreach(_.handleInput(enter)) // Keep this single call
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
              val turnInfo = gameController.get.getTurnInfo
              if (turnInfo.diceRoll1 > 0 && turnInfo.diceRoll2 > 0) {
                diceImageView1.image = diceImages(turnInfo.diceRoll1 - 1)
                diceImageView2.image = diceImages(turnInfo.diceRoll2 - 1)
              }
            }
          })
          cycleCount = maxRolls
        }
        timeline.playFromStart()
      }

      buyHouseButton.minWidth = 100
      buyHouseButton.minHeight = 40
      buyHouseButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #ffffff; -fx-text-fill: black;"
      buyHouseButton.onAction = _ => {
        val dialog = new TextInputDialog() {
          initOwner(stage)
          title = "Haus kaufen"
          headerText = "Geben Sie die ID des Grundstücks ein (1-40)."
          contentText = "Grundstücks-ID:"
        }
        dialog.showAndWait().foreach { inputText =>
          Try(inputText.toInt).toOption match {
            case Some(intValue) if intValue >= 1 && intValue <= 40 =>
              gameController.foreach { ctrl =>
                val player = ctrl.currentPlayer
                ctrl.board.fields(intValue - 1) match {
                  case propertyField: PropertyField =>
                    val maxHouses = 5
                    val housePrice = propertyField.price / 2

                    val validationResult = for {
                      _ <- Try(if (!propertyField.owner.exists(_.name == player.name)) throw new Exception(s"Sie besitzen ${propertyField.name} nicht."))
                      _ <- Try(if (player.balance < housePrice) throw new Exception(s"Sie haben nicht genug Geld. Benötigt: ${housePrice}€"))
                      _ <- Try(if (propertyField.house.amount >= maxHouses) throw new Exception(s"Auf ${propertyField.name} können keine weiteren Häuser gebaut werden."))
                      ownsAll <- Try {
                        val group = ctrl.board.fields.collect { case p: PropertyField if p.color == propertyField.color => p }
                        group.forall(_.owner.exists(_.name == player.name))
                      }
                      _ <- Try(if (!ownsAll) throw new Exception(s"Sie müssen alle Grundstücke der Farbe ${propertyField.color} besitzen."))
                    } yield ()

                    validationResult match {
                      case scala.util.Success(_) =>
                        ctrl.handleInput(OpEnum.fieldSelected(intValue))
                      case scala.util.Failure(e) =>
                        new Alert(AlertType.Error) {
                          initOwner(stage)
                          title = "Fehler beim Hauskauf"
                          headerText = "Aktion nicht möglich"
                          contentText = e.getMessage
                        }.showAndWait()
                    }
                  case _ =>
                    new Alert(AlertType.Error) {
                      initOwner(stage)
                      title = "Ungültiges Feld"
                      headerText = "Auf diesem Feld können keine Häuser gebaut werden."
                    }.showAndWait()
                }
              }
            case _ =>
              new Alert(AlertType.Error) {
                initOwner(stage)
                title = "Ungültige Eingabe"
                headerText = "Bitte geben Sie eine gültige Ganzzahl zwischen 1 und 40 ein."
              }.showAndWait()
          }
        }
      }

      buyPropertyButton.minWidth = 50
      buyPropertyButton.minHeight = 40
      buyPropertyButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #f0ad4e; -fx-text-fill: white;"
      buyPropertyButton.onAction = _ => {
        gameController.foreach(_.handleInput(y))
      }

      refusePropertyButton.minWidth = 50
      refusePropertyButton.minHeight = 40
      refusePropertyButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #d9534f; -fx-text-fill: yellow;"
      refusePropertyButton.onAction = _ => {
        gameController.foreach(_.handleInput(n))
      }

      endTurnButton.minWidth = 120
      endTurnButton.minHeight = 40
      endTurnButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #d9534f; -fx-text-fill: white;"
      endTurnButton.onAction = _ => {
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

      children = Seq(
        diceImageView1, diceImageView2, rollDiceButton, propertyDecisionButtons, endTurnButton, buyHouseButton, payJailFineButton, houseConfirmationButtons
      )
    }
  }

  private def updateButtonStates(): Unit = {
    gameController.foreach { ctrl =>
      val currentState = ctrl.state

      // Helper to set visibility and managed state
      def setVisible(node: javafx.scene.Node, isVisible: Boolean): Unit = {
        node.setVisible(isVisible)
        node.setManaged(isVisible)
      }

      // Default state: hide all action buttons/groups
      setVisible(rollDiceButton, false)
      setVisible(propertyDecisionButtons, false)
      setVisible(endTurnButton, false)
      setVisible(buyHouseButton, false)
      setVisible(payJailFineButton, false)
      setVisible(houseConfirmationButtons, false)

      // Enable buttons based on the current state
      currentState match {
        case StartTurnState() =>
          gameController.foreach(_.handleInput(enter))
          setVisible(rollDiceButton, true)

        case RollingState(_) =>
          setVisible(rollDiceButton, true)

        case JailState() =>
          setVisible(rollDiceButton, true)
          setVisible(payJailFineButton, true)

        case MovingState(_) =>
          // Transient state, no buttons needed
          gameController.foreach(_.handleInput(enter))

        case PropertyDecisionState(_) =>
          setVisible(propertyDecisionButtons, true)

        case BuyPropertyState(_) =>
          // Transient state
          gameController.foreach(_.handleInput(enter))

        case AdditionalActionsState(_) =>
          setVisible(endTurnButton, true)
          setVisible(buyHouseButton, true)

        case BuyHouseState(_) =>
          // Modal dialog is open, but we keep the buttons for context
          setVisible(endTurnButton, true)
          setVisible(buyHouseButton, true)

        case ConfirmBuyHouseState(_, _) =>
          setVisible(houseConfirmationButtons, true)

        case EndTurnState() =>
          // Transient state, triggers next turn
          gameController.foreach(_.handleInput(enter))
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
        case (Some(rent), Some(owner: IPlayer)) =>
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

  def setController(ctrl: IController): Unit = {
    gameController = Some(ctrl)
    ctrl match {
      case observable: Observable => observable.add(this)
      case _ => println("Warning: Controller ist keine Observable-Implementierung.")
    }
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