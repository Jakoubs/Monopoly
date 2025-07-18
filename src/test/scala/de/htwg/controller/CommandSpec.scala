package de.htwg.controller

import de.htwg.controller.controllerBaseImpl.{BuyCommand, BuyHouseCommand, Controller, PayJailFeeCommand, RollDiceCommand}
import de.htwg.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.*
import de.htwg.model.modelBaseImple.PropertyField.Color.*
import de.htwg.model.modelBaseImple.{BoardField, BuyableField, ChanceField, CommunityChestField, Dice, GoField, JailField, MonopolyGame, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.model.FileIOComponent.JSONFileIO.FileIO as JSONFileIO

class CommandSpec extends AnyWordSpec {

  "A Command" should {
    // Testumgebung einrichten
    val dice = new Dice()
    val mockAsk: String => Boolean = _ => true // Simuliert immer "ja" als Antwort
    val mockPrint: String => Unit = _ => () // Tut nichts beim Drucken
    val mockChoice: () => Int = () => 1 // Gibt immer 1 zurück
    val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
    val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)
    val fields = Vector(
      GoField,
      PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      CommunityChestField(3),
      PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TaxField(100, 5),
      TrainStationField("Marklylebone Station", 6, 200, None),
      PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      ChanceField(8),
      PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      JailField,
      PropertyField("Pink1", 12, 100, 10, Some(player1), color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility, None),
      PropertyField("Pink2", 14, 100, 10, Some(player1), color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Pink3", 15, 100, 10, Some(player1), color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),

    )
    val fileIO = new JSONFileIO
    val board = Board(fields)
    val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
    val controller = new Controller(initialGame)(using fileIO)

    // Hilfsfunktionen, um auf Spieler und Felder zuzugreifen
    def findPlayer(name: String): IPlayer = controller.game.players.find(_.name == name).get
    def getFieldAt(position: Int): BoardField = controller.game.board.fields(position - 1)

    "when BuyCommand is executed for a PropertyField" should {
      val propertyField = fields(1).asInstanceOf[BuyableField]
      val buyCommand = BuyCommand(propertyField, player1)(using controller)

      "buy the property and update player's money" in {
        val initialMoney = player1.balance
        buyCommand.execute()

        val updatedField = getFieldAt(propertyField.index).asInstanceOf[BuyableField]
        updatedField.owner shouldBe Some(player1)

        findPlayer(player1.name).balance shouldBe (initialMoney - propertyField.price)
      }

      "restore previous state when undone" in {
        buyCommand.undo()

        val restoredField = getFieldAt(propertyField.index).asInstanceOf[BuyableField]
        restoredField.owner shouldBe None

        findPlayer(player1.name).balance shouldBe 1500
      }
    }

    "when BuyCommand is executed for a TrainStationField" should {
      val trainStationField = fields(5).asInstanceOf[BuyableField]
      val buyCommand = BuyCommand(trainStationField, player1)(using controller)

      "buy the train station and update player's money" in {
        val initialMoney = player1.balance
        buyCommand.execute()

        val updatedField = getFieldAt(trainStationField.index).asInstanceOf[BuyableField]
        updatedField.owner shouldBe Some(player1)

        findPlayer(player1.name).balance shouldBe (initialMoney - trainStationField.price)
      }

      "restore previous state when undone" in {
        buyCommand.undo()

        val restoredField = getFieldAt(trainStationField.index).asInstanceOf[BuyableField]
        restoredField.owner shouldBe None

        findPlayer(player1.name).balance shouldBe 1500
      }
    }

    "when BuyCommand is executed for a UtilityField" should {
      val utilityField = fields(12).asInstanceOf[BuyableField]
      val buyCommand = BuyCommand( utilityField, player1)(using controller)

      "buy the utility and update player's money" in {
        val initialMoney = player1.balance
        buyCommand.execute()

        val updatedField = getFieldAt(utilityField.index).asInstanceOf[BuyableField]
        updatedField.owner shouldBe Some(player1)

        findPlayer(player1.name).balance shouldBe (initialMoney - utilityField.price)
      }

      "restore previous state when undone" in {
        buyCommand.undo()

        val restoredField = getFieldAt(utilityField.index).asInstanceOf[BuyableField]
        restoredField.owner shouldBe None

        findPlayer(player1.name).balance shouldBe 1500
      }
    }
    
    
    "when BuyHouseCommand is executed" should {
      val propertyField = fields(11).asInstanceOf[PropertyField]
      val buyHouseCommand = BuyHouseCommand()(using controller, propertyField, player1)
      
      "buy a house for the property and update player's money" in {
        val initialMoney = findPlayer(player1.name).balance
        buyHouseCommand.execute()

        val updatedField = getFieldAt(propertyField.index).asInstanceOf[PropertyField]
        updatedField.house.amount should be > 0

        findPlayer(player1.name).balance should be < initialMoney
      }

      "restore previous state when undone" in {
        val moneyBeforeUndo = findPlayer(player1.name).balance
        buyHouseCommand.undo()

        val restoredField = getFieldAt(propertyField.index).asInstanceOf[PropertyField]
        restoredField.house.amount shouldBe 0

        findPlayer(player1.name).balance shouldBe (moneyBeforeUndo + propertyField.house.calculateHousePrice(propertyField.price))
      }
    }

    "when RollDiceCommand is executed" should {
      val rollDiceCommand = RollDiceCommand()(using controller)

      "roll the dice and return a result" in {
        rollDiceCommand.execute()
        val (dice1, dice2) = rollDiceCommand.getResult

        // Überprüfe, dass beide Würfel Werte zwischen 1 und 6 haben
        dice1 should be >= 1
        dice1 should be <= 6
        dice2 should be >= 1
        dice2 should be <= 6
      }

      "restore previous player state when undone" in {
        val playerBeforeRoll = controller.currentPlayer
        rollDiceCommand.execute()
        rollDiceCommand.undo()

        // Überprüfe, dass der Spielerzustand wiederhergestellt wurde
        controller.currentPlayer shouldBe playerBeforeRoll
      }
    }

    "when PayJailFeeCommand is executed" should {
      // Spieler ins Gefängnis setzen
      val jailPlayer = player1.copy(isInJail = true)
      controller.updatePlayer(jailPlayer)

      val payJailFeeCommand = PayJailFeeCommand()(using controller, jailPlayer)

      "pay the fee and release player from jail" in {
        val initialMoney = findPlayer(jailPlayer.name).balance
        payJailFeeCommand.execute()

        findPlayer(jailPlayer.name).isInJail shouldBe false

        findPlayer(jailPlayer.name).balance should be < initialMoney
      }

      "restore previous state when undone" in {
        payJailFeeCommand.undo()

        findPlayer(jailPlayer.name).isInJail shouldBe true

        findPlayer(jailPlayer.name).balance shouldBe 1500
      }
    }

    
  }
}