package de.htwg.model.FileIOComponent.JSONFileIO


import de.htwg.model.{IMonopolyGame, IPlayer}
import de.htwg.model.modelBaseImple.{BuyableField, MonopolyGame, Player, PropertyField}
import de.htwg.Board
import de.htwg.model.FileIOComponent.Data.{GameSnapshot, PlayerSnapshot, PropertySnapshot}
import de.htwg.model.FileIOComponent.IFileIO
import scala.util.{Failure, Success, Try}
import java.io.{BufferedReader, File, FileInputStream, FileReader, PrintWriter}
import scala.io.Source

class JSONFileIO extends IFileIO {

  override def save(game: IMonopolyGame, filename: String): Try[Unit] = Try {
    val snapshot = createSnapshot(game)
    val json = gameToJSON(snapshot)

    val writer = new PrintWriter(new File(s"$filename.json"))
    try {
      writer.write(json)
    } finally {
      writer.close()
    }
  }

  override def load(filename: String): Try[IMonopolyGame] = Try {
    val source = Source.fromFile(s"$filename.json")
    val json = try source.mkString finally source.close()
    val snapshot = jsonToGame(json)
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

  private def gameToJSON(snapshot: GameSnapshot): String = {
    val playersJson = snapshot.players.map { p =>
      s"""    {
      "name": "${p.name}",
      "balance": ${p.balance},
      "position": ${p.position},
      "isInJail": ${p.isInJail},
      "consecutiveDoubles": ${p.consecutiveDoubles}
    }"""
    }.mkString(",\n")

    val propertiesJson = snapshot.boardProperties.map { p =>
      s"""    {
      "index": ${p.index},
      "owner": ${p.ownerName.map(name => s""""$name"""").getOrElse("null")},
      "houses": ${p.houses}
    }"""
    }.mkString(",\n")

    s"""{
  "sound": ${snapshot.sound},
  "currentPlayerIndex": ${snapshot.currentPlayerIndex},
  "players": [
$playersJson
  ],
  "properties": [
$propertiesJson
  ]
}"""
  }

  private def jsonToGame(json: String): GameSnapshot = {
    // Simple JSON parsing (in a real project, you'd use a JSON library like Play JSON)
    val lines = json.split('\n').map(_.trim)

    val sound = extractValue(lines, "sound").toBoolean
    val currentPlayerIndex = extractValue(lines, "currentPlayerIndex").toInt

    // Extract players (simplified parsing)
    val playersSection = extractSection(json, "players")
    val players = parsePlayersFromJSON(playersSection)

    // Extract properties
    val propertiesSection = extractSection(json, "properties")
    val properties = parsePropertiesFromJSON(propertiesSection)

    GameSnapshot(players, currentPlayerIndex, properties, sound)
  }

  private def extractValue(lines: Array[String], key: String): String = {
    lines.find(_.contains(s""""$key":"""))
      .map(_.split(":")(1).trim.stripSuffix(","))
      .getOrElse("")
  }

  private def extractSection(json: String, sectionName: String): String = {
    val startPattern = s""""$sectionName": ["""
    val start = json.indexOf(startPattern)
    if (start == -1) return ""

    var bracketCount = 0
    var inSection = false
    var end = start + startPattern.length

    for (i <- (start + startPattern.length) until json.length) {
      json(i) match {
        case '[' => bracketCount += 1; inSection = true
        case ']' =>
          bracketCount -= 1
          if (bracketCount == -1) {
            end = i
            return json.substring(start + startPattern.length, end)
          }
        case _ =>
      }
    }
    ""
  }

  private def parsePlayersFromJSON(playersJson: String): Vector[PlayerSnapshot] = {
    val playerObjects = playersJson.split("},").map(_.trim + "}")
    playerObjects.map { playerObj =>
      val name = extractJSONField(playerObj, "name").stripPrefix("\"").stripSuffix("\"")
      val balance = extractJSONField(playerObj, "balance").toInt
      val position = extractJSONField(playerObj, "position").toInt
      val isInJail = extractJSONField(playerObj, "isInJail").toBoolean
      val consecutiveDoubles = extractJSONField(playerObj, "consecutiveDoubles").toInt

      PlayerSnapshot(name, balance, position, isInJail, consecutiveDoubles)
    }.toVector
  }

  private def parsePropertiesFromJSON(propertiesJson: String): Vector[PropertySnapshot] = {
    val propertyObjects = propertiesJson.split("},").map(_.trim + "}")
    propertyObjects.map { propObj =>
      val index = extractJSONField(propObj, "index").toInt
      val ownerStr = extractJSONField(propObj, "owner")
      val owner = if (ownerStr == "null") None else Some(ownerStr.stripPrefix("\"").stripSuffix("\""))
      val houses = extractJSONField(propObj, "houses").toInt

      PropertySnapshot(index, owner, houses)
    }.toVector
  }

  private def extractJSONField(obj: String, field: String): String = {
    val pattern = s""""$field":\\s*([^,}]+)""".r
    pattern.findFirstMatchIn(obj).map(_.group(1).trim).getOrElse("")
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
            propSnapshot.ownerName.foreach { ownerName =>
              val owner = players.find(_.name == ownerName)
              owner.foreach { o =>
                val updatedField = buyable.withNewOwner(o)
                val newFields = updatedBoard.fields.updated(fieldIndex, updatedField)
                updatedBoard = updatedBoard.copy(fields = newFields)
              }
            }
          case _ => // Non-buyable field, skip
        }
      }
    }

    val currentPlayer = players(snapshot.currentPlayerIndex)
    MonopolyGame(players, updatedBoard, currentPlayer, snapshot.sound)
  }
}

