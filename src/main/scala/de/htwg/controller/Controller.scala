package de.htwg.controller

import de.htwg.model.*
import de.htwg.{Board, MonopolyGame}
import de.htwg.util.util.Observable

import scala.io.StdIn.readLine
import scala.util.Random

class Controller(var game: MonopolyGame, val dice: Dice) extends Observable{

  def currentPlayer: Player = game.currentPlayer
  def board: Board = game.board
  def players: Vector[Player] = game.players
  def sound: Boolean = game.sound

  def handlePlayerTurn(): Unit = {
    if (game.currentPlayer.isInJail) {
      handleJailTurn()
    } else {
      readLine("Press ENTER to roll a dice")
      handleRegularTurn()
    }
    switchToNextPlayer()
    notifyObservers()
  }

  def handleRegularTurn(): Unit = {
    val player = game.currentPlayer
    val (dice1, dice2) = dice.rollDice(game.sound)
    val diceSum = dice1 + dice2

    val newPosition = (player.position + diceSum - 1) % game.board.fields.size + 1
    val updatedPlayer = player.copy(position = newPosition)
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
    game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
    handleFieldAction(newPosition)
    notifyObservers()
  }

  def handleJailTurn(): Unit = {

    val player = game.currentPlayer

    // Handle jail options (this is simplified - expand as needed)
    val jailChoice = 3  // This would come from the view in a real implementation

    jailChoice match {
      case 1 =>
        // Pay €50 to get out
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
          handleFieldAction(newPosition)
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
          handleFieldAction(newPosition)
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
              handleFieldAction(newPosition)
            }
          } else {
            val updatedPlayer = player.copy(jailTurns = jailTurns)
            val updatedPlayers = game.players.updated(game.players.indexOf(player), updatedPlayer)
            game = game.copy(players = updatedPlayers)
          }
        }
    }
  }

  def handleFieldAction(position: Int): Unit = {
    val field = game.board.fields.find(_.index == position).getOrElse(throw new Exception(s"Field at position $position not found"))

    val updatedGame = field match {
      case goToJail: GoToJailField => handleGoToJailField()
      //case taxF: TaxField => handleTaxField(game, taxF.amount)
      //case freeP: FreeParkingField => handleFreeParkingField(game, freeP)
      //case pf: PropertyField => handlePropertyField(game, pf)
      //case tf: TrainStationField => handlePropertyField(game, tf)

      case _ => game
    }
  }

  def handleGoToJailField(): Unit = {
    val index = game.players.indexWhere(_.name == game.currentPlayer.name)
    val updatedPlayer = game.currentPlayer.goToJail()
    val updatedPlayers = game.players.updated(index, updatedPlayer)
    game.copy(players = updatedPlayers,currentPlayer = updatedPlayer)
  }

  def handleOptionalActions(input: Int, fieldIndex: Int): Unit = {

    input match {
      case 1 =>
        buyHouse(fieldIndex)
      case 2 =>
        println("not implemented")
      case 3 =>
        println("not implemented")
      case -1 =>
    }
  }

  def isGameOver: Boolean = {
    game.players.count(_.balance > 0) <= 1
  }

  def buyHouse(propertyIndex: Int): Unit = {
    if (!isValidFieldIndex(propertyIndex)) return

    game.board.fields.find(_.index == propertyIndex) match {
      case Some(field: PropertyField) =>
        if (field.owner.exists(_.name == currentPlayer.name)) {
          val colorGroup = game.board.fields.collect {
            case pf: PropertyField if pf.color == field.color => pf
          }
          val ownsAll = colorGroup.forall(_.owner.exists(_.name == currentPlayer.name))

          if (ownsAll && canAfford(currentPlayer, 50)) {
            updateGameWithNewHouse(field, 50)
          }
        }
      case _ => // Nicht gültiges Feld
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
    notifyObservers()
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
    notifyObservers()
  }
}
