package de.htwg.model

import javax.sound.sampled.{AudioInputStream, AudioSystem, Clip, LineEvent}
import java.io.File
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

class SoundPlayer {

  /**
   * Spielt eine Sounddatei im Hintergrund ab. Die Methode kehrt sofort zurück.
   *
   * @param filePath Der Pfad zur Sounddatei. Unterstützte Formate hängen vom System ab (oft WAV, AIFF, AU).
   */
  def playBackground(filePath: String): Unit = {
    Future {
        val soundFile = new File(filePath)
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(soundFile)
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.start()
    }
  }

  /**
   * Spielt eine Sounddatei ab und wartet, bis die Wiedergabe abgeschlossen ist, bevor die Methode zurückkehrt.
   *
   * @param filePath Der Pfad zur Sounddatei. Unterstützte Formate hängen vom System ab (oft WAV, AIFF, AU).
   * @return Ein Future, das abgeschlossen wird, wenn der Sound fertig abgespielt wurde (oder bei einem Fehler).
   */
  def playAndWait(filePath: String): Future[Unit] = {
    val promise = Promise[Unit]()
      val soundFile = new File(filePath)
      val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(soundFile)
      val clip: Clip = AudioSystem.getClip()

      clip.addLineListener(event => {
        if (event.getType == LineEvent.Type.STOP || event.getType == LineEvent.Type.CLOSE) {
          promise.success(())
        }
      })

      clip.open(audioInputStream)
      clip.start()
    promise.future
  }
}