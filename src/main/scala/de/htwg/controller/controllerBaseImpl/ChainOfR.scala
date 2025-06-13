package de.htwg.controller

import de.htwg.controller.Controller
import de.htwg.controller.PayJailFeeCommand
import de.htwg.controller.JailTurnStrategy
import de.htwg.controller.OpEnum.roll
import de.htwg.model.modelBaseImple.{GameState, JailState, MovingState, Player, RollingState}

abstract class ActionHandler {
  val controller: Controller
  var nextHandler: Option[ActionHandler] // nextHandler ist jetzt var

  def handle(input: OpEnum): Option[GameState]

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

case class PayJailHandler(override val controller: Controller, var nextHandler: Option[ActionHandler] = None) extends ActionHandler {
  override def handle(input: OpEnum): Option[GameState] = {
    if (input == OpEnum.pay) {
      if (controller.currentPlayer.balance >= 50) {
        val command = PayJailFeeCommand(controller, controller.currentPlayer)
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

case class RollDoublesJailHandler(override val controller: Controller, var nextHandler: Option[ActionHandler] = None) extends ActionHandler {
  override def handle(input: OpEnum): Option[GameState] = {
    if (input == OpEnum.roll) {
      val strategy = JailTurnStrategy()
      val updatedPlayer = strategy.executeTurn(controller.currentPlayer, () => controller.dice.rollDice(controller.sound))
      controller.updatePlayer(updatedPlayer)
      if (!updatedPlayer.isInJail) {
        Some(MovingState(() => controller.dice.rollDice(controller.sound)))
      } else {
        Some(JailState())
      }
    } else {
      nextHandler.flatMap(_.handle(input))
    }
  }
}

case class InvalidJailInputHandler(override val controller: Controller, var nextHandler: Option[ActionHandler] = None) extends ActionHandler {
  override def handle(input: OpEnum): Option[GameState] = {
    Some(JailState()) 
  }
}