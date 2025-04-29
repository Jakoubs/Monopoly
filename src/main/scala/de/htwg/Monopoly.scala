package de.htwg

import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.PropertyField
import de.htwg.model.SoundPlayer
import de.htwg.model.PropertyField.calculateRent
import scala.io.StdIn.readLine
import scala.util.Random
import de.htwg.controller.Controller
import de.htwg.view.Tui
import de.htwg.model.*

case class Board(fields: Vector[BoardField])

object Monopoly:
  def main(args: Array[String]): Unit = {
    val game = defineGame()
    val dice = Dice()
    val controller = Controller(game, dice)
    val tui = Tui(controller)
    tui.run() // Die TUI steuert jetzt den Spielablauf
  }

  def defineGame(): MonopolyGame = {
    println("play with sound? (y/n)")
    val soundInput = readLine()
    val isTestBoard = soundInput == "yT" || soundInput == "nT"
    val soundBool = soundInput == "y" || soundInput == "yT"

    if (soundBool) {
      SoundPlayer().playBackground("src/main/resources/MonopolyJazz.wav")
    }

    var playerVector = Vector[Player]()

    def askForPlayerCount(): Int = {
      println("How many Player? (2-4):")
      val input = scala.io.StdIn.readLine()
      try {
        val playerCount = input.toInt
        if (playerCount >= 2 && playerCount <= 4) {
          playerCount
        } else {
          println("Invalid player count. Please enter a number between 2 and 4.")
          askForPlayerCount()
        }
      } catch {
        case _: NumberFormatException =>
          println("Invalid input. Please enter a number.")
          askForPlayerCount()
      }
    }

    val playerAnz = askForPlayerCount()

    for (i <- 1 to playerAnz) {
      val playerName = randomEmoji(playerVector)
      playerVector = playerVector.appended(Player(playerName, 1500, 1))
      println(s"Spieler $playerName hinzugefügt.")
    }

    val board = Board(
      Vector(
        GoField,
        PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        CommunityChestField(3),
        PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        TaxField(100, 5),
        TrainStationField("Marklylebone Station", 6, None),
        PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        ChanceField(8),
        PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        JailField,
        PropertyField("Pink1", 12, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        UtilityField("Electric Company", 13, None),
        PropertyField("Pink2", 14, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        PropertyField("Pink3", 15, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        TrainStationField("Fenchurch ST Station", 16, None),
        PropertyField("Orange1", 17, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        CommunityChestField(18),
        PropertyField("Orange2", 19, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        PropertyField("Orange3", 20, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        FreeParkingField(0),
        PropertyField("Red1", 22, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        ChanceField(23),
        PropertyField("Red2", 24, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        PropertyField("Red3", 25, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        TrainStationField("King's Cross Station", 26, None),
        PropertyField("Yellow1", 27, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        UtilityField("Water Works", 28, None),
        PropertyField("Yellow2", 29, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        PropertyField("Yellow3", 30, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        GoToJailField(),
        PropertyField("Green1", 32, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        PropertyField("Green2", 33, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        CommunityChestField(34),
        PropertyField("Green3", 35, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        TrainStationField("Liverpool ST Station", 36, None),
        ChanceField(37),
        PropertyField("DarkBlue1", 38, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
        TaxField(200, 39),
        PropertyField("DarkBlue2", 40, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0))
      )
    )
    MonopolyGame(playerVector, board, playerVector.head, soundBool)
  }

  def randomEmoji(vektor: Vector[Player]): String = {
    val emojis = List(
      "🐶", "🐱", "🐯", "🦁", "🐻", "🐼", "🦊", "🐺", "🦄", "🐲", "🦉",
      "🦅", "🐝", "🦋", "🐙", "🦑", "🦈", "🐊", "🦖", "🦓", "🦒", "🐘",
      "🦔", "🐢", "🐸", "🦜", "👑", "🤖", "👽", "🧙", "🧛", "🧟", "👻",
      "🦸", "🧚", "🥷")
    val availableEmojis = emojis.filterNot(e => vektor.exists(_.name == e))
    Random.shuffle(availableEmojis).headOption.getOrElse("🐾")
  }

case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player,
                         sound: Boolean
                       )