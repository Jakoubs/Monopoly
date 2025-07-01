package de.htwg.model.FileIoComponents

import java.io.File
import java.nio.file.{Files, Paths}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import de.htwg.model.FileIOComponent.SaveManager

class SaveManagerSpec extends AnyWordSpec with Matchers {

  private val savesDir = Paths.get("saves").toFile

  private def clearSaves(): Unit = {
    if (savesDir.exists() && savesDir.isDirectory) {
      savesDir.listFiles().foreach(_.delete())
    }
  }

  "SaveManager" should {

    "create the saves directory on initialization" in {
      savesDir.exists() shouldBe true
      savesDir.isDirectory shouldBe true
    }

    "return an empty listSlots when no save files are present" in {
      clearSaves()
      SaveManager.listSlots shouldBe empty
    }

    "list only .xml and .json files in listSlots" in {
      clearSaves()
      new File(savesDir, "game1.xml").createNewFile()
      new File(savesDir, "game2.json").createNewFile()
      new File(savesDir, "ignore.txt").createNewFile()
      new File(savesDir, "another.XML.backup").createNewFile()

      val slots = SaveManager.listSlots.sorted
      slots should contain theSameElementsAs Vector("game1.xml", "game2.json")
    }

    "build the correct full path via slotPath" in {
      val fileName = "mySave.json"
      val expected = savesDir.toPath.resolve(fileName).toString

      SaveManager.slotPath(fileName) shouldBe expected
    }
  }
}

