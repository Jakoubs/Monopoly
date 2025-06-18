// src/main/scala/de/htwg/GameFactory.scala
package de.htwg

import de.htwg.model.{IMonopolyGame, IPlayer}
import de.htwg.model.modelBaseImple.{BoardField, ChanceField, CommunityChestField, Dice, FreeParkingField, GoField, GoToJailField, JailField, MonopolyGame, Player, PropertyField, SoundPlayer, TaxField, TrainStationField, UtilityField}
import de.htwg.model.modelBaseImple.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.modelBaseImple.PropertyField.calculateRent

import scala.io.StdIn.readLine
import scala.util.Random
import de.htwg.Board
import de.htwg.model.modelBaseImple.UtilityField.UtilityCheck.utility


object GameFactory:
  def defineGame(): IMonopolyGame = {
    val playerNames = List("🐶", "🐱") // Or however you get player names
    val playerVector: Vector[IPlayer] = playerNames.map(name => Player(name, 1500, 1, false, 0)).toVector

    val board = Board(
      Vector(
        GoField,
        PropertyField("Brown1", 2, 60, 2, None, color = Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
        CommunityChestField(3),
        PropertyField("Brown2", 4, 60, 4, None, color = Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
        TaxField(200, 5),
        TrainStationField("King's Cross Station", 6, 200, None),
        PropertyField("LightBlue1", 7, 100, 6, None, color = LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
        ChanceField(8),
        PropertyField("LightBlue2", 9, 100, 6, None, color = LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
        PropertyField("LightBlue3", 10, 120, 8, None, color = LightBlue, PropertyField.Mortgage(60, false), PropertyField.House(0)),
        JailField,
        PropertyField("Pink1", 12, 140, 10, None, color = Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
        UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility,None),
        PropertyField("Pink2", 14, 140, 10, None, color = Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
        PropertyField("Pink3", 15, 160, 12, None, color = Pink, PropertyField.Mortgage(80, false), PropertyField.House(0)),
        TrainStationField("Marylebone Station", 16, 200, None),
        PropertyField("Orange1", 17, 180, 14, None, color = Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
        CommunityChestField(18),
        PropertyField("Orange2", 19, 180, 14, None, color = Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
        PropertyField("Orange3", 20, 200, 16, None, color = Orange, PropertyField.Mortgage(100, false), PropertyField.House(0)),
        FreeParkingField(21),
        PropertyField("Red1", 22, 220, 18, None, color = Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
        ChanceField(23),
        PropertyField("Red2", 24, 220, 18, None, color = Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
        PropertyField("Red3", 25, 240, 20, None, color = Red, PropertyField.Mortgage(120, false), PropertyField.House(0)),
        TrainStationField("Fenchurch St Station", 26, 200, None),
        PropertyField("Yellow1", 27, 260, 22, None, color = Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
        UtilityField("Water Works", 28, 150, UtilityField.UtilityCheck.utility,None),
        PropertyField("Yellow2", 29, 260, 22, None, color = Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
        PropertyField("Yellow3", 30, 280, 24, None, color = Yellow, PropertyField.Mortgage(140, false), PropertyField.House(0)),
        GoToJailField(),
        PropertyField("Green1", 32, 300, 26, None, color = Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
        PropertyField("Green2", 33, 300, 26, None, color = Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
        CommunityChestField(34),
        PropertyField("Green3", 35, 320, 28, None, color = Green, PropertyField.Mortgage(160, false), PropertyField.House(0)),
        TrainStationField("Liverpool ST Station", 36,200, None),
        ChanceField(37),
        PropertyField("DarkBlue1", 38, 350, 35, None, color = DarkBlue, PropertyField.Mortgage(175, false), PropertyField.House(0)),
        TaxField(200, 39),
        PropertyField("DarkBlue2", 40, 400, 50, None, color = DarkBlue, PropertyField.Mortgage(200, false), PropertyField.House(0))
      )
    )
    val soundBool = false // Assuming this is hardcoded or set elsewhere.

    MonopolyGame(playerVector, board, playerVector.head, soundBool)
  }

