package de.htwg.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.concurrent.PatienceConfiguration
import java.io.File
import javax.sound.sampled.{AudioInputStream, AudioSystem, Clip, LineEvent}
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

class SoundPlayerSpec extends AnyWordSpec with Matchers with ScalaFutures with PatienceConfiguration {

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  "SoundPlayer" should {
    "play a sound in the background without blocking" in {
      val soundPlayer = new SoundPlayer()
      val filePath = "src/main/resources/RollDice.wav" // Ersetze durch deine Testdatei

      val file = new File(filePath)
      if (!file.exists()) {
        try {
          file.createNewFile()
        } catch {
          case e: Exception => fail(s"Could not create dummy sound file: ${e.getMessage}")
        }
      }

      val startTime = System.currentTimeMillis()
      soundPlayer.playBackground(filePath)
      val endTime = System.currentTimeMillis()

      (endTime - startTime) should be < 100L
    }

    "play a sound and wait for it to complete" in {
      val soundPlayer = new SoundPlayer()
      val filePath = "src/main/resources/RollDice.wav" 

      val file = new File(filePath)
      if (!file.exists()) {
        try {
          file.createNewFile()
        } catch {
          case e: Exception => fail(s"Could not create dummy sound file: ${e.getMessage}")
        }
      }

      val future = soundPlayer.playAndWait(filePath)

      whenReady(future) { _ =>
        succeed
      }
    }
  }
}