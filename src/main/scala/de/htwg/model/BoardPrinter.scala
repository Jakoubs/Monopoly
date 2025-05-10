package de.htwg.model

import de.htwg.MonopolyGame
import de.htwg.model.{BoardField, CommunityChestField, FreeParkingField, PropertyField, TrainStationField, UtilityField}

object BoardPrinter {
  def getBoardAsString(game: MonopolyGame): String = {
    printTop(game) + printSides(game) + printBottom(game)
  }

  private def printTop(game: MonopolyGame): String = {
    val fieldNames = game.board.fields.slice(0, 5)
    val fieldData = (0 to 4).map(i => formatField(fieldNames.lift(i)))

    val (stats1, stats2, stats3, stats4) = getStats(game)

    val line1 = "\n+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+"

    val baseLines = List(
      "|  ______  _____  |",
      "| |  ____ |     | |",
      "| |_____| |_____| |"
    )

    val fields2To10 = game.board.fields.filter(field => field.index > 1 && field.index < 11)

    val line2 = fields2To10.foldLeft(baseLines(0))((line, field) =>
      line + fillSpace(s"Nr${field.index}${getExtra(field)}", 8) + "|") + "                 |"

    val line3 = fields2To10.foldLeft(baseLines(1))((line, field) =>
      line + fillSpace(getPrice(field), 8) + '|') + "__________       |"

    val line4 = fields2To10.foldLeft(baseLines(2))((line, field) =>
      line + fillSpace(playersOnIndex(game,field.index, false), 8) + '|') + "  JAIL    |      |" + " " * 18 + "ALL FIELDS:"

    val additionalLines = List(
      s"|          Ss.    |${"-" * 80}+          |      |${" " * 20}Index: 1, GoField",
      s"|  ssssssssSSSS   |  ${fillSpace(stats1, 76)}  |          |      |${" " * 20}${fieldData(1)}",
      s"|          ;:`    |  ${fillSpace(stats2, 76)}  |          |      |${" " * 20}${fieldData(2)}",
      s"|${fillSpace(playersOnIndex(game,1, false), 17)}|  ${fillSpace(stats3, 76)}  |${fillSpace(playersOnIndex(game,11, true), 10)}|${fillSpace(playersOnIndex(game,11, false), 6)}|${" " * 20}${fieldData(3)}",
      s"+--------+--------+  ${fillSpace(stats4, 76)}  +--------+-+------+${" " * 20}${fieldData(4)}\n"
    )

    (List(line1, line2, line3, line4) ++ additionalLines).mkString("\n")
  }


  private def printSides(game: MonopolyGame): String = {
    val sideRows = (12 to 20).map { a =>
      val fieldAIndex = 52 - a
      val fieldA = game.board.fields.find(_.index == fieldAIndex)
      val fieldB = game.board.fields.find(_.index == a)

      (fieldA, fieldB) match {
        case (Some(fA), Some(fB)) =>
          val (fieldData1, fieldData2, fieldData3, fieldData4) = getFieldsData(game,a)

          val lines = List(
            s"|${fillSpace(fillSpace(s"${fA.index}${getExtra(fA)}", 8) + '|', 107)}|${fillSpace(s"${fB.index}${getExtra(fB)}", 8)}|${" " * 20}$fieldData1",
            s"|${fillSpace(fillSpace(getPrice(fA), 8) + '|', 107)}|${fillSpace(getPrice(fB), 8)}|${" " * 20}$fieldData2",
            s"|${fillSpace(fillSpace(playersOnIndex(game,fieldAIndex, false), 8) + '|', 107)}|${fillSpace(playersOnIndex(game,a, false), 8)}|${" " * 20}$fieldData3",
            if (a != 20)
              s"+--------+${" " * 98}+--------+${" " * 20}$fieldData4"
            else
              s"+--------+--------+${" " * 80}+--------+--------+\n"
          )
          lines.mkString("\n")
        case _ =>
          s"Fehler: Feld mit Index $fieldAIndex oder $a nicht gefunden.\n"
      }
    }

    sideRows.mkString("\n")
  }


  private def printBottom(game: MonopolyGame): String = {
    val fixedLines = List(
      "|   GO TO JAIL    |                                                                                |  FREE PARIKING  |",
      "|     ---->       |                                                                                |   ______        |",
      "|                 |                                                                                |  /|_||_`.__     |",
      "|                 +--------+--------+--------+--------+--------+--------+--------+--------+--------+ (   _    _ _\\   |"
    )

    val fields22To30 = (22 to 30).map(a => game.board.fields.find(_.index == 52 - a))

    val line6 = fields22To30.foldLeft("|                 |")((line, fieldOption) =>
      fieldOption match {
        case Some(field) => line + fillSpace(s"${field.index}${getExtra(field)}", 8) + '|'
        case None => line + fillSpace("N/A", 8) + '|'
      }
    ) + " =`-(_)--(_)-`   |"

    val freeParkingMoney = game.board.fields.find(_.index == 21) match {
      case Some(field: FreeParkingField) => getPrice(field)
      case _ => "N/A"
    }

    val line7 = fields22To30.foldLeft("|                 |")((line, fieldOption) =>
      fieldOption match {
        case Some(field) => line + fillSpace(getPrice(field), 8) + '|'
        case None => line + fillSpace("N/A", 8) + '|'
      }
    ) + s"   Money [$freeParkingMoney]    |"

    val line8 = fields22To30.foldLeft(s"|${fillSpace(playersOnIndex(game,31, false), 17)}|")((line, fieldOption) =>
      fieldOption match {
        case Some(field) => line + fillSpace(playersOnIndex(game,field.index, false), 8) + '|'
        case None => line + fillSpace(" ", 8) + '|'
      }
    ) + fillSpace(playersOnIndex(game,21, false), 17) + '|'

    val line9 = "+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+\n"

    (fixedLines ++ List(line6, line7, line8, line9)).mkString("\n")
  }

  // alle deine Methoden wie formatField, fillSpace usw. hierher verschieben!
  private def getFieldsData(game: MonopolyGame,currentIndex: Int): (String, String, String, String) = {
    val batchIndex = currentIndex - 12
    val startIdx = batchIndex * 4 + 5

    val fieldNames = game.board.fields.slice(startIdx, startIdx + 4)

    (0 to 3).map(i => formatField(fieldNames.lift(i))) match {
      case Seq(f1, f2, f3, f4) => (f1, f2, f3, f4)
      case _ => ("", "", "", "") // Fallback falls nicht genug Felder
    }
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

  private def playersOnIndex(game: MonopolyGame,idx: Int, inJail: Boolean): String = {
    game.players
      .filter(p => p.position == idx && p.isInJail == inJail)
      .map(_.name + " ")
      .mkString
  }


  private def getStats(game: MonopolyGame): (String, String, String, String) = {
    val playerInfos = game.players.map(p =>
      s"${p.name} pos[${p.position}], balance[${p.balance}], isInJail[${p.isInJail}]    "
    )

    playerInfos.foldLeft(("", "", "", "")) {
      case ((s1, s2, s3, s4), info) =>
        if (s1.length < 20) (s1 + info, s2, s3, s4)
        else if (s2.length < 20) (s1, s2 + info, s3, s4)
        else if (s3.length < 20) (s1, s2, s3 + info, s4)
        else (s1, s2, s3, s4 + info)
    }
  }



  def getInventoryString(game: MonopolyGame): String = {
    val header = s"INVENTORY Player: ${game.currentPlayer.name}| "

    val inventoryItems = game.board.fields.collect {
      case pf: PropertyField if pf.owner.contains(game.currentPlayer.name) =>
        s"idx:${pf.index}[${pf.house.amount}]"
      case ts: TrainStationField if ts.owner.contains(game.currentPlayer.name) =>
        s"idx:${ts.index}"
      case uf: UtilityField if uf.owner.contains(game.currentPlayer.name) =>
        s"idx:${uf.index}"
    }

    if (inventoryItems.isEmpty) {
      header
    } else {
      header + inventoryItems.mkString(", ")
    }
  }

}