package de.htwg.view

import de.htwg.controller.{AdditionalActionsState, BuyHouseState, BuyPropertyState, Controller, EndTurnState, JailState, MovingState, PropertyDecisionState, RollingState, TurnInfo}
import de.htwg.util.util.Observer

import scala.io.StdIn.readLine

class Tui(controller: Controller) extends Observer {

  controller.add(this)

  def run(): Unit = {
    println(controller.getBoardString)

    while (!controller.isGameOver) {
      controller.state match {
        case _: JailState =>
          println("You're in jail! Options:")
          println("1. Pay €50 to get out")
          println("3. Try to roll doubles")
          controller.handleInput(readLine())

        case _: PropertyDecisionState =>
          println(s"You moved to position ${controller.currentPlayer.position} and are now on the field ${controller.board.fields(controller.currentPlayer.position-1).name}.")
          println(s"Would you like to buy ${controller.board.fields(controller.currentPlayer.position-1).name}? (y/n)")
          controller.handleInput(readLine())

        case _: AdditionalActionsState =>
          println("Additional actions:")
          println("1. Buy house")
          println("2. End turn")
          controller.handleInput(readLine())
          
        case _: RollingState =>
          println("Press enter to roll a dice")
          controller.handleInput(readLine())

        case _: MovingState =>
          displayTurnInfo()
          println("Your now moving. Press enter to continue...")
          controller.handleInput(readLine())

        case _: BuyPropertyState =>
          println("You bought a property. Press enter to continue...")
          controller.handleInput(readLine())

        case _: EndTurnState =>
          println("Turn ended. Switch to next player.")
          controller.handleInput(readLine())

        case _: BuyHouseState =>
          println("Which House do you want to buy? (1-40)")
          controller.handleInput(readLine())

        case _ =>
          println("Press enter to continue...")
          controller.handleInput(readLine())
      }
    }

    val winner = controller.game.players.find(_.balance > 0).getOrElse(controller.game.players.head)
    println(s"\n${winner.name} wins the game!")
  }

  def displayTurnInfo(): Unit = {
    val turnInfo = controller.getTurnInfo // Verwende die getTurnInfo-Methode des Controllers

    turnInfo.diceRoll1 match {
      case 0 => // Keine Würfel geworfen
      case _ => println(s"Würfelergebnis: ${turnInfo.diceRoll1} und ${turnInfo.diceRoll2} (Summe: ${turnInfo.diceRoll1 + turnInfo.diceRoll2})")
    }

    turnInfo.landedField.foreach(field =>
      println(s"Gelandet auf: ${field.name}")
    )

    turnInfo.boughtProperty.foreach(property =>
      println(s"Gekaufte Immobilie: ${property.name}")
    )

    turnInfo.builtHouse.foreach(property =>
      println(s"Haus gebaut auf: ${property.name}")
    )

    (turnInfo.paidRent, turnInfo.rentPaidTo) match {
      case (Some(rent), Some(owner)) =>
        println(s"Miete bezahlt: ${rent}€ an ${owner.name}")
      case _ => // Keine Miete bezahlt
    }
  }


  override def update(): Unit = {
    println(controller.getBoardString)
    println(controller.getCurrentPlayerStatus)
  }
}