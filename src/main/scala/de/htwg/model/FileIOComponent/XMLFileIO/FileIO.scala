package de.htwg.model.FileIOComponent.XMLFileIO

import de.htwg.model.{IMonopolyGame, IPlayer}
import de.htwg.model.modelBaseImple.{BuyableField, MonopolyGame, Player, PropertyField}
import de.htwg.Board
import de.htwg.model.FileIOComponent.Data.{GameSnapshot, PlayerSnapshot, PropertySnapshot}
import de.htwg.model.FileIOComponent.IFileIO
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, Node, XML}
import java.io.{File, PrintWriter}

class FileIO extends IFileIO {

  override def save(game: IMonopolyGame, filename: String): Try[Unit] = Try {
    val snapshot = createSnapshot(game)
    val xml = gameToXML(snapshot)

    val writer = new PrintWriter(new File(s"$filename.xml"))
    try {
      writer.write(xml.toString)
    } finally {
      writer.close()
    }
  }

  override def load(filename: String): Try[IMonopolyGame] = Try {
    val xml = XML.loadFile(s"$filename.xml")
    val snapshot = xmlToGame(xml)
    reconstructGame(snapshot)
  }

  private def createSnapshot(game: IMonopolyGame): GameSnapshot = {
    val playerSnapshots = game.players.map(p => PlayerSnapshot(
      p.name, p.balance, p.position, p.isInJail, p.consecutiveDoubles
    ))

    val currentPlayerIndex = game.players.indexOf(game.currentPlayer)

    val propertySnapshots = game.board.fields.zipWithIndex.collect {
      case (field: PropertyField, idx) => PropertySnapshot(
        idx + 1,
        field.owner.map(_.name),
        field.house.amount
      )
      case (field: BuyableField, idx) => PropertySnapshot(
        idx + 1,
        field.owner.map(_.name),
        0
      )
    }.toVector

    GameSnapshot(playerSnapshots, currentPlayerIndex, propertySnapshots, game.sound)
  }

  private def gameToXML(snapshot: GameSnapshot): Elem = {
    <monopolygame>
      <sound>{snapshot.sound}</sound>
      <currentPlayer>{snapshot.currentPlayerIndex}</currentPlayer>
      <players>
        {snapshot.players.map(p =>
        <player>
          <name>{p.name}</name>
          <balance>{p.balance}</balance>
          <position>{p.position}</position>
          <isInJail>{p.isInJail}</isInJail>
          <consecutiveDoubles>{p.consecutiveDoubles}</consecutiveDoubles>
        </player>
      )}
      </players>
      <properties>
        {snapshot.boardProperties.map(p =>
        <property>
          <index>{p.index}</index>
          <owner>{p.ownerName.getOrElse("")}</owner>
          <houses>{p.houses}</houses>
        </property>
      )}
      </properties>
    </monopolygame>
  }

  private def xmlToGame(xml: Node): GameSnapshot = {
    val sound = (xml \ "sound").text.toBoolean
    val currentPlayerIndex = (xml \ "currentPlayer").text.toInt

    val players = (xml \ "players" \ "player").map { playerNode =>
      PlayerSnapshot(
        (playerNode \ "name").text,
        (playerNode \ "balance").text.toInt,
        (playerNode \ "position").text.toInt,
        (playerNode \ "isInJail").text.toBoolean,
        (playerNode \ "consecutiveDoubles").text.toInt
      )
    }.toVector

    val properties = (xml \ "properties" \ "property").map { propNode =>
      val owner = (propNode \ "owner").text
      PropertySnapshot(
        (propNode \ "index").text.toInt,
        if (owner.isEmpty) None else Some(owner),
        (propNode \ "houses").text.toInt
      )
    }.toVector

    GameSnapshot(players, currentPlayerIndex, properties, sound)
  }

  private def reconstructGame(snapshot: GameSnapshot): IMonopolyGame = {
    // Create players from snapshot
    val players = snapshot.players.map(ps =>
      Player(ps.name, ps.balance, ps.position, ps.isInJail, ps.consecutiveDoubles)
    )

    // Get the original board from your defineGame method
    val originalGame = de.htwg.Monopoly.defineGame()
    var updatedBoard = originalGame.board

    // Update board with ownership and houses
    snapshot.boardProperties.foreach { propSnapshot =>
      val fieldIndex = propSnapshot.index - 1
      if (fieldIndex >= 0 && fieldIndex < updatedBoard.fields.length) {
        updatedBoard.fields(fieldIndex) match {
          case prop: PropertyField =>
            val owner = propSnapshot.ownerName.flatMap(name =>
              players.find(_.name == name)
            )
            val updatedProp = prop.copy(
              owner = owner,
              house = PropertyField.House(propSnapshot.houses)
            )
            val newFields = updatedBoard.fields.updated(fieldIndex, updatedProp)
            updatedBoard = updatedBoard.copy(fields = newFields)
          case buyable: BuyableField =>
            val owner = propSnapshot.ownerName.flatMap(name =>
              players.find(_.name == name)
            )
            val updatedField = buyable.withNewOwner(owner.get)
            val newFields = updatedBoard.fields.updated(fieldIndex, updatedField)
            updatedBoard = updatedBoard.copy(fields = newFields)
          case _ => // Non-buyable field, skip
        }
      }
    }

    val currentPlayer = players(snapshot.currentPlayerIndex)
    MonopolyGame(players, updatedBoard, currentPlayer, snapshot.sound)
  }
}
