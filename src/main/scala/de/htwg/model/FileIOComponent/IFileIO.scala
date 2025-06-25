package de.htwg.model.FileIOComponent


import de.htwg.model.IMonopolyGame
import scala.util.Try

trait IFileIO {
  def save(game: IMonopolyGame, filename: String): Try[Unit]
  def load(filename: String): Try[IMonopolyGame]
}