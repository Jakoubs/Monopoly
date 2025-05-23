package de.htwg.model

import scala.util.Random
import de.htwg.model.SoundPlayer

class Dice:
  print("rolling....")
  def rollDice(valid: Boolean): (Int, Int) = {
    if(valid) {
      val waitFuture = SoundPlayer().playAndWait("src/main/resources/RollDice.wav")
      scala.concurrent.Await.result(waitFuture, scala.concurrent.duration.Duration.Inf)
    }
    val a = Random.nextInt(6) + 1
    val b = Random.nextInt(6) + 1
    (a, b)
  }

  def rollDice(d1: Int, d2: Int): (Int, Int) = {
      (d1, d2)
  }




