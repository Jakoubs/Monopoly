package de.htwg.controller

import de.htwg.model.*
import de.htwg.controller.TurnInfo
import de.htwg.util.util.Observable
import de.htwg.controller._

sealed trait GameState {
  def handle(input: String, controller: Controller): GameState
}

// Initial state when a player starts their turn
case class StartTurnState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val player = controller.currentPlayer.resetDoubles()
    controller.updatePlayer(player)
    
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
          val command = PayJailFeeCommand(controller, controller.currentPlayer)
          command.execute()
          RollingState()
        } else {
          this // Stay in jail if can't pay
        }
      case "3" => // Try to roll doubles
        val strategy = JailTurnStrategy()
        val updatedPlayer = strategy.executeTurn(controller.currentPlayer, () => controller.dice.rollDice(controller.sound))
        controller.updatePlayer(updatedPlayer)
        if (!updatedPlayer.isInJail) {
          MovingState(() => controller.dice.rollDice(controller.sound))
        } else {
          this
        }
      case _ => this
    }
  }
}

// State when player is rolling dice
case class RollingState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val command = RollDiceCommand(controller)
    command.execute()
    val (d1, d2) = command.getResult
    val isDouble = d1 == d2

    // Update turn info
    controller.updateTurnInfo(
      TurnInfo(
        diceRoll1 = d1,
        diceRoll2 = d2
      )
    )
    controller.notifyObservers()

    if (isDouble) {
      val player = controller.currentPlayer
      val updatedPlayer = player.incrementDoubles()
      controller.updatePlayer(updatedPlayer)

      if (updatedPlayer.consecutiveDoubles >= 3) {
        val jailedPlayer = updatedPlayer.goToJail()
        controller.updatePlayer(jailedPlayer)
        return EndTurnState()
      }
    } else {

      val updatedPlayer = controller.currentPlayer.resetDoubles()
      controller.updatePlayer(updatedPlayer)
    }

    MovingState(() => (d1, d2))
  }
}

// State when player is moving
case class MovingState(dice: () => (Int, Int)) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val strategy = if (controller.currentPlayer.isInJail) {
      JailTurnStrategy()
    } else {
      RegularTurnStrategy()
    }

    val (d1, d2) = dice()
    val isDouble = d1 == d2

    val updatedPlayer = strategy.executeTurn(controller.currentPlayer,() => (d1, d2))
    controller.updatePlayer(updatedPlayer)

    controller.board.fields(updatedPlayer.position-1) match {
      case _: PropertyField | _: TrainStationField | _: UtilityField =>
        PropertyDecisionState(isDouble)
      case _: GoToJailField =>
        val jailedPlayer = updatedPlayer.goToJail()
        controller.updatePlayer(jailedPlayer)
        EndTurnState()
      case _ =>
        AdditionalActionsState(isDouble)
    }
  }
}


// State when player needs to decide whether to buy a property
case class PropertyDecisionState(isDouble: Boolean = false) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    
      input.toLowerCase match {
        case "y" | "j" => // Yes/ja
          BuyPropertyState(isDouble)
        case _ => // No
          AdditionalActionsState(isDouble)
      }
  }
}

// State when buying a property
case class BuyPropertyState(isDouble: Boolean = false) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val field = controller.board.fields(controller.currentPlayer.position-1)/*Hallo*/
    field match {
      case pf: PropertyField =>
        val command = BuyPropertyCommand(controller, pf, controller.currentPlayer)
        command.execute()
        AdditionalActionsState(isDouble)
      case tf: TrainStationField =>
        val command = BuyTrainStationCommand(controller,tf, controller.currentPlayer)
        command.execute()
        AdditionalActionsState(isDouble)
      case uf: UtilityField =>
        val command = BuyUtilityCommand(controller, uf, controller.currentPlayer)
        command.execute()
        AdditionalActionsState(isDouble)
      case _ =>
        AdditionalActionsState(isDouble)
    }
  }
}

// State for additional actions after moving
case class AdditionalActionsState(isDouble: Boolean = false) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    input match {
      case "1" => // Buy house
        BuyHouseState(isDouble)
      case _ =>
        if (isDouble) {
          RollingState()
        } else {
          EndTurnState()
        }
    }
  }
}

// State when buying a house
case class BuyHouseState(isDouble: Boolean = false) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    controller.game.board.fields(input.toInt - 1) match {
      case field: PropertyField =>
        val command = BuyHouseCommand(controller, field, controller.currentPlayer)
        command.execute()
        ConfirmBuyHouseState(isDouble, command)
      case _ =>
        if (isDouble) {
          RollingState()
        } else {
          EndTurnState()
        }
    }
  }
}

case class ConfirmBuyHouseState(isDouble: Boolean = false, command: Command) extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    input match {
      case  "y" =>
        command.undo()
        AdditionalActionsState(isDouble)
      case _ =>
        if (isDouble) {
          RollingState()
        } else {
          EndTurnState()
        }
    }
  }
}

// State when turn ends
case class EndTurnState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    controller.switchToNextPlayer()
    StartTurnState()
  }
}
