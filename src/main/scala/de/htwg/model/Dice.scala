package de.htwg.model

import scala.util.Random

class Dice:
  def rollDice(): (Int, Int) = {
    val a = Random.nextInt(6) + 1
    val b = Random.nextInt(6) + 1
    (a, b)
  }

  def rollDice(d1: Int, d2: Int): (Int, Int) = {
      (d1, d2)
  }



