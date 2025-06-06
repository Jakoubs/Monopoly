package de.htwg.model

import de.htwg.model.modelBaseImple.SoundPlayer
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Await
import scala.concurrent.duration.*

class SoundPlayerTest extends AnyWordSpec {
  "SoundPlayer" should {
    "play sound in background" in {
      // Test playBackground method
      val soundPlayer = new SoundPlayer()
      noException should be thrownBy {
        soundPlayer.playBackground("src/main/resources/RollDice.wav")
      }
    }
    /*
    "play sound and wait for completion" in {
      // Test playAndWait method
      val soundPlayer = new SoundPlayer()
      val future = soundPlayer.playAndWait("src/main/resources/RollDice.wav")
      
      // We're not actually waiting for completion in the test,
      // just ensuring the method executes without exceptions
      future.isCompleted shouldBe false
    }

     */
  }
}
