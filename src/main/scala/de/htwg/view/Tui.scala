package de.htwg.view

import de.htwg.util.util.Observer
import de.htwg.controller.controllerBaseImpl.{AdditionalActionsState, BuyHouseState, BuyPropertyState, ConfirmBuyHouseState, Controller, EndTurnState, JailState, MovingState, OpEnum, PropertyDecisionState, RollingState, TurnInfo}
import com.google.inject.{Guice, Inject}
import scala.io.StdIn.readLine
import de.htwg.controller.IController

class Tui @Inject() (controller: Controller) extends Observer {

  controller.add(this)

  def run(): Unit = {
    println(controller.getBoardString)

    while (!controller.isGameOver) {
      controller.state match {
        case _: JailState =>
          println("You're in jail! Options:")
          println("1. Pay €50 to get out")
          println("3. Try to roll doubles")
          val input = readLine()
          input match {
            case "1" => controller.handleInput(OpEnum.pay)
            case "3" => controller.handleInput(OpEnum.roll)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.pay)
          }

        case _: PropertyDecisionState =>
          println(s"You moved to position ${controller.currentPlayer.position} and are now on the field ${controller.board.fields(controller.currentPlayer.position-1).name}.")
          println(s"Would you like to buy ${controller.board.fields(controller.currentPlayer.position-1).name}? (y/n)")
          val input = readLine()
          input match {
            case "y" => controller.handleInput(OpEnum.y)
            case "n" => controller.handleInput(OpEnum.n)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.n)
          }
        case _: AdditionalActionsState =>
          println("Additional actions:")
          println("1. Buy house")
          println("2. End turn")
          val input = readLine()
          input match {
            case "1" => controller.handleInput(OpEnum.buy)
            case "2" => controller.handleInput(OpEnum.end)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.end)
          }
        case _: RollingState =>
          println("Press enter to roll a dice")
          val input = readLine()
          input match {
            case "" => controller.handleInput(OpEnum.enter)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.enter)
          }
        case _: MovingState =>
          displayTurnInfo()
          println("Your now moving. Press enter to continue...")
          val input = readLine()
          input match {
            case "" => controller.handleInput(OpEnum.enter)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.enter)
          }
        case _: BuyPropertyState =>
          println("You bought a property. Press enter to continue...")
          val input = readLine()
          input match {
            case "" => controller.handleInput(OpEnum.enter)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.enter)
          }
        case _: EndTurnState =>
          println("Turn ended. Switch to next player.")
          val input = readLine()
          input match {
            case "" => controller.handleInput(OpEnum.enter)
            case _ => controller.handleInput(OpEnum.enter)
          }
        case _: BuyHouseState =>
          println("Which House do you want to buy? (1-40)")
          val input = readLine()
          input match {
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ =>
              val houseNumber: Int = input.toIntOption match {
                case Some(num) if num >= 1 && num <= 40 => num
                case _ =>
                  println("Invalid input. Defaulting to house 1.")
                  1
              }
              controller.handleInput(OpEnum.fieldSelected(houseNumber))
          }
        case _: ConfirmBuyHouseState =>
          println("Do you want to Undo the House purchase? (y/n)")
          val input = readLine()
          input match {
            case "y" => controller.handleInput(OpEnum.y)
            case "n" => controller.handleInput(OpEnum.n)
            case "u" => controller.handleInput(OpEnum.undo)
            case "r" => controller.handleInput(OpEnum.redo)
            case _ => controller.handleInput(OpEnum.n)
          }

        case _ =>
          println("Press enter to continue...")
          val input = readLine()
          input match {
            case "" => controller.handleInput(OpEnum.enter)
            case _ => controller.handleInput(OpEnum.enter)
          }      }
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
    println(controller.getInventory)
  }
}