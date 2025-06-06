package de.htwg.model.modelBaseImple

import java.io.File
import javax.sound.sampled.{AudioInputStream, AudioSystem, Clip, LineEvent}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class SoundPlayer {
  def playBackground(filePath: String): Unit = {
    Future {
        val soundFile = new File(filePath)
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(soundFile)
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.start()
    }
  }
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