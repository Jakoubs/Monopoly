package de.htwg.controller

import de.htwg.Board
import de.htwg.model.IPlayer
import de.htwg.model.modelBaseImple.{BoardField, PropertyField, TrainStationField, UtilityField}
import de.htwg.controller.controllerBaseImpl.{Command, GameState, OpEnum, TurnInfo}

import scala.util.Try

trait IController {
  def state: GameState
  def currentPlayer: IPlayer
  def board: Board
  def players: Vector[IPlayer]
  def sound: Boolean

  def getTurnInfo: TurnInfo
  def updateTurnInfo(newInfo: TurnInfo): Unit

  def handleInput(input: OpEnum): Unit

  def updatePlayer(player: IPlayer): Unit
  def updatePlayers(player: Vector[IPlayer]): Unit

  def setBoard(board: Board): Unit
  def updateBoardAndPlayer(field: BoardField, player: IPlayer): Unit
  def switchToNextPlayer(): Unit

  def executeCommand(cmd: Command): Unit
  def undo(): Unit
  def redo(): Unit

  def isGameOver: Boolean

  def saveSlot(slotName: String): Try[Unit]
  def loadSlot(slotName: String): Try[Unit]
  def availableSlots: Vector[String]

  def getBoardString: String
  def getInventory: String
  def getCurrentPlayerStatus: String

  def getOwnedProperties(): Map[IPlayer, List[PropertyField]]
  def getOwnedTrainStations(): Map[IPlayer, Int]
  def getOwnedUtilities(): Map[IPlayer, Int]
  def setState(newState: GameState): Unit
}