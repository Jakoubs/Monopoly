package de.htwg.model

import de.htwg.model.PropertyField.Color.{Brown, LightBlue, Pink, Red}

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
        CommunityChestField(3), //ListofCards
        PropertyField("brown2",2,100,10,None,color = Brown,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        TaxField(100,5),
        TrainStationField("Marklylebone Station",6,None),
        PropertyField("lightBlue1",7,100,10,None,color = LightBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        ChanceField(8),
        PropertyField("lightBlue2",9,100,10,None,color = LightBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("lightBlue2",10,100,10,None,color = LightBlue,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        JailField,
        PropertyField("Pink1",12,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("Pink1",14,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(0)),
        PropertyField("Pink1",15,100,10,None,color = Pink,PropertyField.Mortgage(10,false),PropertyField.House(0)),




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