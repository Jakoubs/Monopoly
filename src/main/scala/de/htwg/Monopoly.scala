package de.htwg.model

import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.PropertyField
import scala.io.StdIn.readLine

case class Board(fields: Vector[BoardField])

object Monopoly:
  def main(args: Array[String]): Unit = {
    var game = defineGame()
    printBoard(game)
    while (game.players.size > 1) {
      println(s"${game.currentPlayer.name}'s turn")
      val (updatedPlayer, updatedGame) = playerTurn(game.currentPlayer, game)

      val updatedPlayers = updatedGame.players.map(p =>
        if (p.name == updatedPlayer.name) updatedPlayer else p
      )


      val nextPlayer = updatedPlayers((updatedPlayers.indexOf(updatedPlayer) + 1) % updatedPlayers.size)

      game = game.copy(players = updatedPlayers, currentPlayer = nextPlayer)

      printBoard(game)
    }
  }


  def playerTurn(player: Player, game: MonopolyGame): (Player, MonopolyGame) = {
    val dice = new Dice()
    val updatedPlayer = player.playerMove(() => dice.rollDice(), 1)

    // Prüfen, ob der Spieler auf einem kaufbaren Feld gelandet ist
    val currentFieldOption = game.board.fields.find(_.index == updatedPlayer.position)

    currentFieldOption match {
      case Some(field: PropertyField) if field.owner.isEmpty =>
        println(s"Sie sind auf dem freien Grundstück ${field.name} gelandet. Möchten Sie es für ${field.price} kaufen? (j/n)")
        val answer = readLine().trim.toLowerCase
        if (answer == "j") {
          val (updatedGame, playerAfterBuying) = buyProperty(game, field.index, updatedPlayer)
          return (playerAfterBuying, updatedGame)
        }
      case Some(field: TrainStationField) if field.owner.isEmpty =>
        println(s"Sie sind auf dem Bahnhof ${field.name} gelandet. Möchten Sie ihn kaufen? (j/n)")
        val answer = readLine().trim.toLowerCase
        if (answer == "j") {
          val (updatedGame, playerAfterBuying) = buyProperty(game, field.index, updatedPlayer)
          return (playerAfterBuying, updatedGame)
        }
      case Some(field: UtilityField) if field.owner.isEmpty =>
        println(s"Sie sind auf dem Versorgungswerk ${field.name} gelandet. Möchten Sie es kaufen? (j/n)")
        val answer = readLine().trim.toLowerCase
        if (answer == "j") {
          val (updatedGame, playerAfterBuying) = buyProperty(game, field.index, updatedPlayer)
          return (playerAfterBuying, updatedGame)
        }
      case _ => // Nichts zu tun
    }

    // Nach dem Würfeln weitere Aktionen anbieten
    println("Möchten Sie eine Aktion ausführen? (h: Haus kaufen, k: Immobilie kaufen, n: Nächster Spieler)")
    val action = readLine().trim.toLowerCase

    action match {
      case "h" =>
        println("Auf welchem Grundstück möchten Sie ein Haus bauen? (Geben Sie den Index ein)")
        try {
          val propertyIndex = readLine().toInt
          val (updatedGame, playerAfterBuying) = buyHouse(game, propertyIndex, updatedPlayer)
          (playerAfterBuying, updatedGame)
        } catch {
          case _: NumberFormatException =>
            println("Ungültige Eingabe. Bitte geben Sie eine Zahl ein.")
            (updatedPlayer, game)
        }
      case "k" =>
        println("Welche Immobilie möchten Sie kaufen? (Geben Sie den Index ein)")
        try {
          val propertyIndex = readLine().toInt
          val (updatedGame, playerAfterBuying) = buyProperty(game, propertyIndex, updatedPlayer)
          (playerAfterBuying, updatedGame)
        } catch {
          case _: NumberFormatException =>
            println("Ungültige Eingabe. Bitte geben Sie eine Zahl ein.")
            (updatedPlayer, game)
        }
      case _ =>
        (updatedPlayer, game)
    }
  }

  import scala.util.Random

  def randomEmoji(): String = {
    val emojis = List(
      "🐶", "🐱", "🐯", "🦁", "🐻", "🐼", "🦊", "🐺", "🦄", "🐲", "🦉",
      "🦅", "🐝", "🦋", "🐙", "🦑", "🦈", "🐊", "🦖", "🦓", "🦒", "🐘",
      "🦔", "🐢", "🐸", "🦜", "👑", "🤖", "👽", "🧙", "🧛", "🧟", "👻",
      "🦸", "🧚", "🥷")

    emojis(Random.nextInt(emojis.size))
  }

  def defineGame(): MonopolyGame = {
    println("Wie viele Spieler? (2-4):")
    val playerAnz = readLine().toInt
    var playerVector = Vector[Player]()

    for (i <- 1 to playerAnz) {
      val playerName = randomEmoji()
      playerVector = playerVector.appended(Player(playerName, 1500, 1))
      println(s"Spieler $playerName hinzugefügt.")
    }


    val board = Board(
      Vector(
        GoField,
        PropertyField("brown1",2,100,10,None,color = Brown,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        CommunityChestField(3),
        PropertyField("brown2",4,100,10,None,color = Brown,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        TaxField(100,5),
        TrainStationField("Marklylebone Station",6,None),
        PropertyField("lightBlue1",7,100,10,None,color = LightBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        ChanceField(8),
        PropertyField("lightBlue2",9,100,10,None,color = LightBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("lightBlue3",10,100,10,None,color = LightBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        JailField,
        PropertyField("Pink1",12,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        UtilityField("Electric Company", 13, None),
        PropertyField("Pink2",14,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(5)),
        PropertyField("Pink3",15,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        TrainStationField("Fenchurch ST Station",16,None),
        PropertyField("Orange1",17,100,10,None,color = Orange,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        CommunityChestField(18),
        PropertyField("Orange2",19,100,10,None,color = Orange,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("Orange3",20,100,10,None,color = Orange,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        FreeParkingField(0),
        PropertyField("Red1",22,100,10,None,color = Red,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        ChanceField(23),
        PropertyField("Red2",24,100,10,None,color = Red,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("Red3",25,100,10,None,color = Red,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        TrainStationField("Kings Cross Station",26,None),
        PropertyField("Yellow1",27,100,10,None,color = Yellow,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("Yellow2",28,100,10,None,color = Yellow,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        UtilityField("Water Works",29,None),
        PropertyField("Yellow3",30,100,10,None,color = Yellow,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        GoToJailField(),
        PropertyField("Green1",32,100,10,None,color = Green,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("Green2",33,100,10,None,color = Green,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        ChanceField(34),
        PropertyField("Green3",35,100,10,None,color = Green,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        TrainStationField("Liverpool ST Station",36,None),
        ChanceField(37),
        PropertyField("Blue1",38,100,10,None,color = DarkBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        TaxField(200,39),
        PropertyField("Blue2",40,100,10,None,color = DarkBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
      )
    )
    val game = MonopolyGame(playerVector, board, playerVector.head)
    println(s"Spiel gestartet mit ${playerVector.size} Spielern.")
    game
  }

  def printBoard(game: MonopolyGame): Unit = {
    printTop(game)
    printSides(game)
    printBottom(game)
  }

  def printTop(game: MonopolyGame): Unit = {
    val (stats1, stats2, stats3, stats4) = getStats(game)

    val line1 = "+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+"

    val baseLines = List(
      "|  ______  _____  |",
      "| |  ____ |     | |",
      "| |_____| |_____| |"
    )

    val fields2To10 = game.board.fields.filter(field => field.index > 1 && field.index < 11)

    val line2 = fields2To10.foldLeft(baseLines(0))((line, field) =>
      line + fillSpace(("Nr" + field.index.toString + getExtra(field)), 8) + "|") + "                 |"

    val line3 = fields2To10.foldLeft(baseLines(1))((line, field) =>
      line + fillSpace(getPrice(field), 8) + '|') + "__________       |"

    val line4 = fields2To10.foldLeft(baseLines(2))((line, field) =>
      line + fillSpace(playersOnIndex(field.index, game, false), 8) + '|') + "  JAIL    |      |"

    val additionalLines = List(
      "|          Ss.    |--------+--------+--------+--------+--------+--------+--------+--------+--------+          |      |",
      "|  ssssssssSSSS   |  " + fillSpace(stats1, 76) + "  |          |      |",
      "|          ;:`    |  " + fillSpace(stats2, 76) + "  |          |      |",
      "|" + fillSpace(playersOnIndex(1, game, false), 17) + "|  " + fillSpace(stats3, 76) + "  |" + fillSpace(playersOnIndex(11, game, true), 10) + "|" + fillSpace(playersOnIndex(11, game, false), 6) + "|",
      "+--------+--------+  " + fillSpace(stats4, 76) + "  +--------+-+------+"
    )

    println(line1)
    println(line2)
    println(line3)
    println(line4)
    additionalLines.foreach(println)
  }

  def printSides(game: MonopolyGame): Unit = {
    (12 to 20).foreach { a =>
      val fieldA = game.board.fields.find(_.index == 52 - a).get
      val fieldB = game.board.fields.find(_.index == a).get

      val lines = List(
        '|' + fillSpace(fillSpace(fieldA.index.toString + getExtra(fieldA), 8) + '|', 107) + '|' + fillSpace(fieldB.index.toString + getExtra(fieldB), 8) + '|',
        '|' + fillSpace(fillSpace(getPrice(fieldA), 8) + '|', 107) + '|' + fillSpace(getPrice(fieldB), 8) + '|',
        '|' + fillSpace(fillSpace(playersOnIndex(52 - a, game, false), 8) + '|', 107) + '|' + fillSpace(playersOnIndex(a, game, false), 8) + '|',
        if (a != 20) "+--------+                                                                                                  +--------+"
        else "+--------+--------+                                                                                +--------+--------+"
      )

      lines.foreach(println)
    }
  }

  def printBottom(game: MonopolyGame): Unit = {
    val fixedLines = List(
      "|   GO TO JAIL    |                                                                                |  FREE PARIKING  |",
      "|     ---->       |                                                                                |   ______        |",
      "|                 |                                                                                |  /|_||_`.__     |",
      "|                 +--------+--------+--------+--------+--------+--------+--------+--------+--------+ (   _    _ _\\   |"
    )

    val fields22To30 = (22 to 30).map(a => game.board.fields.find(_.index == 52 - a).get)

    val line6 = fields22To30.foldLeft("|                 |")((line, field) =>
      line + fillSpace(field.index.toString + getExtra(field), 8) + '|') + " =`-(_)--(_)-`   |"

    val line7 = fields22To30.foldLeft("|                 |")((line, field) =>
      line + fillSpace(getPrice(field), 8) + '|') + "   Money [" + getPrice(game.board.fields.find(_.index == 21).get) + "]    |"

    val line8 = fields22To30.foldLeft("|" + fillSpace(playersOnIndex(31, game, false), 17) + "|")((line, field) =>
      line + fillSpace(playersOnIndex(field.index, game, false), 8) + '|') + fillSpace(playersOnIndex(21, game, false), 17) + '|'

    val line9 = "+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+                " + getInventory(game)

    fixedLines.foreach(println)
    println(line6)
    println(line7)
    println(line8)
    println(line9)
  }

  def fillSpace(input: String, maxChar: Int): String = {
    input.padTo(maxChar, ' ')
  }

  def getPrice(field: BoardField): String = {
    field match {
      case pf: PropertyField => pf.price.toString + '$'
      case tf: TrainStationField => "200$"
      case fp: FreeParkingField => fp.amount.toString + '$'
      case _ => ""
    }
  }

  def getExtra(field: BoardField): String = {
    field match {
      case pf: PropertyField =>
        pf.owner match {
          case Some(ownerName) => (ownerName + " [" + pf.house.amount.toString + ']')
          case None => ""
        }
      case ts: TrainStationField =>
        ts.owner match {
          case Some(ownerName) => " " + ownerName
          case None => ""
        }
      case uf: UtilityField =>
        uf.owner match {
          case Some(ownerName) => " " + ownerName
          case None => ""
        }
      case _ => ""
    }
  }

  def playersOnIndex(idx: Int, game: MonopolyGame, inJail: Boolean): String = {
    game.players
      .filter(p => p.position == idx && p.isInJail == inJail)
      .map(_.name + " ")
      .mkString
  }

  def getStats(game: MonopolyGame): (String, String, String, String) = {
    // Create information strings for all players
    val playerInfos = game.players.map(p =>
      p.name + " pos[" + p.position + "], balance[" + p.balance + "], isInJail[" + p.isInJail + "]    "
    )

    playerInfos.foldLeft(("", "", "", "")) {
      case ((s1, s2, s3, s4), info) =>
        if (s1.length < 20) (s1 + info, s2, s3, s4)
        else if (s2.length < 20) (s1, s2 + info, s3, s4)
        else if (s3.length < 20) (s1, s2, s3 + info, s4)
        else (s1, s2, s3, s4 + info)
    }
  }


  def getInventory(game: MonopolyGame): String = {
    val header = "INVENTORY Player: " + game.currentPlayer.name + "|"

    game.board.fields.foldLeft(header) { (acc, field) =>
      field match {
        case pf: PropertyField if pf.owner.equals(game.currentPlayer.name) =>
          acc + "idx:" + pf.index + "[" + pf.house.amount + "], "

        case ts: TrainStationField if ts.owner.equals(game.currentPlayer.name) =>
          acc + "idx:" + ts.index + ", "

        case uf: UtilityField if uf.owner.equals(game.currentPlayer.name) =>
          acc + "idx:" + uf.index + ", "

        case _ => acc
      }
    }
  }


  def buyHouse(game: MonopolyGame, propertyIndex: Int, player: Player): (MonopolyGame, Player) = {
    // Überprüfe, ob das Feld im Spielbrett existiert
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        // Überprüfe, ob der Spieler der Eigentümer ist
        field.owner match {
          case Some(owner) if owner == player.name =>
            // Überprüfe, ob der Spieler genug Geld hat (Kosten für Haus: 50)
            val houseCost = 50
            if (player.balance >= houseCost) {
              // Aktualisiere das Feld mit einem neuen Haus
              val updatedField = field.copy(
                house = PropertyField.House(field.house.amount + 1)
              )

              // Aktualisiere das Spielbrett
              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              // Aktualisiere den Spieler mit reduziertem Geld
              val updatedPlayer = player.copy(balance = player.balance - houseCost)

              // Aktualisiere das Spiel
              val updatedGame = game.copy(board = updatedBoard)

              println(s"${player.name} hat ein Haus auf ${field.name} gebaut.")
              (updatedGame, updatedPlayer)
            } else {
              println(s"Nicht genug Geld! Ein Haus kostet $houseCost, aber ${player.name} hat nur ${player.balance}.")
              (game, player)
            }
          case _ =>
            println(s"${player.name} ist nicht der Eigentümer dieser Immobilie.")
            (game, player)
        }
      case Some(_) =>
        println(s"Auf dem Feld mit Index $propertyIndex kann kein Haus gebaut werden.")
        (game, player)
      case None =>
        println(s"Feld mit Index $propertyIndex nicht gefunden.")
        (game, player)
    }
  }

  def buyProperty(game: MonopolyGame, propertyIndex: Int, player: Player): (MonopolyGame, Player) = {
    // Überprüfe, ob das Feld im Spielbrett existiert
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        // Überprüfe, ob die Immobilie noch keinen Eigentümer hat
        field.owner match {
          case None =>
            // Überprüfe, ob der Spieler genug Geld hat
            if (player.balance >= field.price) {
              // Aktualisiere das Feld mit dem neuen Eigentümer
              val updatedField = field.copy(
                owner = Some(player.name)
              )

              // Aktualisiere das Spielbrett
              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              // Aktualisiere den Spieler mit reduziertem Geld
              val updatedPlayer = player.copy(balance = player.balance - field.price)

              // Aktualisiere das Spiel
              val updatedGame = game.copy(board = updatedBoard)

              println(s"${player.name} hat die Immobilie ${field.name} für ${field.price} gekauft.")
              (updatedGame, updatedPlayer)
            } else {
              println(s"Nicht genug Geld! Die Immobilie kostet ${field.price}, aber ${player.name} hat nur ${player.balance}.")
              (game, player)
            }
          case Some(owner) =>
            println(s"Diese Immobilie gehört bereits ${owner}.")
            (game, player)
        }
      case Some(field: TrainStationField) =>
        // Ähnliche Logik für Bahnhöfe
        field.owner match {
          case None =>
            val stationPrice = 200 // Typischer Preis für Bahnhöfe
            if (player.balance >= stationPrice) {
              val updatedField = field.copy(owner = Some(player.name))
              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              val updatedPlayer = player.copy(balance = player.balance - stationPrice)
              val updatedGame = game.copy(board = updatedBoard)

              println(s"${player.name} hat den Bahnhof ${field.name} für $stationPrice gekauft.")
              (updatedGame, updatedPlayer)
            } else {
              println(s"Nicht genug Geld! Der Bahnhof kostet $stationPrice, aber ${player.name} hat nur ${player.balance}.")
              (game, player)
            }
          case Some(owner) =>
            println(s"Dieser Bahnhof gehört bereits ${owner}.")
            (game, player)
        }
      case Some(field: UtilityField) =>
        // Ähnliche Logik für Versorgungswerke
        field.owner match {
          case None =>
            val utilityPrice = 150 // Typischer Preis für Versorgungswerke
            if (player.balance >= utilityPrice) {
              val updatedField = field.copy(
                owner = Some(player.name)
              )

              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              val updatedPlayer = player.copy(balance = player.balance - utilityPrice)
              val updatedGame = game.copy(board = updatedBoard)

              println(s"${player.name} hat das Versorgungswerk ${field.name} für $utilityPrice gekauft.")
              (updatedGame, updatedPlayer)
            } else {
              println(s"Nicht genug Geld! Das Versorgungswerk kostet $utilityPrice, aber ${player.name} hat nur ${player.balance}.")
              (game, player)
            }
          case Some(owner) =>
            println(s"Dieses Versorgungswerk gehört bereits ${owner}.")
            (game, player)
        }
      case Some(_) =>
        println(s"Das Feld mit Index $propertyIndex kann nicht gekauft werden.")
        (game, player)
      case None =>
        println(s"Feld mit Index $propertyIndex nicht gefunden.")
        (game, player)
    }
  }

case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player
                       )