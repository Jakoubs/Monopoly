package de.htwg.controller.controllerBaseImpl

import OpEnum.roll
import de.htwg.controller.*
import de.htwg.model.IPlayer

abstract class ActionHandler {
  var nextHandler: Option[ActionHandler] = None

  def handle(input: OpEnum)(using controller: Controller): Option[GameState]

  def setNext(handler: ActionHandler): ActionHandler = {
    this match {
      case h: ActionHandler =>
        h.nextHandler match {
          case Some(next) => next.setNext(handler)
          case None => h.nextHandler = Some(handler); handler
        }
    }
    this
  }
}

case class PayJailHandler() extends ActionHandler {
  override def handle(input: OpEnum)(using controller: Controller): Option[GameState] = {
    if (input == OpEnum.pay) {
      if (controller.currentPlayer.balance >= 50) {
        given IPlayer = controller.currentPlayer
        val command = PayJailFeeCommand()
        command.execute()
        Some(RollingState())
      } else {
        Some(JailState())
      }
    } else {
      nextHandler.flatMap(_.handle(input))
    }
  }
}

class RollDoublesJailHandler extends ActionHandler {
  override def handle(input: OpEnum)(using controller: Controller): Option[GameState] = {
    input match {
      case OpEnum.roll =>
        val (d1, d2) = controller.game.rollDice(true)
        val isDouble = d1 == d2
        val player = controller.currentPlayer

        if (isDouble) {
          // Bei Pasch: Spieler aus dem Gefängnis entlassen
          val updatedPlayer = player.goToJail
          controller.updatePlayer(updatedPlayer)
          Some(MovingState(()=>(d1,d2)))
        } else {
          // Kein Pasch: Spieler bleibt im Gefängnis
          controller.updatePlayer(player)
          Some(JailState())
        }
      case _ => nextHandler.flatMap(_.handle(input))
    }
  }
}

case class InvalidJailInputHandler() extends ActionHandler {
  override def handle(input: OpEnum)(using controller: Controller): Option[GameState] = {
    Some(JailState())
  }
}