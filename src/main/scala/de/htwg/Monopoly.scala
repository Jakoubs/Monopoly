package de.htwg

import de.htwg.controller.controllerBaseImpl.Controller
import de.htwg.model.modelBaseImple.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.modelBaseImple.PropertyField.calculateRent
import de.htwg.model.modelBaseImple.MonopolyGame

import scala.io.StdIn.readLine
import scala.util.Random
import de.htwg.view.Tui
import de.htwg.view.GUI
import de.htwg.model.*
import de.htwg.model.FileIOComponent.{FileIOModule, IFileIO}
import de.htwg.model.modelBaseImple.{BoardField, ChanceField, CommunityChestField, Dice, FreeParkingField, GoField, GoToJailField, JailField, Player, PropertyField, SoundPlayer, TaxField, TrainStationField, UtilityField}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future // Import Future for asynchronous execution

case class Board(fields: Vector[BoardField])

object Monopoly:
  // Make the controller a global variable or pass it to GUI.main
  // Option 1: Global var (less ideal for large apps, but simple for now)
  var gameController: Option[Controller] = None // Option to hold the controller

  def main(args: Array[String]): Unit = {
    // 1) Format aus args (Default "xml")
    val format = args.headOption.getOrElse("json")

    given IFileIO = FileIOModule.select(format)

    val game = defineGame()
    val controller = Controller(game)
    gameController = Some(controller) // Store the controller

    // Launch the TUI in a separate Future (on a separate thread)
    Future {
      val tui = Tui(controller)
      tui.run() // This will block this Future's thread, not the main thread.
    }

    // Launch the GUI. The GUI's start() method will then retrieve the controller.
    GUI.main(args)
  }

  def defineGame(): IMonopolyGame = {
    //println("play with sound? (y/n)")
    val soundInput = "y"//readLine()
    val isTestBoard = soundInput == "yT" || soundInput == "nT"
    val soundBool = soundInput == "y" || soundInput == "yT"

    if (soundBool) {
      SoundPlayer().playBackground("src/main/resources/sound/MonopolyJazz.wav")
    }

    var playerVector = Vector[IPlayer]()

    def askForPlayerCount(): Int = {
      4
    }
    val playerAnz = askForPlayerCount()

    for (i <- 1 to playerAnz) {
      val playerName = randomEmoji(playerVector)
      playerVector = playerVector.appended(Player(playerName, 1500, 1,false,0))
      println(s"Spieler $playerName hinzugefügt.")
    }

    val board = Board(
      Vector(
        GoField,
        PropertyField("brown1", 2, 60, 2, Some(playerVector.head), color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(5)),
        CommunityChestField(3),
        PropertyField("brown2", 4, 60, 4, Some(playerVector.head), color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
        TaxField(100, 5),
        TrainStationField("Marklylebone Station", 6,200, None),
        PropertyField("lightBlue1", 7, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
        ChanceField(8),
        PropertyField("lightBlue2", 9, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
        PropertyField("lightBlue3", 10, 120, 8, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(60, false), PropertyField.House(0)),
        JailField,
        PropertyField("Pink1", 12, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
        UtilityField("Electric Company", 13,150,UtilityField.UtilityCheck.utility,None),
        PropertyField("Pink2", 14, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
        PropertyField("Pink3", 15, 160, 12, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(80, false), PropertyField.House(0)),
        TrainStationField("Fenchurch ST Station", 16,200, None),
        PropertyField("Orange1", 17, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
        CommunityChestField(18),
        PropertyField("Orange2", 19, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
        PropertyField("Orange3", 20, 200, 16, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(100, false), PropertyField.House(0)),
        FreeParkingField(0),
        PropertyField("Red1", 22, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
        ChanceField(23),
        PropertyField("Red2", 24, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
        PropertyField("Red3", 25, 240, 20, None, color = PropertyField.Color.Red, PropertyField.Mortgage(120, false), PropertyField.House(0)),
        TrainStationField("King's Cross Station", 26,200, None),
        PropertyField("Yellow1", 27, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
        UtilityField("Water Works", 28,150,UtilityField.UtilityCheck.utility, None),
        PropertyField("Yellow2", 29, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
        PropertyField("Yellow3", 30, 280, 24, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(140, false), PropertyField.House(0)),
        GoToJailField(),
        PropertyField("Green1", 32, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
        PropertyField("Green2", 33, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
        CommunityChestField(34),
        PropertyField("Green3", 35, 320, 28, None, color = PropertyField.Color.Green, PropertyField.Mortgage(160, false), PropertyField.House(0)),
        TrainStationField("Liverpool ST Station", 36,200, None),
        ChanceField(37),
        PropertyField("DarkBlue1", 38, 350, 35, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(175, false), PropertyField.House(0)),
        TaxField(200, 39),
        PropertyField("DarkBlue2", 40, 400, 50, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(200, false), PropertyField.House(0))
      )
    )
    MonopolyGame(playerVector, board, playerVector.head, soundBool)
  }

  def randomEmoji(vektor: Vector[IPlayer]): String = {
    val emojis = List(
      "🐶", "🐱", "🐯", "🦁", "🐻", "🐼", "🦊", "🐺", "🦄", "🐲", "🦉",
      "🦅", "🐝", "🦋", "🐙", "🦑", "🦈", "🐊", "🦖", "🦓", "🦒", "🐘",
      "🦔", "🐢", "🐸", "🦜", "👑", "🤖", "👽", "🧙", "🧛", "🧟", "👻",
      "🦸", "🧚", "🥷")
    val availableEmojis = emojis.filterNot(e => vektor.exists(_.name == e))
    Random.shuffle(availableEmojis).headOption.getOrElse("🐾")
  }

  def createEmptyBaseGame(): IMonopolyGame = {
    val board = Board(
      Vector(
        GoField,
        PropertyField("brown1", 2, 60, 2, None, color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(5)),
        CommunityChestField(3),
        PropertyField("brown2", 4, 60, 4, None, color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
        TaxField(100, 5),
        TrainStationField("Marklylebone Station", 6,200, None),
        PropertyField("lightBlue1", 7, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
        ChanceField(8),
        PropertyField("lightBlue2", 9, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
        PropertyField("lightBlue3", 10, 120, 8, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(60, false), PropertyField.House(0)),
        JailField,
        PropertyField("Pink1", 12, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
        UtilityField("Electric Company", 13,150,UtilityField.UtilityCheck.utility,None),
        PropertyField("Pink2", 14, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
        PropertyField("Pink3", 15, 160, 12, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(80, false), PropertyField.House(0)),
        TrainStationField("Fenchurch ST Station", 16,200, None),
        PropertyField("Orange1", 17, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
        CommunityChestField(18),
        PropertyField("Orange2", 19, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
        PropertyField("Orange3", 20, 200, 16, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(100, false), PropertyField.House(0)),
        FreeParkingField(0),
        PropertyField("Red1", 22, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
        ChanceField(23),
        PropertyField("Red2", 24, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
        PropertyField("Red3", 25, 240, 20, None, color = PropertyField.Color.Red, PropertyField.Mortgage(120, false), PropertyField.House(0)),
        TrainStationField("King's Cross Station", 26,200, None),
        PropertyField("Yellow1", 27, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
        UtilityField("Water Works", 28,150,UtilityField.UtilityCheck.utility, None),
        PropertyField("Yellow2", 29, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
        PropertyField("Yellow3", 30, 280, 24, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(140, false), PropertyField.House(0)),
        GoToJailField(),
        PropertyField("Green1", 32, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
        PropertyField("Green2", 33, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
        CommunityChestField(34),
        PropertyField("Green3", 35, 320, 28, None, color = PropertyField.Color.Green, PropertyField.Mortgage(160, false), PropertyField.House(0)),
        TrainStationField("Liverpool ST Station", 36,200, None),
        ChanceField(37),
        PropertyField("DarkBlue1", 38, 350, 35, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(175, false), PropertyField.House(0)),
        TaxField(200, 39),
        PropertyField("DarkBlue2", 40, 400, 50, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(200, false), PropertyField.House(0))
      )
    )
    val emptyPlayers = Vector.empty[IPlayer]
    MonopolyGame(emptyPlayers, board, Player("Placeholder", 0, 1, false, 0), sound = false)
  }
