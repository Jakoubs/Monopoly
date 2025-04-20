package de.htwg.model

import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.PropertyField
import scala.io.StdIn.readLine
import scala.util.Random

case class Board(fields: Vector[BoardField])

object Monopoly:
  def main(args: Array[String]): Unit = {
    var game = defineGame()
    printBoard(game)
    while (game.players.size > 1) {
      println(s"${game.currentPlayer.name}'s turn")
      val playerId = game.players.indexOf(game.currentPlayer)
      val updatedGame = handlePlayerTurn(game)
      val updatedPlayer = updatedGame.players(playerId)
      val updatedPlayers = updatedGame.players.map(p =>
        if (p.name == updatedPlayer.name) updatedPlayer else p
      )
      val updatedBoard = updatedGame.board


      val nextPlayer = updatedPlayers((updatedPlayers.indexOf(updatedPlayer) + 1) % updatedPlayers.size)

      game = game.copy(players = updatedPlayers, currentPlayer = nextPlayer,board = updatedBoard)

      printBoard(game)
      print(game.players)
    }
  }


  def handlePlayerTurn(game: MonopolyGame): MonopolyGame = {
    if (game.currentPlayer.isInJail) {
      handleJailTurn(game)
    } else {
      handleRegularTurn(game)
    }
  }

  def handleRegularTurn(game: MonopolyGame): MonopolyGame = {
    val player = game.currentPlayer
    println(s"${player.name}'s regular turn")
    readLine("Press anything to roll a dice")
    val (dice1, dice2) = Dice().rollDice()
    val diceSum = dice1 + dice2
    println(s"You rolled $dice1 and $dice2 ($diceSum)")

    val newPosition = (player.position + diceSum - 1) % game.board.fields.size + 1
    println(s"Moving to position $newPosition")
    val updatedPlayer = player.copy(position = newPosition)
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
    val updatedGame = game.copy(players = updatedPlayers)
    handleFieldAction(updatedGame, newPosition)
  }
  def caseDiceJail(game: MonopolyGame):MonopolyGame = {
    val (dice1, dice2) = Dice().rollDice()
    val isDoubles = dice1 == dice2
    println(s"You rolled $dice1 and $dice2")

    if (isDoubles) {
      println("You rolled doubles! You're free!")
      val diceSum = dice1 + dice2

      val newPosition = (game.currentPlayer.position + diceSum) % game.board.fields.size
      val updatedPlayer = game.currentPlayer.copy(
        isInJail = false,
        jailTurns = 0,
        position = newPosition
      )

      val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
      val updatedGame = game.copy(players = updatedPlayers)
      handleFieldAction(updatedGame, newPosition)
    } else {
      val jailTurns = game.currentPlayer.jailTurns + 1

      if (jailTurns >= 3) {
        println("This was your third attempt. You must pay €50 to get out.")

        val updatedPlayer = if (game.currentPlayer.balance >= 50) {
          val (dice1, dice2) = Dice().rollDice()
          val diceSum = dice1 + dice2
          println(s"You rolled $dice1 and $dice2 ($diceSum)")

          val newPosition = (game.currentPlayer.position + diceSum) % game.board.fields.size
          game.currentPlayer.copy(
            isInJail = false,
            balance = game.currentPlayer.balance - 50,
            jailTurns = 0,
            position = newPosition
          )
        } else {
          println("You don't have enough money to pay €50. You must sell properties or declare bankruptcy.")
          game.currentPlayer.copy(jailTurns = jailTurns)
        }

        val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
        val updatedGame = game.copy(players = updatedPlayers)

        if (!updatedPlayer.isInJail) {
          handleFieldAction(updatedGame, updatedPlayer.position)
        } else {
          updatedGame
        }
      } else {
        println(s"You failed to roll doubles. This was attempt ${jailTurns}/3.")
        val updatedPlayer = game.currentPlayer.copy(jailTurns = jailTurns)
        val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
        game.copy(players = updatedPlayers)
      }
    }
  }
  def handleJailTurn(game: MonopolyGame): MonopolyGame = {
    println(s"${game.currentPlayer.name}, you are in jail!")
    println("Options to get out of jail:")
    println("1. Pay €50 to get out")
    println("2. Use a 'Get Out of Jail Free' card (if available)")
    println("3. Try to roll doubles")

    val choice = readLine("Enter your choice (1-3): ").trim

    choice match {
      case "1" =>
        if (game.currentPlayer.balance >= 50) {
          val updatedPlayer = game.currentPlayer.copy(
            isInJail = false,
            balance = game.currentPlayer.balance - 50,
            jailTurns = 0
          )

          val (dice1, dice2) = Dice().rollDice()
          val diceSum = dice1 + dice2
          println(s"You rolled $dice1 and $dice2 ($diceSum)")

          val newPosition = (game.currentPlayer.position + diceSum) % game.board.fields.size
          val playerAfterMove = updatedPlayer.copy(position = newPosition)
          val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), playerAfterMove)
          val updatedGame = game.copy(players = updatedPlayers)
          handleFieldAction(updatedGame, newPosition)
        } else {
          println("You don't have enough money to pay €50!")
          game
        }

      case "2" =>
        println("Not implimented yet")
        game
      case "3" => caseDiceJail(game)

      case _ => caseDiceJail(game)
    }
  }
def handleFieldAction(game: MonopolyGame, position: Int): MonopolyGame = {
  val field = game.board.fields.find(_.index == position).getOrElse(throw new Exception(s"Field at position $position not found"))
  val updatedGame = field match {
    case goToJail: GoToJailField => handleGoToJailField(game)
    case taxF: TaxField => handleTaxField(game, taxF.amount)
    case freeP: FreeParkingField => handleFreeParkingField(game, freeP)
    case _ => game
  }
  updatedGame
}
def handleGoToJailField(game: MonopolyGame): MonopolyGame = {
  val index = game.players.indexWhere(_.name == game.currentPlayer.name)
  if (index >= 0) {
    // Den Spieler in das Gefängnis schicken (Position = 11, isInJail = true)
    val updatedPlayer = game.currentPlayer.goToJail()
    val updatedPlayers = game.players.updated(index, updatedPlayer)
    printBoard(game)
    print("you landet on \"go to jail\", Press any key to continue the game")
    readLine()
    game.copy(players = updatedPlayers)
  } else {
    println(s"Fehler: Spieler ${game.currentPlayer.name} nicht gefunden!")
    game
  }
}
def handleTaxField(game: MonopolyGame, amount: Int): MonopolyGame = {
  print("PlayerOnTax")
  val playerIndex = game.players.indexWhere(_.name == game.currentPlayer.name)
  val freeParkingFieldIndex = game.board.fields.indexWhere(_.isInstanceOf[FreeParkingField])
  print(freeParkingFieldIndex + "is the parkingIndex")

  if (playerIndex >= 0 && freeParkingFieldIndex >= 0) {
    val updatedPlayer = if (game.currentPlayer.balance >= amount) {
      game.currentPlayer.copy(balance = game.currentPlayer.balance - amount)
    } else {
      // TODO: Implementiere Bankrott oder Verkauf von Eigentum
      game.currentPlayer
    }
    val updatedPlayers = game.players.updated(playerIndex, updatedPlayer)
    val freeParkingField = game.board.fields(freeParkingFieldIndex).asInstanceOf[FreeParkingField]
    print(freeParkingField)
    val updatedFreeParkingField = freeParkingField.copy(amount = freeParkingField.amount + amount)
    print(updatedFreeParkingField)
    val updatedFields = game.board.fields.updated(freeParkingFieldIndex, updatedFreeParkingField)
    val updatedBoard = game.board.copy(fields = updatedFields)
    print(updatedBoard)

    val returnVal = game.copy(players = updatedPlayers, board = updatedBoard)
    print(returnVal)
    returnVal

  } else {
    println(s"Fehler: Spieler ${game.currentPlayer.name} nicht gefunden oder 'Frei Parken'-Feld fehlt!")
    game
  }
}
def handleFreeParkingField(game: MonopolyGame, freeP: FreeParkingField): MonopolyGame = {
  println(s"You landed on Free Parking! Collecting €${freeP.amount}.")
  val playerIndex = game.players.indexWhere(_.name == game.currentPlayer.name)
  print("PlayersIndex" + playerIndex)
  if (playerIndex >= 0) {
    val collectedAmount = freeP.amount
    val updatedPlayer = game.currentPlayer.copy(balance = game.currentPlayer.balance + collectedAmount)
    val updatedPlayers = game.players.updated(playerIndex, updatedPlayer)
    val updatedField = freeP.copy(amount = 0)
    val updatedBoard = game.board.copy(fields = game.board.fields.updated(freeP.index, updatedField))
    game.copy(players = updatedPlayers, board = updatedBoard)
  } else {
    println(s"Fehler: Spieler ${game.currentPlayer.name} nicht gefunden!")
    game
  }
}

def randomEmoji(vektor: Vector[Player]): String = {
  val emojis = List(
    "🐶", "🐱", "🐯", "🦁", "🐻", "🐼", "🦊", "🐺", "🦄", "🐲", "🦉",
    "🦅", "🐝", "🦋", "🐙", "🦑", "🦈", "🐊", "🦖", "🦓", "🦒", "🐘",
    "🦔", "🐢", "🐸", "🦜", "👑", "🤖", "👽", "🧙", "🧛", "🧟", "👻",
    "🦸", "🧚", "🥷")
  val availableEmojis = emojis.filterNot(e => vektor.exists(_.name == e))
  Random.shuffle(availableEmojis).headOption.getOrElse("🐾")
}

  def defineGame(): MonopolyGame = {
    println("Wie viele Spieler? (2-4):")
    val playerAnz = readLine().toInt
    var playerVector = Vector[Player]()

    for (i <- 1 to playerAnz) {
      val playerName = randomEmoji(playerVector)
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
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        field.owner match {
          case Some(owner) if owner == player.name =>
            val houseCost = 50
            if (player.balance >= houseCost) {
              val updatedField = field.copy(
                house = PropertyField.House(field.house.amount + 1)
              )

              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              val updatedPlayer = player.copy(balance = player.balance - houseCost)

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
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        field.owner match {
          case None =>
            if (player.balance >= field.price) {
              val updatedField = field.copy(
                owner = Some(player.name)
              )

              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              val updatedPlayer = player.copy(balance = player.balance - field.price)

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