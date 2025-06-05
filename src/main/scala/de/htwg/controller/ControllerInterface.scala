package de.htwg.controller

import de.htwg.model.{Player, PropertyField, TrainStationField, UtilityField, BoardField, Dice}
import de.htwg.Board
import de.htwg.MonopolyGame
import de.htwg.controller.GameState

trait ControllerInterface {

  def game: MonopolyGame
  def dice: Dice
  def state: GameState
  def currentPlayer: Player
  def board: Board
  def players: Vector[Player]
  def sound: Boolean

  def getTurnInfo: TurnInfo
  def updateTurnInfo(newInfo: TurnInfo): Unit

  def handleInput(input: OpEnum): Unit

  def updatePlayer(player: Player): Unit
  def updateBoardAndPlayer(field: BoardField, player: Player): Unit
  def switchToNextPlayer(): Unit

  def executeCommand(cmd: Command): Unit
  def undo(): Unit
  def redo(): Unit

  def isGameOver: Boolean

  def getBoardString: String
  def getInventory: String
  def getCurrentPlayerStatus: String

  def getOwnedProperties(): Map[Player, List[PropertyField]]
  def getOwnedTrainStations(): Map[Player, Int]
  def getOwnedUtilities(): Map[Player, Int]
}