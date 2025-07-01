package de.htwg.model.FileIoComponents.XMLFileIO
import java.io.File
import scala.util.{Success, Failure, Try}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import de.htwg.model.FileIOComponent.XMLFileIO.FileIO
import de.htwg.model.FileIOComponent.IFileIO
import de.htwg.model.IMonopolyGame
import de.htwg.model.modelBaseImple.{MonopolyGame, Player, PropertyField, BoardField}
import de.htwg.Board

class FileIOSpec extends AnyWordSpec with Matchers {

  private val testFile = "testGame"

  /** Hilfsmethode zum Löschen alter Testdateien */
  private def deleteFile(name: String): Unit = {
    val f = new File(s"$name.xml")
    if (f.exists()) f.delete()
  }

  "An XML FileIO" should {

    "save and load a simple MonopolyGame preserving players, currentPlayer and sound" in {
      deleteFile(testFile)
      val fileIO: IFileIO = new FileIO()

      // Wir bauen ein minimales Spiel mit exakt einem PropertyField
      val p1 = Player("TestPlayer", 1234, position = 1, isInJail = false, consecutiveDoubles = 0)
      val fields = Vector[BoardField](
        PropertyField(
          name = "onlyProp",
          index = 1,
          price = 50,
          rent = 5,
          owner = Some(p1),
          color = PropertyField.Color.Brown,
          mortgage = PropertyField.Mortgage(25, false),
          house = PropertyField.House(0)
        )
      )
      val game: IMonopolyGame =
        new MonopolyGame(Vector(p1), Board(fields), p1, sound = true)

      // 1) Speichern
      fileIO.save(game, testFile) shouldBe a[Success[_]]

      // 2) Laden
      val loadedTry: Try[IMonopolyGame] = fileIO.load(testFile)
      loadedTry shouldBe a[Success[_]]

      val loadedGame = loadedTry.get

      // 3) Assertions: Spieler, currentPlayer, sound
      loadedGame.players.map(_.name) should contain theSameElementsInOrderAs game.players.map(_.name)
      loadedGame.currentPlayer.name shouldBe game.currentPlayer.name
      loadedGame.sound shouldBe game.sound

      // Es reicht, hier nicht auf Board/Felder zu testen
    }
  }
}
