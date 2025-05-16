package de.htwg.controller

import de.htwg.controller.Controller
import de.htwg.controller.PayJailFeeCommand
import de.htwg.controller.JailTurnStrategy
import de.htwg.model.Player

// Abstrakte Basisklasse für die Handler in der Kette
abstract class JailActionHandler {
  val controller: Controller
  var nextHandler: Option[JailActionHandler] // nextHandler ist jetzt var

  def handle(input: String): Option[GameState]

  def setNext(handler: JailActionHandler): JailActionHandler = {
    this match {
      case h: JailActionHandler =>
        h.nextHandler match {
          case Some(next) => next.setNext(handler)
          case None => h.nextHandler = Some(handler); handler
        }
    }
    this
  }
}

// Handler für die Option "Bezahlen, um freizukommen"
case class PayJailHandler(override val controller: Controller, var nextHandler: Option[JailActionHandler] = None) extends JailActionHandler {
  override def handle(input: String): Option[GameState] = {
    if (input == "1") {
      if (controller.currentPlayer.balance >= 50) {
        val command = PayJailFeeCommand(controller, controller.currentPlayer)
        command.execute()
        Some(RollingState())
      } else {
        Some(JailState()) // Bleibe im Gefängnis, wenn nicht bezahlt werden kann
      }
    } else {
      nextHandler.flatMap(_.handle(input))
    }
  }
}

// Handler für die Option "Versuchen, Pasch zu würfeln"
case class RollDoublesJailHandler(override val controller: Controller, var nextHandler: Option[JailActionHandler] = None) extends JailActionHandler {
  override def handle(input: String): Option[GameState] = {
    if (input == "3") {
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

// Default-Handler, wenn keine der spezifischen Optionen zutrifft
case class InvalidJailInputHandler(override val controller: Controller, var nextHandler: Option[JailActionHandler] = None) extends JailActionHandler {
  override def handle(input: String): Option[GameState] = {
    Some(JailState()) // Bleibe im JailState bei ungültiger Eingabe
  }
}