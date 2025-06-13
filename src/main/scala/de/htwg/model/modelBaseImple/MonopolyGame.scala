package de.htwg.model.modelBaseImple
      
      import de.htwg.Board
      import de.htwg.model.{IMonopolyGame, IPlayer}
      import de.htwg.model.modelBaseImple.Player
      import de.htwg.model.IMonopolyGame
      
      import scala.util.Try
      
      case class MonopolyGame(
        players: Vector[Player],
        board: Board,
        currentPlayer: Player,
        sound: Boolean,
        override val state: GameState
      ) extends IMonopolyGame {
      
        def createGame: IMonopolyGame = {
          MonopolyGame(
            players = players,
            board = board,
            currentPlayer = players.head,
            sound = sound,
            state = StartTurnState()
          )
        }
      
        override def withUpdatedPlayer(newPlayer: Player): IMonopolyGame = {
          val ps = players.updated(players.indexOf(currentPlayer), newPlayer)
          this.copy(players = ps, currentPlayer = newPlayer)
        }
      
        override def withUpdatedBoardAndPlayer(field: BoardField, player: Player): IMonopolyGame = {
          val updatedFields = board.fields.updated(field.index - 1, field)
          val b = board.copy(fields = updatedFields)
          val ps = players.updated(players.indexOf(currentPlayer), player)
          this.copy(board = b, players = ps, currentPlayer = player)
        }
      
        override def withNextPlayer: IMonopolyGame = {
          val idx = players.indexOf(currentPlayer)
          val next = players((idx + 1) % players.size)
          this.copy(currentPlayer = next)
        }
      
        override def buyHouse(field: PropertyField, player: Player): Try[IMonopolyGame] = {
          PropertyField.House().buyHouse(player, field, this).map {
            case (updatedField, updatedPlayer) =>
              this.withUpdatedBoardAndPlayer(updatedField, updatedPlayer)
          }
        }
      
        override def rollDice(valid: Boolean): (Int, Int) = {
          Dice().rollDice(sound)
        }
      
        override def endTurn(): IMonopolyGame = {
          this.withNextPlayer
        }
      
        override def handle(input: de.htwg.controller.OpEnum, controller: de.htwg.controller.Controller): IMonopolyGame = {
          this.copy(state = state.handle(input, controller))
        }
      
        override def movePlayer(steps: Int): IMonopolyGame = {
          val updatedPlayer = currentPlayer.moveToIndex((currentPlayer.position + steps) % 40)
          this.withUpdatedPlayer(updatedPlayer)
        }
      
        override def rollDice(dice1: Int, dice2: Int): IMonopolyGame = {
          val updatedPlayer = currentPlayer.moveToIndex((currentPlayer.position + dice1 + dice2) % 40)
          this.withUpdatedPlayer(updatedPlayer)
        }
      
        override def toggleSound(): IMonopolyGame = {
          this.copy(sound = !sound)
        }
      }