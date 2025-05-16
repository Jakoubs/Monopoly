package de.htwg.controller

import de.htwg.{Board, MonopolyGame}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.{JailField, *}
import de.htwg.model.PropertyField.Color.*

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
    val board = Board(fields)
    val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
    val controller = new Controller(initialGame, dice)

    // Hilfsfunktionen, um auf Spieler und Felder zuzugreifen
    def findPlayer(name: String): Player = controller.game.players.find(_.name == name).get
    def getFieldAt(position: Int): BoardField = controller.game.board.fields(position - 1)

    "when BuyPropertyCommand is executed" should {
      val propertyField = fields(1).asInstanceOf[PropertyField]
      val buyPropertyCommand = BuyPropertyCommand(controller, propertyField, player1)

      "buy the property and update player's money" in {
        val initialMoney = player1.balance
        buyPropertyCommand.execute()

        // Überprüfe, dass das Feld dem Spieler gehört
        val updatedField = getFieldAt(propertyField.index).asInstanceOf[PropertyField]
        updatedField.owner shouldBe Some(player1)

        // Überprüfe, dass das Geld abgezogen wurde
        findPlayer(player1.name).balance shouldBe (initialMoney - propertyField.price)
      }

      "restore previous state when undone" in {
        buyPropertyCommand.undo()

        // Überprüfe, dass das Feld keinen Besitzer hat
        val restoredField = getFieldAt(propertyField.index).asInstanceOf[PropertyField]
        restoredField.owner shouldBe None

        // Überprüfe, dass das Geld wiederhergestellt wurde
        findPlayer(player1.name).balance shouldBe 1500
      }
    }

    "when BuyTrainStationCommand is executed" should {
      val trainStationField = fields(5).asInstanceOf[TrainStationField]
      val buyTrainStationCommand = BuyTrainStationCommand(controller, trainStationField, player1)

      "buy the train station and update player's money" in {
        val initialMoney = player1.balance
        buyTrainStationCommand.execute()

        // Überprüfe, dass die Bahnstation dem Spieler gehört
        val updatedField = getFieldAt(trainStationField.index).asInstanceOf[TrainStationField]
        updatedField.owner shouldBe Some(player1)

        // Überprüfe, dass das Geld abgezogen wurde
        findPlayer(player1.name).balance shouldBe (initialMoney - trainStationField.price)
      }

      "restore previous state when undone" in {
        buyTrainStationCommand.undo()

        // Überprüfe, dass die Bahnstation keinen Besitzer hat
        val restoredField = getFieldAt(trainStationField.index).asInstanceOf[TrainStationField]
        restoredField.owner shouldBe None

        // Überprüfe, dass das Geld wiederhergestellt wurde
        findPlayer(player1.name).balance shouldBe 1500
      }
    }

    "when BuyHouseCommand is executed" should {
      val propertyField = fields(11).asInstanceOf[PropertyField]
      val buyHouseCommand = BuyHouseCommand(controller, propertyField, player1)

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
      val rollDiceCommand = RollDiceCommand(controller)

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

      val payJailFeeCommand = PayJailFeeCommand(controller, jailPlayer)

      "pay the fee and release player from jail" in {
        val initialMoney = findPlayer(jailPlayer.name).balance
        payJailFeeCommand.execute()

        // Überprüfe, dass der Spieler nicht mehr im Gefängnis ist
        findPlayer(jailPlayer.name).isInJail shouldBe false

        // Überprüfe, dass das Geld für die Kaution abgezogen wurde
        findPlayer(jailPlayer.name).balance should be < initialMoney
      }

      "restore previous state when undone" in {
        payJailFeeCommand.undo()

        // Überprüfe, dass der Spieler wieder im Gefängnis ist
        findPlayer(jailPlayer.name).isInJail shouldBe true

        // Überprüfe, dass das Geld wiederhergestellt wurde
        findPlayer(jailPlayer.name).balance shouldBe 1500
      }
    }

    "when BuyUtilityCommand is executed" should {
      // Annahme: Ein Versorgungswerk ist an Position 12 im Testboard
      val utilityField = UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility, None)
      val buyUtilityCommand = BuyUtilityCommand(controller, utilityField, player1)

      "buy the utility and update player's money" in {
        val initialMoney = player1.balance
        buyUtilityCommand.execute()

        // Da das Versorgunswerk nicht Teil des ursprünglichen Spielfelds ist,
        // müssen wir den aktuellen Zustand des Feldes aus dem Command selbst überprüfen
        val fieldAfterBuy = controller.game.board.fields.find(f =>
          f.isInstanceOf[UtilityField] && f.asInstanceOf[UtilityField].name == utilityField.name
        ).getOrElse(utilityField)

        fieldAfterBuy.asInstanceOf[UtilityField].owner shouldBe Some(player1)

        // Überprüfe, dass das Geld abgezogen wurde
        findPlayer(player1.name).balance shouldBe (initialMoney - utilityField.price)
      }

      "restore previous state when undone" in {
        buyUtilityCommand.undo()

        // Da das Versorgunswerk nicht Teil des ursprünglichen Spielfelds ist,
        // müssen wir den aktuellen Zustand des Feldes aus dem Command selbst überprüfen
        val fieldAfterUndo = controller.game.board.fields.find(f =>
          f.isInstanceOf[UtilityField] && f.asInstanceOf[UtilityField].name == utilityField.name
        ).getOrElse(utilityField)

        fieldAfterUndo.asInstanceOf[UtilityField].owner shouldBe None

        // Überprüfe, dass das Geld wiederhergestellt wurde
        findPlayer(player1.name).balance shouldBe 1500
      }
    }
  }
}