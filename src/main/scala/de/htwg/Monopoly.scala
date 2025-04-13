package de.htwg.model

import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}

import scala.io.StdIn.readLine

case class Board(fields: Vector[BoardField])

object Monopoly:
  def main(args: Array[String]): Unit = {
    var game = defineGame()
    printBoard(game)
    while (game.players.size > 1) {
      println(s"${game.currentPlayer.name}'s turn")
      val updatedPlayer = playerTurn(game.currentPlayer)

      val updatedPlayers = game.players.map(p =>
        if (p.name == updatedPlayer.name) updatedPlayer else p
      )

      val nextPlayer = updatedPlayers((updatedPlayers.indexOf(updatedPlayer) + 1) % updatedPlayers.size)

      game = game.copy(players = updatedPlayers, currentPlayer = nextPlayer)

      printBoard(game)
      println(s"${game.currentPlayer.name} would be the next player.")
      println(s"${updatedPlayer.position} is the new position of ${updatedPlayer.name}.")
    }
  }


  def playerTurn(player: Player): Player = {
    val dice = new Dice()
    val updatedPlayer = player.playerMove(() => dice.rollDice(), 1)
    updatedPlayer
  }

  def defineGame(): MonopolyGame = {
    println("Spieler eingeben (tippe 'ready' um fertig zu sein):")

    var playerVector = Vector[Player]()
    var playerName = ""

    while {
      playerName = readLine()
      playerName != "ready"
    } do {
      playerVector = playerVector.appended(Player(playerName, 1500, 1))
      println(s"Spieler $playerName hinzugefügt. Nächster Spieler (oder 'ready'):")
    }

    if (playerVector.isEmpty) {
      println("Keine Spieler eingegeben. Spiel wird beendet.")
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
    val game = MonopolyGame(playerVector, board, playerVector.head)
    println(s"Spiel gestartet mit ${playerVector.size} Spielern.")
    game
  }
  def printBoard(game: MonopolyGame): Unit = {
    printTop(game)
    printSides(game)
    printBottum(game)
  }
  def printTop(game: MonopolyGame): Unit = {
    val a = 0
    val line1 = "+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+"
    var line2 = "|  ______  _____  |"
    var line3 = "| |  ____ |     | |"
    var line4 = "| |_____| |_____| |"
    var line5 = "|          Ss.    |--------+--------+--------+--------+--------+--------+--------+--------+--------+          |      |"
    var line6 = "|  ssssssssSSSS   |                                                                                |          |      |"
    var line7 = "|          ;:`    |                                                                                |          |      |"
    var line8 = "|" + fillSpace(playersOnIndex(1, game,false), 17) + "|                                                                                |"+fillSpace(playersOnIndex(11, game,true), 10)+"|"+fillSpace(playersOnIndex(11, game,false), 6)+"|"
    var line9 = "+--------+--------+                                                                                +--------+-+------+"
    for (field <- game.board.fields) {
      if (field.index > 1 && field.index < 11) {
        var extra = getExtra(field)
        var name = field.name
        var idx = field.index
        line2 = line2 + fillSpace(("Nr" + idx.toString + extra), 8) + "|"
        val priceStr = getPrice(field)
        line3 = line3 + fillSpace(priceStr, 8) + '|'
        line4 = line4 + fillSpace(playersOnIndex(field.index, game,false), 8) + '|'
      }
    }
    line2 = line2 + "                 |"
    line3 = line3 + "__________       |"
    line4 = line4 + "  JAIL    |      |"
    val lines = Vector(line1, line2, line3, line4, line5, line6, line7, line8, line9)
    lines.foreach(println)
  }
  def printSides(game: MonopolyGame): Unit = {
    val a=0
    for(a <- 12 to 20){
      var fieldA = game.board.fields.find(_.index == 52-a).get
      var fieldB = game.board.fields.find(_.index == a).get
      var topLine ='|' + fillSpace(fillSpace(fieldA.index.toString + getExtra(fieldA),8) + '|', 107)+'|'+ fillSpace(fieldB.index.toString + getExtra(fieldB),8) + '|'
      var priceLine = '|' + fillSpace(fillSpace(getPrice(fieldA),8) + '|', 107)+'|'+ fillSpace(getPrice(fieldB),8) + '|'
      var playerLine = '|' + fillSpace(fillSpace(playersOnIndex(52-a, game,false),8) + '|', 107)+'|'+ fillSpace(playersOnIndex(a, game,false),8) + '|'
      var bottomLine = ""
      if(a!=20){
        bottomLine = "+--------+                                                                                                  +--------+"
      } else {
        bottomLine = "+--------+--------+                                                                                +--------+--------+"
      }
      println(topLine)
      println(priceLine)
      println(playerLine)
      println(bottomLine)
    }
  }
  def printBottum(game: MonopolyGame): Unit = {
    val line1 = "|   GO TO JAIL    |                                                                                |  FREE PARIKING  |"
    var line2 = "|     ---->       |                                                                                |   ______        |"
    var line4 = "|                 |                                                                                |  /|_||_`.__     |"
    var line5 = "|                 +--------+--------+--------+--------+--------+--------+--------+--------+--------+ (   _    _ _\\   |"
    var line6 = "|                 |"
    var line7 = "|                 |"
    var line8 = "|"+fillSpace(playersOnIndex(31,game,false),17)+"|"
    var line9 = "+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+"
    val a = 0
    for (a <- 22 to 30) {
      var field = game.board.fields.find(_.index == 52-a).get
      line6 = line6 + fillSpace(field.index.toString + getExtra(field), 8) + '|'
      line7 = line7 + fillSpace(getPrice(field), 8) + '|'
      line8 = line8 + fillSpace(playersOnIndex(field.index, game,false), 8) + '|'
    }
    line6 = line6 + "                 |"
    line7 = line7 + "                 |"
    line8 = line8 + fillSpace(playersOnIndex(21, game,false), 17) + '|'
    val lines = Vector(line1, line2, line4, line5, line6, line7, line8, line9)
    lines.foreach(println)
  }
  def fillSpace(input: String, maxChar: Int): String = {
    input.padTo(maxChar, ' ')
  }
  def getPrice(field: BoardField): String = {
    field match
      case pf: PropertyField => pf.price.toString + '$'
      case tf: TrainStationField => "200$"
      case _ => ""
  }
  def getExtra(field: BoardField): String = {
    field match
      case pf: PropertyField =>
        if (pf.owner.isEmpty)
          ""
        else
          (pf.owner.toString + " [" + pf.house.amount.toString + ']')
      case cf: ChanceField => ("")
      case cc: CommunityChestField => ("")
      case tf: TaxField => ("")
      case ts: TrainStationField =>
        if (ts.owner.isEmpty)
          ""
        else
          (' ' + ts.owner.toString)
      case uf: UtilityField =>
        if (uf.owner.isEmpty)
          ""
        else
        (' ' + uf.owner.toString)
      case _ => ""
  }
  def playersOnIndex(idx: Int, game: MonopolyGame, inJail: Boolean): String = {
    var playerString =""
    for(p<- game.players){
      if(p.position == idx && p.isInJail == inJail) {
        playerString = playerString + p.name + " "
      }
    }
    playerString
  }
case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player
                       )