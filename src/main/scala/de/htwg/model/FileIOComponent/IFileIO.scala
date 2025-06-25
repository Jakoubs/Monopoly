package de.htwg.model.FileIOComponent


import de.htwg.model.IMonopolyGame
import scala.util.Try

trait IFileIO {
  def save(game: IMonopolyGame, path: String): Try[Unit]
  def load(path: String): Try[IMonopolyGame]
}
