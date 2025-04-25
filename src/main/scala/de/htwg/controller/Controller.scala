package de.htwg.controller

import de.htwg.model.*
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
      handleJailTurn()
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
    val updatedGame = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
    val gameRolled = handleFieldAction(updatedGame, newPosition)
    val finalGame = handleOptionalActions(gameRolled)
    finalGame
  }

}
