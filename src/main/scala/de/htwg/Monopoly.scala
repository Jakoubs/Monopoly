package de.htwg.model
import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.PropertyField
import de.htwg.model.SoundPlayer
import de.htwg.model.PropertyField.calculateRent
import scala.io.StdIn.readLine
import scala.util.Random

case class Board(fields: Vector[BoardField])

object Monopoly:
  def main(args: Array[String]): Unit = {
    var game = defineGame()
    printBoard(game)
    while (game.players.size > 1) {
      println(s"${game.currentPlayer.name}'s turn    |    " + getInventory(game))
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
    readLine("Press ENTER to roll a dice")
    val (dice1, dice2) = Dice().rollDice(game.sound)
    val diceSum = dice1 + dice2
    println(s"You rolled $dice1 and $dice2 ($diceSum)")

    val newPosition = (player.position + diceSum - 1) % game.board.fields.size + 1
    println(s"Moving to position $newPosition")
    val updatedPlayer = player.copy(position = newPosition)
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), updatedPlayer)
    val updatedGame = game.copy(players = updatedPlayers, currentPlayer = updatedPlayer)
    val gameRolled =  handleFieldAction(updatedGame, newPosition)
    val finalGame = handleOptionalActions(gameRolled)
    finalGame
  }

  def handleOptionalActions(currentGame: MonopolyGame): MonopolyGame = {
    println("Do you want to do anything else? |1. Buy House|2.Trade|3.Mortage| => (1/2/3/x)")
    val input = readLine()
    input match {
      case "1" =>
        val fieldIndex = readLine("Enter the index:").toInt
        val updatedGame = buyHouse(currentGame, fieldIndex, currentGame.currentPlayer)
        handleOptionalActions(updatedGame)
      case "2" =>
        println("Trade (Not implemented)")
        handleOptionalActions(currentGame)
      case "3" =>
        println("Mortgage (Not implemented)")
        handleOptionalActions(currentGame)
      case "x" => currentGame
      case _ =>
        println("Not valid! Try again")
        handleOptionalActions(currentGame)
    }
  }


  def caseDiceJail(game: MonopolyGame):MonopolyGame = {
    val (dice1, dice2) = Dice().rollDice(game.sound)
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
          val (dice1, dice2) = Dice().rollDice(game.sound)
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

          val (dice1, dice2) = Dice().rollDice(game.sound)
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
    case pf: PropertyField => handlePropertyField(game, pf)
    //case tf: TrainStationField => handlePropertyField(game, tf)
    case _ => game
  }
  updatedGame
}
def handlePropertyField(game: MonopolyGame, property: PropertyField): MonopolyGame = {
  property.owner match {
    case None =>
      println(s"Property (${property.name}) is available for ${property.price}$$")
      println("Buy? (y/n)")
      val response = readLine().trim.toLowerCase
      if (response == "y") {
        val (updatedGame) = buyProperty(game, property.index, game.currentPlayer)
        if(game.sound) {
          SoundPlayer().playAndWait("src/main/resources/Money.wav")
        }
        updatedGame
      } else {
        game
      }
    case Some(ownerName) if ownerName.name != game.currentPlayer.name =>
      val rent = calculateRent(property)
      println(s"Pay ${rent}$$ rent to ${ownerName.name}")
      val playerIndex = game.players.indexWhere(_.name == game.currentPlayer.name)
      val updatedPlayer = game.currentPlayer.copy(balance = game.currentPlayer.balance - rent, position = property.index)

      val ownerIndex = game.players.indexWhere(_.name == ownerName.name)
      val owner = game.players(ownerIndex)
      val updatedOwner = owner.copy(balance = owner.balance + rent)

      // Aktualisiere die Spielerliste
      val updatedPlayers = game.players
        .updated(playerIndex, updatedPlayer)
        .updated(ownerIndex, updatedOwner)

      game.copy(players = updatedPlayers)
    case Some(_) =>
      println("Tis property is owned by you.")
      game
  }
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
def updateFreeParkingAmount(board: Board, amount: Int): Board = {
  val freeParkingFieldIndex = board.fields.indexWhere(_.isInstanceOf[FreeParkingField])
  if (freeParkingFieldIndex >= 0) {
    val freeParkingField = board.fields(freeParkingFieldIndex).asInstanceOf[FreeParkingField]
    val updatedFreeParkingField = freeParkingField.copy(amount = freeParkingField.amount + amount)
    board.copy(fields = board.fields.updated(freeParkingFieldIndex, updatedFreeParkingField))
  } else {
    println("Fehler: 'Frei Parken'-Feld nicht gefunden!")
    board
  }
}

def handleTaxField(game: MonopolyGame, amount: Int): MonopolyGame = {
  val playerIndex = game.players.indexWhere(_.name == game.currentPlayer.name)

  if (playerIndex >= 0) {
    val updatedPlayer = if (game.currentPlayer.balance >= amount) {
      game.currentPlayer.copy(balance = game.currentPlayer.balance - amount)
    } else {
      // TODO: Implementiere Bankrott oder Verkauf von Eigentum
      game.currentPlayer
    }
    val updatedPlayers = game.players.updated(playerIndex, updatedPlayer)
    val updatedBoard = updateFreeParkingAmount(game.board, amount)
    game.copy(players = updatedPlayers, board = updatedBoard)
  } else {
    println(s"Fehler: Spieler ${game.currentPlayer.name} nicht gefunden!")
    game
  }
}

def handleFreeParkingField(game: MonopolyGame, freeP: FreeParkingField): MonopolyGame = {
  println(s"You landed on Free Parking! Collecting €${freeP.amount}.")
  val playerIndex = game.players.indexWhere(_.name == game.currentPlayer.name)
  if (playerIndex >= 0) {
    val collectedAmount = freeP.amount
    val updatedPlayer = game.currentPlayer.copy(balance = game.currentPlayer.balance + collectedAmount)
    val updatedPlayers = game.players.updated(playerIndex, updatedPlayer)
    val updatedBoard = game.board.copy(fields = game.board.fields.updated(freeP.index-1, freeP.copy(amount = 0)))
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
    println("play with sound? (y/n)")
    val soundInput = readLine()
    val isTestBoard = soundInput == "yT" || soundInput == "nT"
    val soundBool = soundInput == "y" || soundInput == "yT"
    
    if(soundBool){
    SoundPlayer().playBackground("src/main/resources/MonopolyJazz.wav")
    }
    //if(!isTestBoard) {

    var playerVector = Vector[Player]()

    def askForPlayerCount(): Int = {
      println("How many Player? (2-4):")
      val input = scala.io.StdIn.readLine()
      try {
        val playerCount = input.toInt
        if (playerCount >= 2 && playerCount <= 4) {
          playerCount
        } else {
          println("Invalid player count. Please enter a number between 2 and 4.")
          askForPlayerCount()
        }
      } catch {
        case _: NumberFormatException =>
          println("Invalid input. Please enter a number.")
          askForPlayerCount()
      }
    }

    val playerAnz = askForPlayerCount()

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
          PropertyField("Pink2",14,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(0)),
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
      val game = MonopolyGame(playerVector, board, playerVector.head, soundBool)
      println(s"Spiel gestartet mit ${playerVector.size} Spielern.")
      game
    //} else {}
  }

  def printBoard(game: MonopolyGame): String = {
    var AllLines = printTop(game) + printSides(game) + printBottom(game)
    print(AllLines)
    AllLines
  }

  def printTop(game: MonopolyGame): String = {
    val fieldNames = game.board.fields.slice(0, 4)

      val fieldData1 = formatField(fieldNames.lift(0))
      val fieldData2 = formatField(fieldNames.lift(1))
      val fieldData3 = formatField(fieldNames.lift(2))
      val fieldData4 = formatField(fieldNames.lift(3))

    
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
      line + fillSpace(playersOnIndex(field.index, game, false), 8) + '|') + "  JAIL    |      |" + " " * 18 + "ALL FIELDS:"

    val additionalLines = List(
      "|          Ss.    |--------+--------+--------+--------+--------+--------+--------+--------+--------+          |      |" + " " * 20 + "Index: 2, GoField",
      "|  ssssssssSSSS   |  " + fillSpace(stats1, 76) + "  |          |      |" + " " * 20 + fieldData1,
      "|          ;:`    |  " + fillSpace(stats2, 76) + "  |          |      |" + " " * 20 + fieldData2,
      "|" + fillSpace(playersOnIndex(1, game, false), 17) + "|  " + fillSpace(stats3, 76) + "  |" + fillSpace(playersOnIndex(11, game, true), 10) + "|" + fillSpace(playersOnIndex(11, game, false), 6) + "|" + " " * 20 + fieldData3,
      "+--------+--------+  " + fillSpace(stats4, 76) + "  +--------+-+------+" + " " * 20 + fieldData4 + "\n"
    )
    List(line1, line2, line3, line4) ++ additionalLines mkString "\n"  
  }

def printFieldsData(game: MonopolyGame, x: Int): (String, String, String, String) = {
  val batchIndex = x - 12
  val startIdx = batchIndex * 4 + 5
  
  val fieldNames = game.board.fields.slice(startIdx, startIdx + 4)

  (
    formatField(fieldNames.lift(0)),
    formatField(fieldNames.lift(1)),
    formatField(fieldNames.lift(2)),
    formatField(fieldNames.lift(3))
  )
}

def formatField(optField: Option[BoardField]): String = {
  optField match {
    case Some(field) =>
      s"Index: ${field.index}, ${field.name}, Preis: ${getPrice(field)}"
    case None =>
      ""
  }
}



  def printSides(game: MonopolyGame): String = {
    val sideRows = (12 to 20).map { a =>
      val fieldA = game.board.fields.find(_.index == 52 - a).get
      val fieldB = game.board.fields.find(_.index == a).get

      val (fieldData1, fieldData2, fieldData3, fieldData4) = printFieldsData(game, a)

      val line1 = '|' + fillSpace(fillSpace(fieldA.index.toString + getExtra(fieldA), 8) + '|', 107) + '|' + fillSpace(fieldB.index.toString + getExtra(fieldB), 8) + '|' + " " * 20 + fieldData1
      val line2 = '|' + fillSpace(fillSpace(getPrice(fieldA), 8) + '|', 107) + '|' + fillSpace(getPrice(fieldB), 8) + '|' + " " * 20 + fieldData2
      val line3 = '|' + fillSpace(fillSpace(playersOnIndex(52 - a, game, false), 8) + '|', 107) + '|' + fillSpace(playersOnIndex(a, game, false), 8) + '|' + " " * 20 + fieldData3
      val line4 = if (a != 20) "+--------+                                                                                                  +--------+" + " " * 20 + fieldData4
                  else "+--------+--------+                                                                                +--------+--------+\n"

      List(line1, line2, line3, line4).mkString("\n")
    }
    sideRows.mkString("\n")
  }

def printBottom(game: MonopolyGame): String = {
  val fixedLines = List(
    "|   GO TO JAIL    |                                                                                |  FREE PARIKING  |",
    "|     ---->       |                                                                                |   ______        |",
    "|                 |                                                                                |  /|_||_`.__     |",
    "|                 +--------+--------+--------+--------+--------+--------+--------+--------+--------+ (   _    _ _\\   |"
  )

  val fields22To30Options = (22 to 30).map(a => game.board.fields.find(_.index == 52 - a))

  val line6 = fields22To30Options.foldLeft("|                 |")((line, fieldOption) =>
    fieldOption match {
      case Some(field) => line + fillSpace(field.index.toString + getExtra(field), 8) + '|'
      case None => line + fillSpace("N/A", 8) + '|'
    }) + " =`-(_)--(_)-`   |"

  val freeParkingMoney = game.board.fields.find(_.index == 21) match {
    case Some(field: FreeParkingField) => getPrice(field)
    case _ => "N/A"
  }
  val line7 = fields22To30Options.foldLeft("|                 |")((line, fieldOption) =>
    fieldOption match {
      case Some(field) => line + fillSpace(getPrice(field), 8) + '|'
      case None => line + fillSpace("N/A", 8) + '|'
    }) + s"   Money [$freeParkingMoney]    |"

  val line8 = fields22To30Options.foldLeft("|" + fillSpace(playersOnIndex(31, game, false), 17) + "|")((line, fieldOption) =>
    fieldOption match {
      case Some(field) => line + fillSpace(playersOnIndex(field.index, game, false), 8) + '|'
      case None => line + fillSpace(" ", 8) + '|'
    }) + fillSpace(playersOnIndex(21, game, false), 17) + '|'

  val line9 = "+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+\n"
  
  fixedLines ++ List(line6, line7, line8, line9) mkString "\n"  
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
  def getName(field: BoardField): String = {
    field match {
      case pf: PropertyField => pf.name.toString
      case tf: TrainStationField => tf.name
      case fp: FreeParkingField => "FreeParking"
      case cf: CommunityChestField => "CommunityChest"
      case _ => ""
    }
  }

def getExtra(field: BoardField): String = {
  field match {
    case pf: PropertyField =>
      pf.owner match {
        case Some(ownerName) => s" ${ownerName.name}${pf.house.amount}"
        case None => ""
      }
    case ts: TrainStationField =>
      ts.owner match {
        case Some(ownerName) => s" ${ownerName.name}"
        case None => ""
      }
    case uf: UtilityField =>
      uf.owner match {
        case Some(ownerName) => s" ${ownerName.name}"
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
  val header = s"INVENTORY Player: ${game.currentPlayer.name}| "
  val inventoryItems = StringBuilder(header)

  for (field <- game.board.fields) {
    field match {
      case pf: PropertyField if pf.owner.contains(game.currentPlayer) =>
        inventoryItems.append(s"idx:${pf.index}[${pf.house.amount}], ")
      case ts: TrainStationField if ts.owner.contains(game.currentPlayer) =>
        inventoryItems.append(s"idx:${ts.index}, ")
      case uf: UtilityField if uf.owner.contains(game.currentPlayer) =>
        inventoryItems.append(s"idx:${uf.index}, ")
      case _ => // Tue nichts für Felder, die nicht dem aktuellen Spieler gehören
    }
  }

  if (inventoryItems.length > header.length) {
    inventoryItems.delete(inventoryItems.length - 2, inventoryItems.length)
  }

  inventoryItems.toString
}


  def buyHouse(game: MonopolyGame, propertyIndex: Int, player: Player): (MonopolyGame) = {
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        field.owner match {
          case Some(owner) if owner.name == player.name =>

            val colorProperties = game.board.fields.collect {
              case pf: PropertyField if pf.color == field.color => pf
            }

            val playerStreets = colorProperties.forall(_.owner.contains(player.name))

            if (!playerStreets) {
              println(s"${player.name} besitzt nicht alle Straßen der Farbe ${field.color}.")
              return game
            }

            val houseCost = 50
            if (player.balance >= houseCost) {
              val updatedField = field.copy(
                house = PropertyField.House(field.house.amount + 1)
              )

              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)
              val updatedPlayer = player.copy(
                balance = player.balance - houseCost,
                position = game.currentPlayer.position
              )
              val updatedPlayers = game.players.map(p =>
                if (p.name == updatedPlayer.name) updatedPlayer else p
            )    
              val updatedGame = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = updatedPlayer)
              println(s"${player.name} hat ein Haus auf ${field.name} gebaut.")
              (updatedGame)
            } else {
              println(s"Nicht genug Geld! Ein Haus kostet $houseCost, aber ${player.name} hat nur ${player.balance}.")
              (game)
            }
          case _ =>
            println(s"${player.name} ist nicht der Eigentümer dieser Immobilie.")
            (game)
        }
      case Some(_) =>
        println(s"Auf dem Feld mit Index $propertyIndex kann kein Haus gebaut werden.")
        (game)
      case None =>
        println(s"Feld mit Index $propertyIndex nicht gefunden.")
        (game)
    }
  }

  def buyProperty(game: MonopolyGame, propertyIndex: Int, player: Player): (MonopolyGame) = {
    val fieldOption = game.board.fields.find(_.index == propertyIndex)

    fieldOption match {
      case Some(field: PropertyField) =>
        field.owner match {
          case None =>
            if (player.balance >= field.price) {
              val updatedField = field.copy(
                owner = Some(player)
              )

              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)
              val updatedPlayer = player.copy(balance = player.balance - field.price, position = propertyIndex)
              val updatedPlayers = game.players.map(p =>
              if (p.name == updatedPlayer.name) updatedPlayer else p
              )
              val updatedGame = game.copy(board = updatedBoard, players = updatedPlayers)
              println(s"${player.name} hat die Immobilie ${field.name} für ${field.price} gekauft.")
              (updatedGame)
            } else {
              println(s"Nicht genug Geld! Die Immobilie kostet ${field.price}, aber ${player.name} hat nur ${player.balance}.")
              (game)
            }
          case Some(owner) =>
            println(s"Diese Immobilie gehört bereits ${owner.name}.")
            (game)
        }
      case Some(field: TrainStationField) =>
        field.owner match {
          case None =>
            val stationPrice = 200 // Typischer Preis für Bahnhöfe
            if (player.balance >= stationPrice) {
              val updatedField = field.copy(owner = Some(player))
              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              val updatedPlayer = player.copy(balance = player.balance - stationPrice, position = propertyIndex)
              val updatedPlayers = game.players.map(p =>
              if (p.name == updatedPlayer.name) updatedPlayer else p
              )
              val updatedGame = game.copy(board = updatedBoard, players = updatedPlayers)
              println(s"${player.name} hat den Bahnhof ${field.name} für $stationPrice gekauft.")
              (updatedGame)
            } else {
              println(s"Nicht genug Geld! Der Bahnhof kostet $stationPrice, aber ${player.name} hat nur ${player.balance}.")
              (game)
            }
          case Some(owner) =>
            println(s"Dieser Bahnhof gehört bereits ${owner.name}.")
            (game)
        }
      case Some(field: UtilityField) =>
        field.owner match {
          case None =>
            val utilityPrice = 150 // Typischer Preis für Versorgungswerke
            if (player.balance >= utilityPrice) {
              val updatedField = field.copy(
                owner = Some(player)
              )

              val updatedFields = game.board.fields.map { f =>
                if (f.index == propertyIndex) updatedField else f
              }
              val updatedBoard = game.board.copy(fields = updatedFields)

              val updatedPlayer = player.copy(balance = player.balance - utilityPrice, position = propertyIndex)
              val updatedPlayers = game.players.map(p =>
              if (p.name == updatedPlayer.name) updatedPlayer else p
              )
              val updatedGame = game.copy(board = updatedBoard, players = updatedPlayers)
              println(s"${player.name} hat das Versorgungswerk ${field.name} für $utilityPrice gekauft.")
              (updatedGame)
            } else {
              println(s"Nicht genug Geld! Das Versorgungswerk kostet $utilityPrice, aber ${player.name} hat nur ${player.balance}.")
              (game)
            }
          case Some(owner) =>
            println(s"Dieses Versorgungswerk gehört bereits ${owner.name}.")
            (game)
        }
      case Some(_) =>
        println(s"Das Feld mit Index $propertyIndex kann nicht gekauft werden.")
        (game)
      case None =>
        println(s"Feld mit Index $propertyIndex nicht gefunden.")
        (game)
    }
  }

case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player,
                         sound: Boolean
                       )