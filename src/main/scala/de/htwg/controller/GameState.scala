package de.htwg.controller

import de.htwg.model._
import de.htwg.util.util.Observable

sealed trait GameState {
  def handle(input: String, controller: Controller): GameState
}

// Initial state when a player starts their turn
case class StartTurnState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    if (controller.currentPlayer.isInJail) {
      JailState()
    } else {
      RollingState()
    }
  }
}

// State when player is in jail
case class JailState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    input match {
      case "1" => // Pay to get out
        if (controller.currentPlayer.balance >= 50) {
          val updatedPlayer = controller.currentPlayer.copy(
            isInJail = false,
            balance = controller.currentPlayer.balance - 50,
            jailTurns = 0
          )
          controller.updatePlayer(updatedPlayer)
          RollingState()
        } else {
          this // Stay in jail if can't pay
        }
      case "3" => // Try to roll doubles
        val (d1, d2) = controller.dice.rollDice(controller.sound)
        if (d1 == d2) {
          val updatedPlayer = controller.currentPlayer.copy(
            isInJail = false,
            jailTurns = 0
          )
          controller.updatePlayer(updatedPlayer)
          MovingState(d1 + d2)
        } else {
          val jailTurns = controller.currentPlayer.jailTurns + 1
          if (jailTurns >= 3) {
            if (controller.currentPlayer.balance >= 50) {
              val updatedPlayer = controller.currentPlayer.copy(
                isInJail = false,
                balance = controller.currentPlayer.balance - 50,
                jailTurns = 0
              )
              controller.updatePlayer(updatedPlayer)
              RollingState()
            } else {
              this // Stay in jail
            }
          } else {
            val updatedPlayer = controller.currentPlayer.copy(jailTurns = jailTurns)
            controller.updatePlayer(updatedPlayer)
            this
          }
        }
      case _ => this
    }
  }
}

// State when player is rolling dice
case class RollingState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val (d1, d2) = controller.dice.rollDice(controller.sound)
    MovingState(d1 + d2)
  }
}

// State when player is moving
case class MovingState(steps: Int) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val player = controller.currentPlayer
    val newPosition = (player.position + steps) % controller.board.fields.size
    val updatedPlayer = player.copy(position = newPosition)
    controller.updatePlayer(updatedPlayer)

    controller.board.fields(newPosition) match {
      case _: PropertyField | _: TrainStationField | _: UtilityField =>
        PropertyDecisionState()
      case _: GoToJailField =>
        val jailedPlayer = updatedPlayer.goToJail()
        controller.updatePlayer(jailedPlayer)
        EndTurnState()
      case _ =>
        EndTurnState()
    }
  }
}

// State when player needs to decide whether to buy a property
case class PropertyDecisionState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    input.toLowerCase match {
      case "y" | "j" => // Yes/ja
        BuyPropertyState()
      case _ => // No
        EndTurnState()
    }
  }
}

// State when buying a property
case class BuyPropertyState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val field = controller.board.fields(controller.currentPlayer.position)
    field match {
      case pf: PropertyField if controller.currentPlayer.balance >= pf.price =>
        val updatedField = pf.copy(owner = Some(controller.currentPlayer))
        val updatedPlayer = controller.currentPlayer.copy(balance = controller.currentPlayer.balance - pf.price)
        controller.updateBoardAndPlayer(updatedField, updatedPlayer)
        EndTurnState()
      case _ =>
        EndTurnState()
    }
  }
}

// State for additional actions after moving
case class AdditionalActionsState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    input match {
      case "1" => // Buy house
        BuyHouseState()
      case _ =>
        EndTurnState()
    }
  }
}

// State when buying a house
case class BuyHouseState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    // Implementation for buying a house
    EndTurnState()
  }
}

// State when turn ends
case class EndTurnState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    controller.switchToNextPlayer()
    StartTurnState()
  }
}