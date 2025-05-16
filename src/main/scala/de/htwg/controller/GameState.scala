package de.htwg.controller

import de.htwg.model.*
import de.htwg.controller.TurnInfo
import de.htwg.util.util.Observable
import de.htwg.controller.*
import de.htwg.controller.StartTurnState // Import des GameState-Traits
import de.htwg.controller.RollingState // Import des RollingState (falls verwendet)
import de.htwg.controller.JailState // Import des JailState (falls verwendet)
import de.htwg.controller.MovingState // Import des MovingState (falls verwendet)
import de.htwg.controller.PayJailHandler
import de.htwg.controller.RollDoublesJailHandler
import de.htwg.controller.InvalidJailInputHandler


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

// Der GameState für den Jail-Zustand, der die Kette verwendet
case class JailState() extends GameState {
  override def handle(input: String, controller: Controller): GameState = {
    val payHandler = PayJailHandler(controller)
    val rollHandler = RollDoublesJailHandler(controller)
    val invalidHandler = InvalidJailInputHandler(controller)

    // Aufbau der Kette
    payHandler.setNext(rollHandler).setNext(invalidHandler)

    // Start der Kette und Behandlung der Eingabe
    payHandler.handle(input).getOrElse(this) // Fallback auf den aktuellen Zustand, falls die Kette None zurückgibt (was im obigen Aufbau nicht passieren sollte)
  }
}

// State when player is rolling dice
case class RollingState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val command = RollDiceCommand(controller)
    command.execute()
    val (d1, d2) = command.getResult

    controller.updateTurnInfo(
      TurnInfo(
        diceRoll1 = d1,
        diceRoll2 = d2
      )
    )
    controller.notifyObservers()
    MovingState(() => (d1 , d2))
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

    val updatedPlayer = strategy.executeTurn(controller.currentPlayer, dice)
    controller.updatePlayer(updatedPlayer)

    controller.board.fields(updatedPlayer.position-1) match {
      case _: PropertyField | _: TrainStationField | _: UtilityField =>
        PropertyDecisionState()
      case _: GoToJailField =>
        val jailedPlayer = updatedPlayer.goToJail()
        controller.updatePlayer(jailedPlayer)
        EndTurnState()
      case _ =>
        AdditionalActionsState()
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
        AdditionalActionsState()
    }
  }
}

// State when buying a property
case class BuyPropertyState() extends GameState {
  def handle(input: String, controller: Controller): GameState = {
    val field = controller.board.fields(controller.currentPlayer.position-1)/*Hallo*/
    field match {
      case pf: PropertyField =>
        val command = BuyPropertyCommand(controller, pf, controller.currentPlayer)
        command.execute()
        AdditionalActionsState()
      case tf: TrainStationField =>
        val command = BuyTrainStationCommand(controller,tf, controller.currentPlayer)
        command.execute()
        AdditionalActionsState()
      case uf: UtilityField =>
        val command = BuyUtilityCommand(controller, uf, controller.currentPlayer)
        command.execute()
        AdditionalActionsState()
      case _ =>
        AdditionalActionsState()
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
    controller.game.board.fields(input.toInt - 1) match {
      case field: PropertyField =>
        val command = BuyHouseCommand(controller, field, controller.currentPlayer)
        command.execute()
        AdditionalActionsState()
      case _ =>
        EndTurnState()
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
