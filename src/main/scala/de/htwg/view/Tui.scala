package de.htwg.view

import de.htwg.controller.{AdditionalActionsState, Controller, EndTurnState, JailState, MovingState, PropertyDecisionState, RollingState}
import de.htwg.util.util.Observer

import scala.io.StdIn.readLine

class Tui(controller: Controller) extends Observer {

  controller.add(this)

  def run(): Unit = {
    println(controller.getBoardString)

    while (!controller.isGameOver) {
      println(controller.getCurrentPlayerStatus)

      controller.state match {
        case _: JailState =>
          println("You're in jail! Options:")
          println("1. Pay €50 to get out")
          println("3. Try to roll doubles")
          controller.handleInput(readLine())

        case _: PropertyDecisionState =>
          println(s"You moved to position ${controller.currentPlayer.position} and are now on the field ${controller.board.fields(controller.currentPlayer.position).name}.")
          println(s"Would you like to buy ${controller.board.fields(controller.currentPlayer.position).name}? (y/n)")
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
          println("Your now moving. Press enter to continue...")
          controller.handleInput(readLine())

        case _: EndTurnState =>
          println("Turn ended. Switch to next player.")
          controller.handleInput(readLine())

        case _ =>
          println("Press enter to continue...")
          controller.handleInput(readLine())
      }
    }

    val winner = controller.game.players.find(_.balance > 0).getOrElse(controller.game.players.head)
    println(s"\n${winner.name} wins the game!")
  }

  override def update(): Unit = println(controller.getBoardString)
}
  /*
  def run(): Unit = {

    println(controller.getBoardString)

    while (!controller.isGameOver) {
      val current = controller.currentPlayer
      showPlayerStatus()

      controller.handlePlayerTurn(
        ask = msg => readLine(msg + " (j/N): ").trim.toLowerCase == "j",
        print = msg => println(msg),
        choice = () => {
            readLine("Deine Wahl (1-3): ").trim.toIntOption.getOrElse(-1) match {
              case 1 => 1
              case 2 => 2
              case 3 => 3
              case _ =>
                println("Weiter")
                -1 // Fallback oder erneut fragen (rekursiv, je nach Geschmack)
            }
        },
        idxInput = () => readLine("Auf welcher Strße Willst du bauen? (Nr)").trim.toIntOption.getOrElse(-1)
      )
    }

    val winner = controller.game.players.find(_.balance > 0).getOrElse(controller.game.players.head)
    println(s"\n${winner.name} wins the game!")
  }*/
  /*
  def handleRegularTurn(): Unit = {
    readLine("Press ENTER to roll a dice")
    controller.handleRegularTurn()
    printDiceResult()
    printNewPosition()
    handleOptionalActions()
  }

   */


/*
  def handleJailTurn(): Unit = {
    printJailOptions()
    readLine()
    controller.handleJailTurn()
    getJailChoice()
  }

  def showPlayerStatus(): Unit = {
    println(controller.getCurrentPlayerStatus+ " || " + controller.getInventoryString)
  }*/

  /*
  def printNewPosition(): Unit = {
    println(s"Moving to position ${controller.currentPlayer.position}")
  }


  def handleOptionalActions(): Unit = {
    println("Do you want to do anything else? |1. Buy House|2. Trade|3. Mortgage| => (1/2/3/x)")

    val input = readLine().trim match {
      case "1" => 1
      case "2" => 2
      case "3" => 3
      case "x" => -1
      case _ =>
        println("Invalid choice! Try again.")
        handleOptionalActions()
        return
    }

    if (input == -1) {
      println("No more actions.")
      return
    }

    val fieldIndex = if (input == 1) {
      println("Enter the index of the property to buy a house: ")
       val testIndex = readLine().toInt
      while (testIndex < 1  || testIndex > 40) {
        println("Kein passender index!!")
        println("Enter the index of the property to buy a house: ")
        val testIndex = readLine().toInt
      }
      testIndex
    } else {
      -1
    }

    controller.handleOptionalActions(, fieldIndex)

    if (input != -1) handleOptionalActions()
  }




  def handleGoToJailField(): Unit = {
    controller.handleGoToJailField()
    print("you landet on \"go to jail\", Press any key to continue the game")
  }


  def printJailOptions(): Unit = {
    println(s"${controller.currentPlayer.name}, you are in jail!")
    println("Options to get out of jail:")
    println("1. Pay €50 to get out")
    println("2. Use a 'Get Out of Jail Free' card (if available) - Not Implemented")
    println("3. Try to roll doubles")
  }*/
/*
  def getJailChoice(): Unit = {
    val choice = readLine("Enter your choice (1-3): ").trim
    // Die Logik für die Gefängniswahl sollte im Controller sein
    // Wir rufen hier nur die entsprechende Controller-Methode auf
    choice match {
      case "1" => controller.handleJailTurn() // Controller kümmert sich um die Bezahlung
      case "2" => println("Not Implemented")
      case "3" => controller.handleJailTurn() // Controller würfelt und entscheidet
      case _ =>  controller.handleJailTurn() // Controller würfelt und entscheidet
    }
  }




  override def update(): Unit = println(controller.getBoardString)
}
*/