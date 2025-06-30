package de.htwg.model.FileIOComponent

import de.htwg.model.FileIOComponent.JSONFileIO.FileIO as JSONFileIO
import de.htwg.model.FileIOComponent.XMLFileIO.FileIO as XMLFileIO
import de.htwg.model.FileIOComponent.IFileIO

object FileIOModule:
  def select(format: String): IFileIO =
    format.toLowerCase match
      case "xml" => new XMLFileIO()
      case _      => new JSONFileIO()     // deine XML-Impl


