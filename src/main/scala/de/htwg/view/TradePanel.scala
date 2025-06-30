package de.htwg.view

import de.htwg.controller.IController
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, CheckBox, ComboBox, Dialog, Label, TextField}
import scalafx.scene.layout.{HBox, VBox}
import de.htwg.model.modelBaseImple.Trade
import de.htwg.model.modelBaseImple.Player
import de.htwg.model.modelBaseImple.BoardField
import de.htwg.model.modelBaseImple.BuyableField
import de.htwg.model.modelBaseImple.{BoardField, BuyableField, Player, PropertyField, SoundPlayer, Trade, TrainStationField, UtilityField}
import de.htwg.util.util.Observable
import scalafx.scene.control.ButtonType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.ControlIncludes.jfxDialogPane2sfx


import scala.jdk.CollectionConverters.*
import scalafx.scene.layout.FlowPane
import scalafx.scene.Node as SFXNode

class TradePanel(controller: IController) extends VBox with Observable {
  spacing = 20
  padding = Insets(15)
  style = "-fx-background-color: #333333; -fx-border-color: #5cb85c; -fx-border-width: 2;"

  private val titleLabel = new Label("Handelssystem") {
    style = "-fx-font: bold 20pt sans-serif; -fx-text-fill: white;"
  }

  private var otherPlayerComboBox: ComboBox[String] = _
  private var playerSelectionBox: VBox = _
  private var offerBox: VBox = _
  private var demandBox: VBox = _
  private var moneyFieldOffer: TextField = _
  private var moneyFieldDemand: TextField = _
  private var actionButtonsBox: HBox = _


  // Method to refresh/rebuild the panel
  def refreshPanel(): Unit = {
    children.clear() // Clear existing children before rebuilding
    buildPanel()
  }

  private def propertyColorClass(field: BoardField): String = field match {
    case pf: PropertyField =>
      pf.color.toString.toLowerCase match {
        case "braun" | "brown"     => "brown"
        case "hellblau" | "lightblue" => "lightblue"
        case "pink" | "violett"    => "purple"
        case "orange"              => "orange"
        case "rot" | "red"         => "red"
        case "gelb" | "yellow"     => "yellow"
        case "grün" | "green"      => "green"
        case "dunkelblau" | "darkblue" => "darkblue"
        case _                     => "neutral"
      }
    case _: TrainStationField => "black"
    case _: UtilityField      => "neutral"
    case _                   => "neutral"
  }

  private def createMonopolyCheckbox(property: BuyableField): VBox = {
    val checkbox = new CheckBox(property.name) {
      styleClass += "check-box"
      style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
    }
    checkbox.selected.onChange { (_, _, selected) =>
      SoundPlayer().playBackground("src/main/resources/sound/select.wav")
    }

    val fieldTitle = new Label(property.name) {
      styleClass += "field-title"
    }

    val fieldValue = new Label(s"${property.price}€") {
      styleClass += "field-value"
    }

    val hakenLabel = new Label("✔") {
      styleClass += "haken-label"
      visible = false
    }

    val customVisualContainer = new VBox {
      alignment = Pos.Center
      spacing = 5
      children = Seq(fieldTitle, fieldValue, hakenLabel)
    }

    val container = new VBox {
      styleClass ++= Seq("monopoly-checkbox", propertyColorClass(property))
      alignment = Pos.Center
      spacing = 5
      children = Seq(customVisualContainer, checkbox)
    }

    container.onMouseClicked = _ => checkbox.selected.value = !checkbox.selected.value

    checkbox.selected.onChange { (_, _, selected) =>
      hakenLabel.visible = selected
      if (selected) container.styleClass += "selected"
      else container.styleClass -= "selected"
    }

    container
  }

  private def buildPanel(): Unit = {
    // Re-initialize otherPlayerComboBox to ensure items are fresh
    otherPlayerComboBox = new ComboBox[String] {
      // Update items with current players, excluding the current player
      items = ObservableBuffer(controller.players.filter(_ != controller.currentPlayer).map(_.name): _*)
      promptText = "Wähle einen Spieler"
      prefWidth = 200
      style = "-fx-font: normal 12pt sans-serif;"
    }

    // Player selection section
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

      private val propertiesBox = new VBox {
        spacing = 10
        alignment = Pos.TopLeft
        // Ensure this always shows the *current player's* properties
        children = controller.currentPlayer.getProperties(controller.board.fields).map { property =>
          new CheckBox(property.name) {
            style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
          }
        }
      }

      children = Seq(moneyLabel, moneyFieldOffer, propertiesLabel, propertiesBox)
    }

    moneyFieldDemand = new TextField {
      promptText = "Betrag eingeben"
      prefWidth = 150
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

      private val propertiesBox = new VBox {
        spacing = 10
        alignment = Pos.TopLeft
        // Initial empty, will be populated by ComboBox action
      }

      // This is crucial: the ComboBox action updates the properties for the *selected* player
      otherPlayerComboBox.onAction = _ => {
        SoundPlayer().playBackground("src/main/resources/sound/select2.wav")
        propertiesBox.children.clear()
        val selectedPlayerName = Option(otherPlayerComboBox.value.value).filter(_.nonEmpty)

        selectedPlayerName.foreach { name =>
          controller.players.find(_.name == name).foreach { selectedPlayer =>
            propertiesBox.children = selectedPlayer.getProperties(controller.board.fields).map { property =>
              new CheckBox(property.name) {
                style = "-fx-font: normal 12pt sans-serif; -fx-text-fill: white;"
              }
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

          SoundPlayer().playBackground("src/main/resources/sound/click.wav")
          val selectedPlayerOption = Option(otherPlayerComboBox.value.value).filter(_.nonEmpty)

          selectedPlayerOption match {
            case Some(selectedPlayerName) =>
              val selectedPlayer = controller.players.find(_.name == selectedPlayerName).getOrElse(controller.currentPlayer)

              val offerAmount = try { moneyFieldOffer.text.value.toInt } catch { case _: NumberFormatException => 0 }
              val demandAmount = try { moneyFieldDemand.text.value.toInt } catch { case _: NumberFormatException => 0 }
              if (!(result.get == false)) {
                Trade().tradeCall(
                  controller.currentPlayer.asInstanceOf[Player], // The current player initiating the trade
                  selectedPlayer.asInstanceOf[Player],
                  offerAmount,
                  demandAmount
                ) match {
                  case Some((p1, p2)) =>
                    controller.updatePlayer(p1)
                    val updatedPlayers = controller.players.map { playerInList =>
                      if (playerInList.name == p2.name) {
                        p2
                      } else {
                        playerInList
                      }
                    }
                    controller.updatePlayers(updatedPlayers)
                    refreshPanel()
                    notifyObservers()
                  case None =>
                    // Handel fehlgeschlagen, keine Aktion erforderlich
                    new Alert(AlertType.Warning) {
                      title = "Warnung"
                      headerText = "Handel fehlgeschlagen"
                      contentText = "Der Handel konnte nicht durchgeführt werden."
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

  // Initialize the panel by calling buildPanel()
  buildPanel()

  // Public method to refresh the panel from outside
  def refresh(): Unit = {
    refreshPanel()
  }
}