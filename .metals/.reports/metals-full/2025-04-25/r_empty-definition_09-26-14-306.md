error id: scala/Unit#
file://<WORKSPACE>/src/main/scala/de/htwg/model/SoundPlayer.scala
empty definition using pc, found symbol in pc: scala/Unit#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Unit#
	 -scala/Predef.Unit#
offset: 635
uri: file://<WORKSPACE>/src/main/scala/de/htwg/model/SoundPlayer.scala
text:
```scala
package de.htwg.model

import javax.sound.sampled.{AudioInputStream, AudioSystem, Clip, LineEvent}
import java.io.File
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

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
    val promise = Promise[Unit@@]()
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
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Unit#