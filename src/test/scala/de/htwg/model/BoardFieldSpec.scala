package de.htwg.model
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
class BoardFieldSpec extends AnyWordSpec {
  "PropertyField" should {
    val f1 = new PropertyField("kpAlee",100,20,"P1",color = "red", mortgage = 1000, house = 0)
    should be(1)
  }
  "GoField" should {
  }
  "JailField" should {
  }
  "VisitField" should {
  }
  "GoToJailField" should {
  }
  "FreeParkingField" should {
  }
  "ChanceField" should {
  }
  "ChanceField" should {
  }


}
