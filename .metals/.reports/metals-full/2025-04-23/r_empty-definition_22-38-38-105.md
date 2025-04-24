error id: handlePlayerTurn.
file://<WORKSPACE>/src/test/scala/de/htwg/model/MonopolySpec.scala
empty definition using pc, found symbol in pc: handlePlayerTurn.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -de/htwg/model/Monopoly.handlePlayerTurn.
	 -de/htwg/model/Monopoly.handlePlayerTurn#
	 -de/htwg/model/Monopoly.handlePlayerTurn().
	 -Monopoly.handlePlayerTurn.
	 -Monopoly.handlePlayerTurn#
	 -Monopoly.handlePlayerTurn().
	 -scala/Predef.Monopoly.handlePlayerTurn.
	 -scala/Predef.Monopoly.handlePlayerTurn#
	 -scala/Predef.Monopoly.handlePlayerTurn().
offset: 622
uri: file://<WORKSPACE>/src/test/scala/de/htwg/model/MonopolySpec.scala
text:
```scala
package de.htwg.model
import de.htwg.model.Monopoly

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MonopolySpec extends AnyWordSpec with Matchers {
  "Monopoly main" should {
    "run without throwing an exception" in {
      noException should be thrownBy Monopoly.main(Array.empty)
    }
  }
   "handlePlayerTurn" should {
    "call handleJailTurn if player is in jail" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val resultGame = Monopoly.hand@@lePlayerTurn(updatedGame)
      // Assert that handleJailTurn was called (we can't directly test the call,
      // but we can check a side effect or the return type if it's distinct)
      resultGame.currentPlayer.isInJail should be(true)
    }

    "call handleRegularTurn if player is not in jail" in {
      val resultGame = Monopoly.handlePlayerTurn(game)
      // Assert that handleRegularTurn was called (similarly, check for a side effect)
      resultGame.currentPlayer.position should not be(player1.position)
    }
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: handlePlayerTurn.