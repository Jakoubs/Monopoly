package de.htwg.view

import de.htwg.controller.IController
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, CheckBox, ComboBox, Dialog, Label, TextField}
import de.htwg.model.modelBaseImple.Trade
import de.htwg.model.modelBaseImple.Player
import de.htwg.model.modelBaseImple.BoardField
import de.htwg.model.modelBaseImple.BuyableField
import de.htwg.util.util.Observable
import scalafx.scene.control.ButtonType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.ControlIncludes.jfxDialogPane2sfx


class TradePanel(controller: IController) extends VBox with Observable {
  spacing = 20
  padding = Insets(15)
  style = "-fx-background-color: #333333; -fx-border-color: #5cb85c; -fx-border-width: 2;"

  private val titleLabel = new Label("Handelssystem") {
    style = "-fx-font: bold 20pt sans-serif; -fx-text-fill: white;"
  }

  this.getStylesheets.add(classOf[TradePanel].getResource("/style/monopoly-checkboxes.css").toExternalForm)

  private var otherPlayerComboBox: ComboBox[String] = _
  private var playerSelectionBox: VBox = _
  private var offerBox: VBox = _
  private var demandBox: VBox = _
  private var moneyFieldOffer: TextField = _
  private var moneyFieldDemand: TextField = _
  private var actionButtonsBox: HBox = _

  def refreshPanel(): Unit = {
    children.clear()
    buildPanel()
  }
  private def buildPanel(): Unit = {
    otherPlayerComboBox = new ComboBox[String] {
      items = ObservableBuffer(controller.players.filter(_ != controller.currentPlayer).map(_.name): _*)
      promptText = "Wähle einen Spieler"
      prefWidth = 200
      style = "-fx-font: normal 12pt sans-serif;"
    }

    playerSelectionBox = new VBox {
      spacing = 10
      alignment = Pos.Center
      style = "-fx-background-color: #444444; -fx-padding: 10px;"

      private val playerSelectionLabel = new Label("Handelspartner auswählen:") {
        style = "-fx-font: normal 14pt sans-serif; -fx-text-fill: white;"
      }

      children = Seq(playerSelectionLabel, otherPlayerComboBox)
    }

    moneyFieldOffer = new TextField {
      promptText = "Betrag eingeben"
      prefWidth = 150
      text = "0"
    }

    offerBox = new VBox {
      spacing = 10
      alignment = Pos.TopCenter
      style = "-fx-background-color: #444444; -fx-padding: 10px;"
      minWidth = 300
      minHeight = 400

      private val moneyLabel = new Label("Geld anbieten:") {
        style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
      }

      private val propertiesLabel = new Label("Deine Besitztümer:") {
        style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
      }

      private val propertiesBox = new VBox { // Geändert von FlowPane zu VBox, da FlowPane-Import entfernt wurde
        spacing = 8
        alignment = Pos.TopLeft
        children = controller.currentPlayer.getProperties(controller.board.fields).map { property =>
          createMonopolyCheckbox(property)
        }
      }

      children = Seq(moneyLabel, moneyFieldOffer, propertiesLabel, propertiesBox)
    }

    moneyFieldDemand = new TextField {
      promptText = "Betrag eingeben"
      prefWidth = 150
      text = "0"
    }

    demandBox = new VBox {
      spacing = 10
      alignment = Pos.TopCenter
      style = "-fx-background-color: #444444; -fx-padding: 10px;"
      minWidth = 300
      minHeight = 400

      private val moneyLabel = new Label("Geld verlangen:") {
        style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
      }

      private val propertiesLabel = new Label("Besitztümer des Spielers:") {
        style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
      }

      private val propertiesBox = new VBox { // Geändert von FlowPane zu VBox
        spacing = 8
        alignment = Pos.TopLeft
      }

      otherPlayerComboBox.onAction = _ => {
        propertiesBox.children.clear()
        val selectedPlayerName = Option(otherPlayerComboBox.value.value).filter(_.nonEmpty)

        selectedPlayerName.foreach { name =>
          controller.players.find(_.name == name).foreach { selectedPlayer =>
            propertiesBox.children = selectedPlayer.getProperties(controller.board.fields).map { property =>
              createMonopolyCheckbox(property)
            }
          }
        }
      }

      children = Seq(moneyLabel, moneyFieldDemand, propertiesLabel, propertiesBox)
    }

    val tradeBoxesContainer = new HBox {
      spacing = 20
      alignment = Pos.Center
      children = Seq(offerBox, demandBox)
    }

    actionButtonsBox = new HBox {
      spacing = 30
      alignment = Pos.Center

      private val confirmButton = new Button("Handel vorschlagen") {
        style = "-fx-font: normal bold 14pt sans-serif; -fx-background-color: #5cb85c; -fx-text-fill: white;"
        prefWidth = 200
        prefHeight = 40
        onAction = _ => {
          val confirmButtonType = new ButtonType("Bestätigen", ButtonData.OKDone)
          val cancelButtonType = new ButtonType("Ablehnen", ButtonData.CancelClose)
          val dialog = new Dialog[Boolean]() {
            title = "Handelsvorschlag"
            contentText = s"Handel mit ${controller.currentPlayer.name}: Er bietet ${moneyFieldOffer.text.value.toInt}€, du giebst ${moneyFieldDemand.text.value.toInt}€"
            dialogPane().buttonTypes = Seq(confirmButtonType, cancelButtonType)
          }
          dialog.dialogPane().getStylesheets.add(getClass.getResource("/style/dark_mode.css").toExternalForm)
          dialog.dialogPane().getStyleClass.add("dark-dialog-pane")
          dialog.resultConverter = dialogButton => {
            if (dialogButton == confirmButtonType) true else false
          }

          val result = dialog.showAndWait()

          val selectedPlayerOption = Option(otherPlayerComboBox.value.value).filter(_.nonEmpty)

          selectedPlayerOption match {
            case Some(selectedPlayerName) =>
              val selectedPlayer = controller.players.find(_.name == selectedPlayerName).getOrElse(controller.currentPlayer)

              val offerAmount = try {
                val text = moneyFieldOffer.text.value.trim
                if (text.isEmpty) 0 else text.toInt
              } catch {
                case _: NumberFormatException => 0
              }

              val demandAmount = try {
                val text = moneyFieldDemand.text.value.trim
                if (text.isEmpty) 0 else text.toInt
              } catch {
                case _: NumberFormatException => 0
              }

              def findSelectedCheckBoxesRecursive(node: scalafx.scene.Node): List[String] = {
                node match {
                  case cb: CheckBox if cb.selected.value =>
                    List(cb.text.value)
                  case vbox: VBox =>
                    vbox.children.toList.flatMap {
                      case n: javafx.scene.control.CheckBox => findSelectedCheckBoxesRecursive(new CheckBox(n))
                      case n: javafx.scene.layout.VBox     => findSelectedCheckBoxesRecursive(new VBox(n))
                      case n: javafx.scene.layout.HBox     => findSelectedCheckBoxesRecursive(new HBox(n))
                      case n: javafx.scene.layout.FlowPane => findSelectedCheckBoxesRecursive(new FlowPane(n)) // Dies ist ein Überbleibsel, da FlowPane entfernt wurde
                      case _ => Nil
                    }
                  case hbox: HBox =>
                    hbox.children.toList.flatMap {
                      case n: javafx.scene.control.CheckBox => findSelectedCheckBoxesRecursive(new CheckBox(n))
                      case n: javafx.scene.layout.VBox     => findSelectedCheckBoxesRecursive(new VBox(n))
                      case n: javafx.scene.layout.HBox     => findSelectedCheckBoxesRecursive(new HBox(n))
                      case n: javafx.scene.layout.FlowPane => findSelectedCheckBoxesRecursive(new FlowPane(n)) // Dies ist ein Überbleibsel, da FlowPane entfernt wurde
                      case _ => Nil
                    }
                  case _ => // Entfernt den FlowPane-Case, da FlowPane nicht mehr importiert wird
                    List.empty
                }
              }

              val selectedOfferPropertyNames = findSelectedCheckBoxesRecursive(offerBox)
              val listOffer = selectedOfferPropertyNames.flatMap { propName =>
                controller.currentPlayer.getProperties(controller.board.fields)
                  .find(_.name == propName)
              }

              val selectedDemandPropertyNames = findSelectedCheckBoxesRecursive(demandBox)
              val listDemand = selectedDemandPropertyNames.flatMap { propName =>
                selectedPlayer.getProperties(controller.board.fields)
                  .find(_.name == propName)
              }

              val confirmButtonType = new ButtonType("Bestätigen", ButtonData.OKDone)
              val cancelButtonType = new ButtonType("Ablehnen", ButtonData.CancelClose)
              val dialog = new Dialog[Boolean]() {
                title = "Handelsvorschlag"
                val offerDisplay = if (offerAmount > 0) s"${offerAmount}€" else "0€"
                val demandDisplay = if (demandAmount > 0) s"${demandAmount}€" else "0€"
                val offerPropsText = if (listOffer.nonEmpty) s" + ${listOffer.map(_.name).mkString(", ")}" else ""
                val demandPropsText = if (listDemand.nonEmpty) s" + ${listDemand.map(_.name).mkString(", ")}" else ""

                contentText = s"Handel mit $selectedPlayerName:\nDu bietest: ${offerDisplay}${offerPropsText}\nDu verlangst: ${demandDisplay}${demandPropsText}"
                dialogPane().buttonTypes = Seq(confirmButtonType, cancelButtonType)
              }

              dialog.dialogPane().getStylesheets.add(classOf[TradePanel].getResource("/style/dark_mode.css").toExternalForm)
              dialog.dialogPane().getStyleClass.add("dark-dialog-pane")
              dialog.resultConverter = dialogButton => {
                if (dialogButton == confirmButtonType) true else false
              }

              val result = dialog.showAndWait()

              if (result.contains(true)) {
                val tradeResult = Trade().tradeCall(
                  controller.currentPlayer.asInstanceOf[Player],
                  selectedPlayer.asInstanceOf[Player],
                  offerAmount,
                  demandAmount,
                  listOffer,
                  listDemand,
                  controller.board
                )

                tradeResult match {
                  case Some((p1, p2, updatedBoard)) =>
                    controller.updatePlayer(p1)
                    controller.setBoard(updatedBoard)
                    val updatedPlayers = controller.players.map {
                      case player if player.name == p2.name => p2
                      case player => player
                    }
                    controller.updatePlayers(updatedPlayers)
                    refreshPanel()
                    notifyObservers()

                    new Alert(AlertType.Information) {
                      title = "Handel erfolgreich"
                      headerText = "Handel abgeschlossen"
                      contentText = "Der Handel wurde erfolgreich durchgeführt."
                    }.showAndWait()

                  case None =>
                    new Alert(AlertType.Warning) {
                      title = "Warnung"
                      headerText = "Handel fehlgeschlagen"
                      contentText = "Der Handel konnte nicht durchgeführt werden. Überprüfen Sie, ob Sie genug Geld haben."
                    }.showAndWait()
                }
              }

              refreshPanel()

            case None =>
              new Alert(AlertType.Warning) {
                title = "Warnung"
                headerText = "Kein Spieler ausgewählt"
                contentText = "Bitte wähle einen Handelspartner aus der Liste aus."
              }.showAndWait()
          }
        }
      }
      children = Seq(confirmButton)
    }

    children = Seq(titleLabel, playerSelectionBox, tradeBoxesContainer, actionButtonsBox)
  }

  buildPanel()

  def refresh(): Unit = {
    refreshPanel()
  }
}