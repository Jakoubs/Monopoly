package de.htwg.model.FileIOComponent

import de.htwg.model.FileIOComponent.JSONFileIO.{FileIO => JSONFileIO}
import de.htwg.model.FileIOComponent.XMLFileIO.{FileIO => XMLFileIO}

object FileIOFactory {
  def createFileIO(format: String): IFileIO = {
    format.toLowerCase match {
      case "json" => new JSONFileIO()
      case "xml" => new XMLFileIO()
      case other => throw new IllegalArgumentException(s"Unsupported format: $other")
    }
  }
}

