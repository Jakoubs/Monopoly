package de.htwg.model
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
class BoardFieldSpec extends AnyWordSpec {
  "PropertyField" should {
    "build a property field" in{
      val f1 = new PropertyField("kpAlee",100,20,"P1","red",1000,0)
      f1.name should be("kpAlee")
      f1.price should be(100)
      f1.rent should be(20)
      f1.owner should be("P1")
      f1.color should be("red")
      f1.mortgage should be(1000)
      f1.house should be(0)
    }
    "buildHomes"{
      val f1 = new PropertyField("kpAlee", 100, 20, "P1", "red", 1000, 0)
      val p1 = Player("TestPlayer", 1000, 5)
      buyHause(p1, f1, 1)
      f1.house should be(1)
      p1.balance should be(900)
    }
    "calculate house price based on rent correctly" in {
      val f1 = new PropertyField("kpAlee", 100, 20, "P1", "red", 1000, 0)
      f1.calcHousePrice(f1.rent) should be(100)

      val f2 = new PropertyField("Park Place", 350, 35, "P2", "blue", 1750, 0)
      f2.calcHousePrice(f2.rent) should be(180) // 175 gerundet auf 180

      val f3 = new PropertyField("Baltic Avenue", 60, 4, "P3", "brown", 300, 0)
      f3.calcHousePrice(f3.rent) should be(20)
    }


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
