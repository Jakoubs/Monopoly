package de.htwg.view

import de.htwg.MonopolyGame
import de.htwg.model.{BoardField, CommunityChestField, FreeParkingField, PropertyField, TrainStationField, UtilityField}

object BoardPrinter {
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

  def printSides(game: MonopolyGame): String = {
    val sideRows = (12 to 20).map { a =>
      val fieldAOpt = game.board.fields.find(_.index == 52 - a)
      val fieldBOpt = game.board.fields.find(_.index == a)

      (fieldAOpt, fieldBOpt) match {
        case (Some(fieldA), Some(fieldB)) =>
          val (fieldData1, fieldData2, fieldData3, fieldData4) = printFieldsData (game, a)
          val line1 = '|' + fillSpace(fillSpace(fieldA.index.toString + getExtra(fieldA), 8) + '|', 107) + '|' + fillSpace(fieldB.index.toString + getExtra(fieldB), 8) + '|' + " " * 20 + fieldData1
          val line2 = '|' + fillSpace(fillSpace(getPrice(fieldA), 8) + '|', 107) + '|' + fillSpace(getPrice(fieldB), 8) + '|' + " " * 20 + fieldData2
          val line3 = '|' + fillSpace(fillSpace(playersOnIndex(52 - a, game, false), 8) + '|', 107) + '|' + fillSpace(playersOnIndex(a, game, false), 8) + '|' + " " * 20 + fieldData3
          val line4 = if (a != 20) "+--------+                                                                                                  +--------+" + " " * 20 + fieldData4
          else "+--------+--------+                                                                                +--------+--------+\n"

          List(line1, line2, line3, line4).mkString("\n")
        case _ =>
          println(s"Fehler: Feld mit Index ${52 - a} oder $a nicht gefunden.")
      }
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
        case Some(field) => line +  fillSpace(getPrice(field), 8) + '|'
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

  // alle deine Methoden wie formatField, fillSpace usw. hierher verschieben!
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
        case pf: PropertyField if pf.owner.contains(game.currentPlayer.name) =>
          inventoryItems.append(s"idx:${pf.index}[${pf.house.amount}], ")
        case ts: TrainStationField if ts.owner.contains(game.currentPlayer.name) =>
          inventoryItems.append(s"idx:${ts.index}, ")
        case uf: UtilityField if uf.owner.contains(game.currentPlayer.name) =>
          inventoryItems.append(s"idx:${uf.index}, ")
        case _ => // Tue nichts für Felder, die nicht dem aktuellen Spieler gehören
      }
    }

    if (inventoryItems.length > header.length) {
      inventoryItems.delete(inventoryItems.length - 2, inventoryItems.length)
    }

    inventoryItems.toString
  }

}