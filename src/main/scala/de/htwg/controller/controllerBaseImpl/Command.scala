package de.htwg.controller
  import de.htwg.model.IMonopolyGame
  import de.htwg.model.modelBaseImple.{BuyableField, PropertyField}
  import scala.util.{Failure, Success, Try}
  import de.htwg.model.IPlayer
  
  // Funktionales Command Interface
  trait Command {
    def execute(game: IMonopolyGame): IMonopolyGame
  }
  
  // Buy Command - funktional
  case class BuyCommand[T <: BuyableField](
                                            field: T,
                                            player: IPlayer
                                          ) extends Command {
  
    def execute(game: IMonopolyGame): IMonopolyGame = {
      val (updatedField, updatedPlayer) = field.buy(player.asInstanceOf[de.htwg.model.modelBaseImple.Player])
      game.withUpdatedBoardAndPlayer(updatedField, updatedPlayer)
    }
  }
  
  // Buy House Command - saubere funktionale Version
  case class BuyHouseCommand(
                              field: PropertyField,
                              player: IPlayer
                            ) extends Command {
  
    def execute(game: IMonopolyGame): IMonopolyGame = {
      game.buyHouse(field, player.asInstanceOf[de.htwg.model.modelBaseImple.Player]) match {
        case Success(updatedGame) => updatedGame
        case Failure(_) => game // Unverändert bei Fehler
      }
    }
  }
  
  // Roll Dice Command - mit Würfelwerten als Parameter
  case class RollDiceCommand(sound: Boolean) extends Command {
    def execute(game: IMonopolyGame): IMonopolyGame = {
      val (dice1, dice2) = game.rollDice(true)
      game.rollDice(dice1, dice2)
    }
  }
  
  // Pay Jail Fee Command - funktional
  case class PayJailFeeCommand(player: IPlayer) extends Command {
    def execute(game: IMonopolyGame): IMonopolyGame = {
      val updatedPlayer = player.asInstanceOf[de.htwg.model.modelBaseImple.Player].copy(
        isInJail = false,
        balance = player.balance - 50
      )
      game.withUpdatedPlayer(updatedPlayer)
    }
  }