package de.htwg.model.FileIoComponents
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import de.htwg.model.FileIOComponent.FileIOModule
import de.htwg.model.FileIOComponent.JSONFileIO.FileIO   as JSONFileIO
import de.htwg.model.FileIOComponent.XMLFileIO.FileIO    as XMLFileIO
import de.htwg.model.FileIOComponent.IFileIO

class FileIOModuleSpec extends AnyWordSpec with Matchers {

  "FileIOModule.select" should {

    "return an XMLFileIO instance for format \"xml\"" in {
      val io: IFileIO = FileIOModule.select("xml")
      io shouldBe a [XMLFileIO]
    }

    "be case-insensitive when selecting XML" in {
      val ioUpper: IFileIO = FileIOModule.select("XML")
      val ioMixed: IFileIO = FileIOModule.select("xMl")
      ioUpper shouldBe a [XMLFileIO]
      ioMixed shouldBe a [XMLFileIO]
    }

    "return a JSONFileIO instance for any other format" in {
      val ioJsonLower: IFileIO = FileIOModule.select("json")
      val ioOther:     IFileIO = FileIOModule.select("foobar")
      val ioEmpty:     IFileIO = FileIOModule.select("")
      ioJsonLower shouldBe a [JSONFileIO]
      ioOther     shouldBe a [JSONFileIO]
      ioEmpty     shouldBe a [JSONFileIO]
    }
  }
}
