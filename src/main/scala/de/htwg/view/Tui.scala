package de.htwg.view

import de.htwg.Monopoly
import de.htwg.controller.Controller
import de.htwg.util.util.Observer

import scala.io.StdIn.readLine

class Tui(controller: Controller) extends Observer {

  controller.add(this)

  def run(): Unit = {
    BoardPrinter.printBoard(controller.game)

    while (!controller.isGameOver) {
      val current = controller.currentPlayer
      showPlayerStatus()

      controller.handlePlayerTurn(
        ask = msg => readLine(msg + " (j/n): ").trim.toLowerCase == "j",
        print = msg => println(msg),
        choice = () => {
            readLine("Deine Wahl (1-3): ").trim match {
              case "1" => 1
              case "2" => 2
              case "3" => 3
              case _ =>
                println("Ungültige Eingabe.")
                1 // Fallback oder erneut fragen (rekursiv, je nach Geschmack)
            }
        }
      )
    }

    val winner = controller.game.players.find(_.balance > 0).getOrElse(controller.game.players.head)
    println(s"\n${winner.name} wins the game!")
  }
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
 */
  def showPlayerStatus(): Unit = {
    println(controller.getCurrentPlayerStatus+ " || " + BoardPrinter.getInventory(controller.getGameStatus))
  }

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

    controller.handleOptionalActions(input, fieldIndex)

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
  }
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

 */


  override def update(): Unit = BoardPrinter.printBoard(controller.game)
}
