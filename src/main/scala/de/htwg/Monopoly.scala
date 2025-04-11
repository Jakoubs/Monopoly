package de.htwg.model

import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}

import scala.io.StdIn.readLine

case class Board(fields: Vector[BoardField])

object Monopoly:
  def main(args: Array[String]): Unit = {
    val game = defineGame()
    while (game.players.size > 1) {
      println(s"${game.currentPlayer.name}'s turn")
      val updatedPlayer = playerTurn(game.currentPlayer)
      println("Turn finished. Proceeding to next player.")
      val updatedGame = game.copy(currentPlayer = game.players((game.players.indexOf(game.currentPlayer) + 1) % game.players.size))
      println(s"${updatedGame.currentPlayer} would be the next player.")
      println(s"${updatedPlayer.position} would be the position of p1.")
    }
  }

  def playerTurn(player: Player): Player = {
    val dice = new Dice()
    val updatedPlayer = player.playerMove(() => dice.rollDice(2,2), 1)
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
      playerVector = playerVector.appended(Player(playerName, 1500))
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
        PropertyField("brown2",2,100,10,None,color = Brown,PropertyField.Mortgage(10,false),PropertyField.House(0)),
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
        ChanceField(35),
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
case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player
                       )