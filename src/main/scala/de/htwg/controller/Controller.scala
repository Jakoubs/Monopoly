package de.htwg.controller

import de.htwg.model._
import de.htwg.model.Monopoly.*
import de.htwg.util.util.Observable

import scala.util.Random

class Controller(var game: MonopolyGame) extends Observable{

  def currentPlayer: Player = game.currentPlayer
  def board: Board = game.board
  def players: Vector[Player] = game.players
  def sound: Boolean = game.sound

  def handlePlayerTurn(): Unit = {
    if (game.currentPlayer.isInJail) {
      //handleJailTurn()
    } else {
      handleRegularTurn()
    }
    notifyObservers()
  }

  def handleRegularTurn(): Unit = {
    val player = game.currentPlayer
    val (dice1, dice2) = Dice().rollDice(game.sound)
    val diceSum = dice1 + dice2

    val newPosition = (player.position + diceSum - 1) % game.board.fields.size + 1
    val updatedPlayer = player.copy(position = newPosition)
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
    game = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
    handleFieldAction(newPosition)
    handleOptionalActions()
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

          val (dice1, dice2) = Dice().rollDice(game.sound)
          val diceSum = dice1 + dice2
          val newPosition = (updatedPlayer.position + diceSum) % game.board.fields.size
          handleFieldAction(newPosition)
        }

      case 3 =>
        // Try rolling doubles
        val (dice1, dice2) = Dice().rollDice(game.sound)
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
              val (dice1, dice2) = Dice().rollDice(game.sound)
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
      case goToJail: GoToJailField => handleGoToJailField(game)
      case taxF: TaxField => handleTaxField(game, taxF.amount)
      case freeP: FreeParkingField => handleFreeParkingField(game, freeP)
      case pf: PropertyField => handlePropertyField(game, pf)
      //case tf: TrainStationField => handlePropertyField(game, tf)
      case _ => game
    }
  }

  def handleOptionalActions(): Unit = {
    val input = 1 // normalerweie von view bekommen
    val fieldIndex = 10 // später irgendwie implementieren
    input match {
      case 1 =>
        buyHouse(fieldIndex)
        handleOptionalActions()
      case 2 =>
        handleOptionalActions()
      case 3 =>
        handleOptionalActions()
      case 4 => game
      case _ =>
        handleOptionalActions()
    }
  }


  def buyHouse(propertyIndex: Int): Unit = {
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        field.owner match {
          case Some(owner) if owner.name == game.currentPlayer.name =>
            val colorProperties = game.board.fields.collect {
              case pf: PropertyField if pf.color == field.color => pf
            }

            val playerOwnsAllProperties = colorProperties.forall(_.owner.exists(_.name == game.currentPlayer.name))

            if (playerOwnsAllProperties) {
              val houseCost = 50
              if (game.currentPlayer.balance >= houseCost) {
                val updatedField = field.copy(
                  house = PropertyField.House(field.house.amount + 1)
                )

                val updatedFields = game.board.fields.map { f =>
                  if (f.index == propertyIndex) updatedField else f
                }

                val updatedBoard = game.board.copy(fields = updatedFields)
                val updatedPlayer = game.currentPlayer.copy(
                  balance = game.currentPlayer.balance - houseCost
                )

                val updatedPlayers = game.players.map(p =>
                  if (p.name == updatedPlayer.name) updatedPlayer else p
                )

                game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = updatedPlayer)
              }

            }
            notifyObservers()

          case _ => // Not the owner or not owned
        }
      case _ => // Not a property field
    }
  }
}
