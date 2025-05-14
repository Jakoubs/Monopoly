package de.htwg.controller

import de.htwg.model.{BoardPrinter, Player, SoundPlayer, *}
import de.htwg.model.PropertyField.calculateRent
import de.htwg.{Board, MonopolyGame}
import de.htwg.util.util.Observable
import de.htwg.controller.GameState
import java.awt.Choice
import scala.io.StdIn.readLine

class Controller(var game: MonopolyGame, val dice: Dice) extends Observable{
  var state: GameState = StartTurnState()
  def currentPlayer: Player = game.currentPlayer
  def board: Board = game.board
  def players: Vector[Player] = game.players
  def sound: Boolean = game.sound

  def handleInput(input: String): Unit = {
    state = state.handle(input, this)
    notifyObservers()
  }

  def updatePlayer(player: Player): Unit = {
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), player)
    game = game.copy(players = updatedPlayers, currentPlayer = player)
  }

  def updateBoardAndPlayer(field: BoardField, player: Player): Unit = {
    val updatedFields = game.board.fields.updated(field.index, field)
    val updatedBoard = game.board.copy(fields = updatedFields)
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), player)
    game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = player)
  }

  def switchToNextPlayer(): Unit = {
    val currentIndex = game.players.indexOf(game.currentPlayer)
    val nextIndex = (currentIndex + 1) % game.players.size
    val nextPlayer = game.players(nextIndex)
    game = game.copy(currentPlayer = nextPlayer)
  }

  def isGameOver: Boolean = game.players.count(_.balance > 0) <= 1

  // Other existing methods can be kept but might need adjustments
  // ...


  /*
    def handlePlayerTurn(
                          ask: String => Boolean,
                          print: String => Unit,
                          choice: () => Int,
                          idxInput: () => Int = () => -1
                        ): Unit = {
      if (game.currentPlayer.isInJail) {
        handleJailTurn(ask, print, choice)
      } else {
        handleRegularTurn(ask, print,choice,idxInput)
      }
      switchToNextPlayer()
      notifyObservers()
    }

    def handleRegularTurn(ask: String => Boolean, print: String => Unit, choice: () => Int,idxInput: () => Int = () => -1): Unit = {
      val player = game.currentPlayer
      val (dice1, dice2) = dice.rollDice(game.sound)
      //print(s"${player.name} würfelt $dice1 und $dice2 (${dice1 + dice2})")

      val updatedPlayer = player.playerMove(() => (dice1,dice2))
      val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
      game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)

      print(s"${player.name} landet auf Feld ${updatedPlayer.position}")
      handleFieldAction(ask, print,choice)
      if (updatedPlayer.isInJail) {
        return
      }
      handleOptionalActions( print, choice,idxInput)
    }

    def handleJailTurn(ask: String => Boolean, print: String => Unit, choice: () => Int): Unit = {

      val player = game.currentPlayer
      print("Du bist im Gefängnis.")
      print("1. Zahle 50$ um rauszukommen")
      print("2. (Nicht implementiert) Karte verwenden")
      print("3. Versuche Pasch zu würfeln")
      val jailChoice = choice()

      jailChoice match {
        case 1 =>
          if (player.balance >= 50) {
            val updatedPlayer = player.copy(
              isInJail = false,
              balance = player.balance - 50,
              jailTurns = 0
            )
            val updatedPlayers = game.players.updated(game.players.indexOf(player), updatedPlayer)
            game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)

            val (dice1, dice2) = dice.rollDice(game.sound)
            val diceSum = dice1 + dice2
            val newPosition = (updatedPlayer.position + diceSum) % game.board.fields.size
            handleFieldAction(ask, print, choice)
          }

        case 3 =>
          // Try rolling doubles
          val (dice1, dice2) = dice.rollDice(game.sound)
          val isDoubles = dice1 == dice2

          if (isDoubles) {
            // Get out of jail
            val diceSum = dice1 + dice2
            val newPosition = (player.position + diceSum) % game.board.fields.size
            val updatedPlayer = player.copy(
              isInJail = false,
              jailTurns = 0,
              position = newPosition
            )
            val updatedPlayers = game.players.updated(game.players.indexOf(player), updatedPlayer)
            game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
            handleFieldAction(ask, print, choice)
          } else {
            // Increment jail turns
            val jailTurns = player.jailTurns + 1
            if (jailTurns >= 3) {
              // Must pay after third attempt
              if (player.balance >= 50) {
                val updatedPlayer = player.copy(
                  isInJail = false,
                  balance = player.balance - 50,
                  jailTurns = 0
                )
                val updatedPlayers = game.players.updated(game.players.indexOf(player), updatedPlayer)
                game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)

                // Roll dice and move
                val (dice1, dice2) = dice.rollDice(game.sound)
                val diceSum = dice1 + dice2
                val newPosition = (updatedPlayer.position + diceSum) % game.board.fields.size
                handleFieldAction(ask, print, choice)
              }
            } else {
              val updatedPlayer = player.copy(jailTurns = jailTurns)
              val updatedPlayers = game.players.updated(game.players.indexOf(player), updatedPlayer)
              game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
            }
          }
        case -1 =>
          println("Ungültige Auswahl.")
          handleJailTurn(ask, print, choice)
      }
    }

    def handleFieldAction(ask: String => Boolean, print: String => Unit, choice: () => Int): Unit = {
      val field = game.board.fields(currentPlayer.position-1)

      val updatedGame = field match {
        case goToJail: GoToJailField => handleGoToJailField()
        //case taxF: TaxField => handleTaxField(game, taxF.amount)
        //case freeP: FreeParkingField => handleFreeParkingField(game, freeP)
        case pf: PropertyField => handlePropertyField(pf, ask, print, choice)
        case tf: TrainStationField => handleTrainStationField(tf,ask, print, choice)
        case uf: UtilityField => handleUtilityField(uf,ask, print, choice)
        case _ => game

      }
    }

    def handleGoToJailField(): Unit = {
      val index = game.players.indexWhere(_.name == game.currentPlayer.name)
      val updatedPlayer = game.currentPlayer.goToJail()
      val updatedPlayers = game.players.updated(index, updatedPlayer)
      game.copy(players = updatedPlayers,currentPlayer = updatedPlayer)
    }

    def handleOptionalActions(printText: String => Unit, choice: () => Int, idxInput: () => Int = () => -1): Unit = {
      printText("Möchtest du eine Weiter aktion ausführen? 1 = Haus kaufen, 2 = nichts, 3 = nichts, Enter = weiter")
      val input = choice()
      input match {
        case 1 =>
          val houseIndex = idxInput() -1
          if (houseIndex >= 1 && houseIndex <= 40) {
            buyHouse(houseIndex, printText)
          } else {
            printText("Ungültiger Index! Bitte wählen Sie eine Zahl zwischen 1 und 40.")
            //handleOptionalActions(printText, choice, idxInput)
          }
        case 2 =>
          printText("not implemented")
        case 3 =>
          printText("not implemented")
        case -1 =>
      }
    }

    def handlePropertyField(property: PropertyField, ask: String => Boolean, print: String => Unit, choice: () => Int): Unit = {
      property.owner match {
        case None =>
          val response = ask("Möchtest du das Feld kaufen?")
          if (response) {
            buyProperty(property.index, print)
            if (game.sound) {
              SoundPlayer().playAndWait("src/main/resources/Money.wav")
            }
          } else {
            print("Keine Feld gekauft!")
          }
        case Some(ownerName) if !ownerName.name.equals(game.currentPlayer.name) =>
          val rent = calculateRent(property)
          print(s"Pay ${rent}$$ rent to ${ownerName.name}")

          // Finde den zahlenden Spieler
          val playerIndex = game.players.indexWhere(_.name == currentPlayer.name)
          val updatedPlayer = currentPlayer.copy(balance = currentPlayer.balance - rent)

          // Finde den Besitzer
          val ownerIndex = game.players.indexWhere(_.name == ownerName.name)
          if (ownerIndex >= 0 && playerIndex >= 0) {
            val owner = game.players(ownerIndex)
            val updatedOwner = owner.copy(balance = owner.balance + rent)

            // Aktualisiere beide Spieler
            val updatedPlayers = game.players
              .updated(playerIndex, updatedPlayer)
              .updated(ownerIndex, updatedOwner)

            game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
          }
        case Some(_) =>
      }
    }

    def handleTrainStationField(trainstation: TrainStationField, ask: String => Boolean, print: String => Unit, choice: () => Int): Unit = {
      trainstation.owner match {
        case None =>
          val response = ask("Möchtest du das Feld kaufen?")
          if (response) {
            buyProperty(trainstation.index, print)
            if (game.sound) {
              SoundPlayer().playAndWait("src/main/resources/Money.wav")
            }
          } else {
            print("Keine Feld gekauft!")
          }
        case Some(ownerName) if !ownerName.name.equals(game.currentPlayer.name) =>
          val anzTrainStations = game.board.fields.count {
            case ts: TrainStationField => ts.owner.exists(_.name == ownerName.name)
            case _ => false
          }

          val rent = 25 * anzTrainStations
          print(s"Pay ${rent}$$ rent to ${ownerName.name}")

          // Finde den zahlenden Spieler
          val playerIndex = game.players.indexWhere(_.name == currentPlayer.name)
          val updatedPlayer = currentPlayer.copy(balance = currentPlayer.balance - rent)

          // Finde den Besitzer
          val ownerIndex = game.players.indexWhere(_.name == ownerName.name)
          if (ownerIndex >= 0 && playerIndex >= 0) {
            val owner = game.players(ownerIndex)
            val updatedOwner = owner.copy(balance = owner.balance + rent)

            // Aktualisiere beide Spieler
            val updatedPlayers = game.players
              .updated(playerIndex, updatedPlayer)
              .updated(ownerIndex, updatedOwner)

            game = game.copy(players = updatedPlayers,currentPlayer = updatedPlayer)
          }
        case Some(_) =>

      }
    }

    def handleUtilityField(utility: UtilityField, ask: String => Boolean, print: String => Unit, choice: () => Int): Unit = {
      utility.owner match {
        case None =>
          val response = ask("Möchtest du das Feld kaufen?")
          if (response) {
            buyProperty(utility.index, print)
            if (game.sound) {
              SoundPlayer().playAndWait("src/main/resources/Money.wav")
            }
          } else {
            print("Keine Feld gekauft!")
          }
        case Some(ownerName) if !ownerName.name.equals(game.currentPlayer.name) =>
          val rent = 10
          val utilityFields = game.board.fields.collect {
            case uf: UtilityField => uf
          }

          val ownsAll = utilityFields.forall(_.owner.exists(_.name == currentPlayer.name))
          if (ownsAll) {
            // not implemented yet
          }
          print(s"Pay ${rent}$$ rent to ${ownerName.name}")

          // Finde den zahlenden Spieler
          val playerIndex = game.players.indexWhere(_.name == currentPlayer.name)
          val updatedPlayer = currentPlayer.copy(balance = currentPlayer.balance - rent)

          // Finde den Besitzer
          val ownerIndex = game.players.indexWhere(_.name == ownerName.name)
          if (ownerIndex >= 0 && playerIndex >= 0) {
            val owner = game.players(ownerIndex)
            val updatedOwner = owner.copy(balance = owner.balance + rent)

            // Aktualisiere beide Spieler
            val updatedPlayers = game.players
              .updated(playerIndex, updatedPlayer)
              .updated(ownerIndex, updatedOwner)

            game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
          }
        case Some(_) =>
      }
    }


    def buyProperty(propertyIndex: Int, printText: String => Unit): Unit = {
      val fieldOption = game.board.fields.find(_.index == propertyIndex)

      fieldOption match {
        case Some(field: PropertyField) =>
          field.owner match {
            case None =>
              if (game.currentPlayer.balance >= field.price) {
                val updatedField = field.copy(
                  owner = Some(currentPlayer)
                )

                val updatedFields = game.board.fields.map { f =>
                  if (f.index == propertyIndex) updatedField else f
                }
                val updatedBoard = game.board.copy(fields = updatedFields)
                val updatedPlayer =  game.currentPlayer.copy(balance = currentPlayer.balance - field.price, position = propertyIndex)
                val updatedPlayers = game.players.map(p =>
                  if (p.name == updatedPlayer.name) updatedPlayer else p
                )
                game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = updatedPlayer)
                printText(s"${currentPlayer.name} hat die Immobilie ${field.name} für ${field.price} gekauft.")
              } else {
                printText(s"Nicht genug Geld! Die Immobilie kostet ${field.price}, aber ${currentPlayer.name} hat nur ${currentPlayer.balance}.")
              }
            case Some(owner) =>
              printText(s"Diese Immobilie gehört bereits ${owner.name}.")
          }
        case Some(field: TrainStationField) =>
          field.owner match {
            case None =>
              if (game.currentPlayer.balance >= field.price) {
                val updatedField = field.copy(
                  owner = Some(currentPlayer)
                )

                val updatedFields = game.board.fields.map { f =>
                  if (f.index == propertyIndex) updatedField else f
                }
                val updatedBoard = game.board.copy(fields = updatedFields)
                val updatedPlayer = currentPlayer.copy(balance = currentPlayer.balance - field.price, position = propertyIndex)
                val updatedPlayers = game.players.map(p =>
                  if (p.name == updatedPlayer.name) updatedPlayer else p
                )
                game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = updatedPlayer)
                printText(s"${currentPlayer.name} hat den Bahnhof ${field.name} für ${field.price} gekauft.")
              } else {
                printText(s"Nicht genug Geld! Der Bahnhof kostet ${field.price}, aber ${currentPlayer.name} hat nur ${currentPlayer.balance}.")
              }
            case Some(owner) =>
              printText(s"Dieser Bahnhof gehört bereits ${owner}.")
          }
        case Some(field: UtilityField) =>
          field.owner match {
            case None =>
              val utilityPrice = 150 // Typischer Preis für Versorgungswerke
              if (game.currentPlayer.balance >= utilityPrice) {
                val updatedField = field.copy(
                  owner = Some(currentPlayer)
                )

                val updatedFields = game.board.fields.map { f =>
                  if (f.index == propertyIndex) updatedField else f
                }
                val updatedBoard = game.board.copy(fields = updatedFields)

                val updatedPlayer = currentPlayer.copy(balance = currentPlayer.balance - utilityPrice, position = propertyIndex)
                val updatedPlayers = game.players.map(p =>
                  if (p.name == updatedPlayer.name) updatedPlayer else p
                )
                game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = updatedPlayer)
                printText(s"${currentPlayer.name} hat das Versorgungswerk ${field.name} für $utilityPrice gekauft.")
              } else {
                printText(s"Nicht genug Geld! Das Versorgungswerk kostet $utilityPrice, aber ${currentPlayer.name} hat nur ${currentPlayer.balance}.")
              }
            case Some(owner) =>
              printText(s"Dieses Versorgungswerk gehört bereits ${owner}.")
          }
        case Some(_) =>
          printText(s"Das Feld mit Index $propertyIndex kann nicht gekauft werden.")
        case None =>
          printText(s"Feld mit Index $propertyIndex nicht gefunden.")
      }
    }
    def isGameOver: Boolean = {
      game.players.count(_.balance > 0) <= 1
    }

    def buyHouse(propertyIndex: Int, printText: String => Unit): Unit = {
      if (!isValidFieldIndex(propertyIndex)) return

      game.board.fields(propertyIndex) match {
        case field: PropertyField =>
          if (field.owner.exists(_.name == currentPlayer.name)) {
            val colorGroup = game.board.fields.collect {
              case pf: PropertyField if pf.color == field.color => pf
            }
            val ownsAll = colorGroup.forall(_.owner.exists(_.name == currentPlayer.name))

            if (ownsAll && canAfford(currentPlayer, 50)) {
              updateGameWithNewHouse(field, 50)
            } else {
              printText("Entweder hast du nicht alle Immobilien dieser Farbe oder nicht genug Geld.")
            }
            printText(s"")
          } else {
            printText("Das Feld gehört dir nicht!!!")
          }
        case _ => printText("Das Feld ist keine Immobilie.")
          //handleOptionalActions(printText, choice, idxInput)
      }
    }

    private def updateGameWithNewHouse(field: PropertyField, cost: Int): Unit = {
      val updatedField = field.copy(house = PropertyField.House(field.house.amount + 1))
      val updatedFields = game.board.fields.map {
        case f if f.index == field.index => updatedField
        case f => f
      }

      val updatedPlayer = currentPlayer.copy(balance = currentPlayer.balance - cost)
      val updatedPlayers = players.map(p => if (p.name == currentPlayer.name) updatedPlayer else p)

      game = game.copy(board = game.board.copy(fields = updatedFields), players = updatedPlayers, currentPlayer = updatedPlayer)
    }

    def isValidFieldIndex(index: Int): Boolean =
      index >= 1 && index <= game.board.fields.size

    def canAfford(player: Player, amount: Int): Boolean =
      player.balance >= amount

    def switchToNextPlayer(): Unit = {
      val currentIndex = game.players.indexOf(game.currentPlayer)
      val nextIndex = (currentIndex + 1) % game.players.size
      val nextPlayer = game.players(nextIndex)
      game = game.copy(currentPlayer = nextPlayer)
    }
  */
    def getBoardString: String = {
      BoardPrinter.getBoardAsString(game)
    }
  /*
    def getInventoryString: String = {
      BoardPrinter.getInventoryString(game)
    }

    def getGameStatus: MonopolyGame = game
    def getCurrentPlayerName: String = currentPlayer.name
    def getCurrentPlayerBalance: Int = currentPlayer.balance
    def getCurrentPlayerPosition: Int = currentPlayer.position
    def isCurrentPlayerInJail: Boolean = currentPlayer.isInJail
  */

    def getCurrentPlayerStatus: String = {
      val p = currentPlayer
      s"${p.name} | Balance: ${p.balance}€ | Position: ${p.position} | " +
        s"In Jail: ${if (p.isInJail) "Yes" else "No"}"
    }


}
