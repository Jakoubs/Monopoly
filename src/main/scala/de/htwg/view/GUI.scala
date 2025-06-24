package de.htwg.view

            import scalafx.application.JFXApp3
            import scalafx.geometry.{Insets, Pos}
            import scalafx.scene.Scene
            import scalafx.scene.control.{Button, ComboBox, Label, TextField}
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
            import de.htwg.controller.controllerBaseImpl.{AdditionalActionsState, BuyHouseState, BuyPropertyState, ConfirmBuyHouseState, EndTurnState, GameState, JailState, MovingState, PropertyDecisionState, RollingState, StartTurnState}
            import de.htwg.model.modelBaseImple.{BoardField, Dice, GoField, GoToJailField, JailField, Player, PropertyField, TaxField, TrainStationField, UtilityField}
            import scalafx.animation.{KeyFrame, Timeline}
            import scalafx.collections.ObservableBuffer
            import scalafx.scene.image.{Image, ImageView}

            import scala.concurrent.duration.Duration

            object GUI extends JFXApp3 with Observer {
              private var gameController: Option[IController] = None
              private var boardPanel: Option[BoardPanel] = None

              private lazy val rollDiceButton = new Button("Würfeln")
              private lazy val buyPropertyButton = new Button("Kaufen")
              private lazy val endTurnButton = new Button("Zug beenden")
              private lazy val payJailFineButton = new Button("Kaution zahlen")
              private lazy val confirmBuyHouseButton = new Button("Bestätigen")
              private lazy val declineBuyHouseButton = new Button("Abbrechen")
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
                  spacing = 15
                  padding = Insets(10)

                  rollDiceButton.minWidth = 100
                  rollDiceButton.minHeight = 40
                  rollDiceButton.style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #5cb85c; -fx-text-fill: white;"
                  rollDiceButton.onAction = _ => {
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
                            ctrl.handleInput(enter)
                            ctrl.handleInput(enter)
                            ctrl.handleInput(enter)

                            val turnInfo = gameController.get.getTurnInfo
                            if (turnInfo.diceRoll1 > 0 && turnInfo.diceRoll2 > 0) {
                              diceImageView1.image = diceImages(turnInfo.diceRoll1 - 1)
                              diceImageView2.image = diceImages(turnInfo.diceRoll2 - 1)
                            }
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

                  children = Seq(
                    rollDiceButton, buyPropertyButton, endTurnButton,
                    payJailFineButton
                  )
                }
              }

              private def updateButtonStates(): Unit = {
                gameController.foreach { ctrl =>
                  val currentState = ctrl.state

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

                    case RollingState(_) =>
                      rollDiceButton.disable = false

                    case JailState() =>
                      rollDiceButton.disable = false
                      payJailFineButton.disable = false
                      endTurnButton.disable = true

                    case MovingState(_) =>
                      gameController.foreach(_.handleInput(enter))

                    case PropertyDecisionState(_) =>
                      buyPropertyButton.disable = false
                      endTurnButton.disable = false

                    case BuyPropertyState(_) =>
                      gameController.foreach(_.handleInput(enter))

                    case AdditionalActionsState(_) =>
                      endTurnButton.disable = false

                    case BuyHouseState(_) =>

                    case ConfirmBuyHouseState(_, _) =>
                      confirmBuyHouseButton.disable = false
                      declineBuyHouseButton.disable = false

                    case EndTurnState() =>
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