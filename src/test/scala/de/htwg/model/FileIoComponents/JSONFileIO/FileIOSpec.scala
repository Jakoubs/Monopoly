package de.htwg.model.FileIoComponents.JSONFileIO

import java.io.File
import scala.util.{Success, Try}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import de.htwg.model.FileIOComponent.JSONFileIO.FileIO
import de.htwg.model.FileIOComponent.IFileIO
import de.htwg.model.IMonopolyGame
import de.htwg.model.modelBaseImple._
import de.htwg.Board

class FileIOSpec extends AnyWordSpec with Matchers {

  private val testFile = "jsonTestGame"

  /** Löscht vorhandene .json-Datei vor jedem Test */
  private def deleteFile(name: String): Unit = {
    val f = new File(s"$name.json")
    if (f.exists()) f.delete()
  }

  "A JSON FileIO" should {

    "save and load a simple MonopolyGame preserving players, currentPlayer and sound" in {
      deleteFile(testFile)
      val fileIO: IFileIO = new FileIO()

      // Minimal-Spiel mit einem Spieler und einem PropertyField
      val p1 = Player("Tester", 500, position = 1, isInJail = false, consecutiveDoubles = 0)
      val fields = Vector[BoardField](
        PropertyField(
          name     = "onlyProp",
          index    = 1,
          price    = 100,
          rent     = 10,
          owner    = Some(p1),
          color    = PropertyField.Color.Brown,
          mortgage = PropertyField.Mortgage(50, false),
          house    = PropertyField.House(0)
        )
      )
      val game: IMonopolyGame = new MonopolyGame(
        players       = Vector(p1),
        board         = Board(fields),
        currentPlayer = p1,
        sound         = true
      )

      // Speichern
      fileIO.save(game, testFile) shouldBe a[Success[_]]

      // Laden
      val loadedTry: Try[IMonopolyGame] = fileIO.load(testFile)
      loadedTry       shouldBe a[Success[_]]
      val loadedGame  = loadedTry.get

      // Vergleiche Spieler, currentPlayer, sound
      loadedGame.players.map(_.name) should contain theSameElementsInOrderAs game.players.map(_.name)
      loadedGame.currentPlayer.name    shouldBe game.currentPlayer.name
      loadedGame.sound                 shouldBe game.sound
    }
    
  }
}

