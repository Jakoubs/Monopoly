package de.htwg.model.FileIOComponent

import java.nio.file.{Files, Paths}

object SaveManager {
  private val saveDir = Paths.get("saves")
  Files.createDirectories(saveDir)

  def listSlots: Vector[String] =
    Option(saveDir.toFile.list)
      .getOrElse(Array.empty[String])
      .toVector
      .filter(n => n.endsWith(".xml") || n.endsWith(".json"))


  def slotPath(name: String): String =
    saveDir.resolve(name).toString
}
