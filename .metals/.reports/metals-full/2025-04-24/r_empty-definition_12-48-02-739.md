error id: currentPlayer.
file://<WORKSPACE>/src/test/scala/de/htwg/model/MonopolySpec.scala
empty definition using pc, found symbol in pc: currentPlayer.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -resultGame/currentPlayer.
	 -scala/Predef.resultGame.currentPlayer.
offset: 3968
uri: file://<WORKSPACE>/src/test/scala/de/htwg/model/MonopolySpec.scala
text:
```scala
package de.htwg.model
import de.htwg.model.Monopoly

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}

class MonopolySpec extends AnyWordSpec with Matchers {
  "Monopoly main" should {
    "run without throwing an exception" in {
      noException should be thrownBy Monopoly.main(Array.empty)
    }
  }
  val player1 = Player("Player1", 1500, 1)
  val player2 = Player("Player2", 1500, 1)
  val brownProperty1 = PropertyField("Brown1", 2, 100, 10, None, Brown)
  val brownProperty1Owned = brownProperty1.copy(owner = Some(player1.name))
  val brownProperty1WithHouse = brownProperty1Owned.copy(house = PropertyField.House(1))
  val darkBlueProperty1 = PropertyField("DarkBlue1", 38, 200, 20, None, DarkBlue)
  val darkBlueProperty1Owned = darkBlueProperty1.copy(owner = Some(player1.name))
  val trainStation = TrainStationField("Station1", 6, None)
  val trainStationOwned = trainStation.copy(owner = Some(player1.name))
  val utility = UtilityField("Utility1", 13, None)
  val utilityOwned = utility.copy(owner = Some(player1.name))
  val goToJail = GoToJailField()
  val taxField = TaxField(100, 5)
  val freeParkingField = FreeParkingField(50)
  val board = Board(Vector(GoField, brownProperty1, CommunityChestField(3), brownProperty1, taxField, trainStation,
    PropertyField("LightBlue1", 7, 120, 12, None, PropertyField.Color.LightBlue), ChanceField(8),
    PropertyField("LightBlue2", 9, 120, 12, None, PropertyField.Color.LightBlue),
    PropertyField("LightBlue3", 10, 120, 12, None, PropertyField.Color.LightBlue), JailField,
    PropertyField("Pink1", 12, 140, 14, None, PropertyField.Color.Pink), utility,
    PropertyField("Pink2", 14, 140, 14, None, PropertyField.Color.Pink),
    PropertyField("Pink3", 15, 140, 14, None, PropertyField.Color.Pink),
    TrainStationField("Station2", 16, None),
    PropertyField("Orange1", 17, 160, 16, None, PropertyField.Color.Orange), CommunityChestField(18),
    PropertyField("Orange2", 19, 160, 16, None, PropertyField.Color.Orange),
    PropertyField("Orange3", 20, 160, 16, None, PropertyField.Color.Orange), freeParkingField,
    PropertyField("Red1", 22, 180, 18, None, PropertyField.Color.Red), ChanceField(23),
    PropertyField("Red2", 24, 180, 18, None, PropertyField.Color.Red),
    PropertyField("Red3", 25, 180, 18, None, PropertyField.Color.Red),
    TrainStationField("Station3", 26, None),
    PropertyField("Yellow1", 27, 200, 20, None, PropertyField.Color.Yellow),
    PropertyField("Yellow2", 28, 200, 20, None, PropertyField.Color.Yellow), UtilityField("Utility2", 29, None),
    PropertyField("Yellow3", 30, 200, 20, None, PropertyField.Color.Yellow), goToJail,
    PropertyField("Green1", 32, 220, 22, None, PropertyField.Color.Green),
    PropertyField("Green2", 33, 220, 22, None, PropertyField.Color.Green), ChanceField(34),
    PropertyField("Green3", 35, 220, 22, None, PropertyField.Color.Green),
    TrainStationField("Station4", 36, None), ChanceField(37), darkBlueProperty1, taxField,
    darkBlueProperty1
  ))
  val game = MonopolyGame(Vector(player1, player2), board, player1, false)
   "handlePlayerTurn" should {
    "call handleJailTurn if player is in jail" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val resultGame = Monopoly.handlePlayerTurn(updatedGame)
      // Assert that handleJailTurn was called (we can't directly test the call,
      // but we can check a side effect or the return type if it's distinct)
      resultGame.currentPlayer.isInJail should be(true)
    }

    "call handleRegularTurn if player is not in jail" in {
      val resultGame = Monopoly.handlePlayerTurn(game)
      // Assert that handleRegularTurn was called (similarly, check for a side effect)
      resultGame.currentP@@layer.position should not be(player1.position)
    }
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: currentPlayer.