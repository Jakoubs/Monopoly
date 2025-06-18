package de.htwg.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.*
import de.htwg.Board
import de.htwg.model.modelBaseImple.{ChanceField, CommunityChestField, FreeParkingField, GoField, GoToJailField, JailField, Player, PropertyField, RentVisitor, TaxField, TrainStationField, UtilityField}

class RentVisitorSpec extends AnyWordSpec with Matchers {

  "A RentVisitor" when {
    val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
    val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)
    val board = Board(Vector.empty) // You might need to create a more realistic board for some tests

    "visiting a PropertyField" should {
      "return 0 if the field is not owned" in {
        val field = PropertyField("Test Property", 1, 200, 20, None, PropertyField.Color.Brown)
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field) should be(0)
      }

      "return 0 if the field is owned by the current player" in {
        val field = PropertyField("Test Property", 1, 200, 20, Some(player1), PropertyField.Color.Brown)
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field) should be(0)
      }

      "return the rent if the field is owned by another player" in {
        val field1 = PropertyField("Test Property 1", 1, 200, 20, Some(player2), PropertyField.Color.Brown)
        val field2 = PropertyField("Test Property 2", 2, 200, 20, None, PropertyField.Color.Brown)
        val boardWithOutColorGroup = Board(Vector(field1, field2))
        val visitor = new RentVisitor(player1, Vector(player1, player2), boardWithOutColorGroup, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field1) should be(20)
      }

      "return double the rent if the owner has a monopoly and no houses" in {
        val field1 = PropertyField("Test Property 1", 1, 200, 20, Some(player2), PropertyField.Color.Brown)
        val field2 = PropertyField("Test Property 2", 2, 200, 20, Some(player2), PropertyField.Color.Brown)
        val boardWithColorGroup = Board(Vector(field1, field2))
        val visitor = new RentVisitor(player1, Vector(player1, player2), boardWithColorGroup, 5, Map(player2 -> List(field1, field2)), Map.empty, Map.empty)
        visitor.visit(field1) should be(40)
      }

      "return the rent plus house rent if the field has houses" in {
        val field = PropertyField("Test Property", 1, 200, 20, Some(player2), PropertyField.Color.Brown, house = PropertyField.House(2))
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field) should be(80) // 20 (base) + 2 * (20 / 2)
      }

      "return double the rent plus house rent if the owner has a monopoly and houses" in {
        val field1 = PropertyField("Test Property 1", 1, 200, 20, Some(player2), PropertyField.Color.Brown, house = PropertyField.House(2))
        val field2 = PropertyField("Test Property 2", 2, 200, 20, Some(player2), PropertyField.Color.Brown)
        val boardWithColorGroup = Board(Vector(field1, field2))
        val visitor = new RentVisitor(player1, Vector(player1, player2), boardWithColorGroup, 5, Map(player2 -> List(field1, field2)), Map.empty, Map.empty)
        visitor.visit(field1) should be(80) // (20 + 2 * (20 / 2)) * 2
      }
    }

    "visiting a TrainStationField" should {
      "return 0 if the field is not owned" in {
        val field = TrainStationField("Test Station", 1, 200, None)
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field) should be(0)
      }

      "return the correct rent based on the number of owned stations" in {
        val field = TrainStationField("Test Station", 1, 200, Some(player2))
        val visitor1 = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map(player2 -> 1), Map.empty)
        val visitor2 = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map(player2 -> 2), Map.empty)
        val visitor3 = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map(player2 -> 3), Map.empty)
        val visitor4 = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map(player2 -> 4), Map.empty)

        visitor1.visit(field) should be(25)
        visitor2.visit(field) should be(50)
        visitor3.visit(field) should be(100)
        visitor4.visit(field) should be(200)
      }
    }

    "visiting a UtilityField" should {
      "return 0 if the field is not owned" in {
        val field = UtilityField("Test Utility", 1, 150, UtilityField.UtilityCheck.utility, None)
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field) should be(0)
      }

      "return the correct rent based on the dice result and the number of owned utilities" in {
        val field = UtilityField("Test Utility", 1, 150, UtilityField.UtilityCheck.utility, Some(player2))
        val visitor1 = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map(player2 -> 1))
        val visitor2 = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map(player2 -> 2))

        visitor1.visit(field) should be(5 * 4)
        visitor2.visit(field) should be(5 * 10)
      }
    }

    "visiting other BoardFields" should {
      "return 0 for GoField, JailField, GoToJailField, FreeParkingField, ChanceField, CommunityChestField" in {
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(GoField) should be(0)
        visitor.visit(JailField) should be(0)
        visitor.visit(GoToJailField()) should be(0)
        visitor.visit(FreeParkingField(100)) should be(0)
        visitor.visit(ChanceField(10)) should be(0)
        visitor.visit(CommunityChestField(10)) should be(0)
      }

      "return the amount for TaxField" in {
        val field = TaxField(100, 1)
        val visitor = new RentVisitor(player1, Vector(player1, player2), board, 5, Map.empty, Map.empty, Map.empty)
        visitor.visit(field) should be(100)
      }
    }
  }
}