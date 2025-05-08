package de.htwg.util

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.util.util.{Observable, Observer}
import org.scalatest.matchers.must.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {
    "add and notify observers" in {
      var wasUpdated = false

      val testObserver = new Observer {
        override def update(): Unit = wasUpdated = true
      }

      val observable = new Observable {}
      observable.add(testObserver)
      observable.notifyObservers()

      wasUpdated should be(true)
    }

    "remove observers so they are not notified" in {
      var wasUpdated = false

      val testObserver = new Observer {
        override def update(): Unit = wasUpdated = true
      }

      val observable = new Observable {}
      observable.add(testObserver)
      observable.remove(testObserver)
      observable.notifyObservers()

      wasUpdated should be(false)
    }
  }
}
