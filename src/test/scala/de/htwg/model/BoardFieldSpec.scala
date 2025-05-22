package de.htwg.model
import de.htwg.controller.Controller
import de.htwg.{Board, MonopolyGame}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.BoardField
import de.htwg.model.PropertyField.*
import de.htwg.model.PropertyField.Color.*
import de.htwg.model.PropertyField.{House, Mortgage}

class BoardFieldSpec extends AnyWordSpec {

  val dummyPlayer = Player("dummy", 1500, 0, isInJail = false, 0)
  val otherPlayer = Player("other", 1500, 0, isInJail = false, 0)

  val dice = new Dice()
  val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
  val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)
  val fields = Vector(
    GoField,
    PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(3),
    PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(100, 5),
    TrainStationField("Marklylebone Station", 6, 200, Some(player1)),
    PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(8),
    PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    JailField,
    PropertyField("Pink1", 12, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility, None),
    PropertyField("Pink2", 14, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Pink3", 15, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("Fenchurch ST Station", 16, 200, None),
    PropertyField("Orange1", 17, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(18),
    PropertyField("Orange2", 19, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Orange3", 20, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    FreeParkingField(0),
    PropertyField("Red1", 22, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(23),
    PropertyField("Red2", 24, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Red3", 25, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("King's Cross Station", 26, 200, None),
    PropertyField("Yellow1", 27, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    UtilityField("Water Works", 28, 150, UtilityField.UtilityCheck.utility, None),
    PropertyField("Yellow2", 29, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Yellow3", 30, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    GoToJailField(),
    PropertyField("Green1", 32, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Green2", 33, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(34),
    PropertyField("Green3", 35, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("Liverpool ST Station", 36, 200, None),
    ChanceField(37),
    PropertyField("DarkBlue1", 38, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(200, 39),
    PropertyField("DarkBlue2", 40, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0))
  )

  val board = Board(fields)
  val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
  val controller = new Controller(initialGame, dice)

  val ownedProperties: Map[Player, List[PropertyField]] =
    board.fields.collect { case p: PropertyField if p.owner.isDefined => (p.owner.get, p) }
      .groupBy(_._1).view.mapValues(_.map(_._2).toList).toMap

  val ownedTrainStations: Map[Player, Int] =
    board.fields.collect { case t: TrainStationField if t.owner.isDefined => (t.owner.get, 1) }
      .groupBy(_._1).view.mapValues(_.size).toMap

  val ownedUtilities: Map[Player, Int] =
    board.fields.collect { case u: UtilityField if u.owner.isDefined => (u.owner.get, 1) }
      .groupBy(_._1).view.mapValues(_.size).toMap

  val diceResult = 7

  val rentVisitor = new RentVisitor(dummyPlayer, Vector(dummyPlayer, otherPlayer), board, diceResult, ownedProperties, ownedTrainStations, ownedUtilities)

  "PropertyField" should {
    "build a property field" in {
      val player = Player("Tim", 500, 0)
      val f1 = PropertyField("kpAlee", 10, 100, 20, Some(player), Red, Mortgage(1000))
      f1.name should be("kpAlee")
      f1.index should be(10)
      f1.price should be(100)
      f1.rent should be(20)
      f1.owner should be(Some(player))
      f1.color should be(Red)
      f1.mortgage.price should be(1000)
      f1.house.amount should be(0)

    }

    "be created without Default values" in {
      val f1 = PropertyField("kpAlee", 10, 100, 20, None, Red)
      f1.name should be("kpAlee")
      f1.price should be(100)
      f1.rent should be(20)
      f1.owner should be(None)
      f1.color should be(Red)
      f1.mortgage.price should be(0)
      f1.house.amount should be(0)
      f1.mortgage.active should be(false)
    }
    "buildHomes" in {
      val (newp,newf) = PropertyField.House().buyHouse(player1, fields(21).asInstanceOf[PropertyField], initialGame)
      controller.updateBoardAndPlayer(newp,newf)
      val updatedField = controller.game.board.fields(21).asInstanceOf[PropertyField]
      updatedField.house.amount should be(1)
      val updatedPlayer = controller.game.players.find(_.name == player1.name).get
      updatedPlayer.balance should be(1450)
    }

    "not build house if player is not the owner" in {
      val (newp, newf) = PropertyField.House().buyHouse(player1, fields(16).asInstanceOf[PropertyField], initialGame)
      controller.updateBoardAndPlayer(newp, newf)
      val updatedField = controller.game.board.fields(16).asInstanceOf[PropertyField]
      updatedField.house.amount should be(0)
      val updatedPlayer = controller.game.players.find(_.name == player1.name).get
      updatedPlayer.balance should be(1500)
    }

    "not buildHomes if balance is to low" in {
      val p1 = Player("TestPlayer", 0, 5)
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some(p1), Red, Mortgage(1000))
      val (newf1, newp1) = PropertyField.House().buyHouse(p1,f1,initialGame)

      controller.updateBoardAndPlayer(newf1, newp1)
      val updatedField = controller.game.board.fields(newf1.index-1).asInstanceOf[PropertyField]
      updatedField.house.amount should be(0)
      val updatedPlayer = controller.game.players.find(_.name == p1.name).get
      updatedPlayer.balance should be(0)
    }

    "not buildHomes if max Hotel" in {
      val p1 = Player("TestPlayer", 1000, 5)
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some(p1), Red, Mortgage(1000), House(5))
      val (newf1, newp1) = PropertyField.House().buyHouse(p1,f1,initialGame)
      controller.updateBoardAndPlayer(newf1, newp1)
      val updatedField = controller.game.board.fields(newf1.index - 1).asInstanceOf[PropertyField]
      updatedField.house.amount should be(5)
      val updatedPlayer = controller.game.players.find(_.name == p1.name).get
      updatedPlayer.balance should be(1000)
    }

    "calculate house price based on rent correctly" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, None, Red, Mortgage(1000))
      House().calculateHousePrice(f1.price) should be(50)

      val f2 = PropertyField("Park Place", 5, 350, 35, None, DarkBlue, Mortgage(1750))
      House().calculateHousePrice(f2.price) should be(180)

      val f3 = PropertyField("Baltic Avenue", 5, 64, 4, None, Brown, Mortgage(300))
      House().calculateHousePrice(f3.price) should be(40)
    }
    "have a calculateRent method" which {
      "return the base rent if there are no houses" in {
        val field = PropertyField(
          name = "Test Property",
          index = 1,
          price = 100,
          rent = 20,
          owner = None,
          color = Color.Brown,
          mortgage = Mortgage(),
          house = House(0)
        )
        PropertyField.calculateRent(field) should be(20)
      }

      "return the base rent plus half the base rent per house" in {
        val fieldWithOneHouse = PropertyField(
          name = "Test Property",
          index = 1,
          price = 100,
          rent = 20,
          owner = None,
          color = Color.Brown,
          mortgage = Mortgage(),
          house = House(1)
        )
        PropertyField.calculateRent(fieldWithOneHouse) should be(30)

        val player = Player("Tim", 500)
        val fieldWithTwoHouses = PropertyField(
          name = "Another Property",
          index = 5,
          price = 200,
          rent = 30,
          owner = Some(player),
          color = Color.LightBlue,
          mortgage = Mortgage(),
          house = House(2)
        )
        PropertyField.calculateRent(fieldWithTwoHouses) should be(60)

        val fieldWithMaxHouses = PropertyField(
          name = "Expensive Property",
          index = 10,
          price = 500,
          rent = 50,
          owner = Some(player),
          color = Color.DarkBlue,
          mortgage = Mortgage(),
          house = House(5)
        )
        PropertyField.calculateRent(fieldWithMaxHouses) should be(175)
      }
    }
  }

  "Mortgage" should {
    "toggle the mortgage status in a PropertyField" in {
      val Player1 = Player("Tim",500)
      val originalField = PropertyField("TestStreet", 1, 100, 10, Some(Player1), Red, Mortgage(100))
      val toggledMortgage = originalField.mortgage.toggle()
      val updatedField = originalField.copy(mortgage = toggledMortgage)

      updatedField.mortgage.active should be(true)
    }
    "toggle the mortgage and not affect price" in {
      val mortgage = Mortgage()
      val updatedMortgage = mortgage.toggle()
      updatedMortgage.active should be(true)
      updatedMortgage.price should be(0)
    }

  }


  "GoField" should {
    "be created" in {
      val goField = GoField
      goField.index should be(1)
      goField.name should be("GoField")
    }
    "add player 200 to Player" in {
      val goField = GoField
      val player = Player("TestPlayer", 1000, 5)
      val updatedPlayer = goField.addMoney(player)
      updatedPlayer.balance should be(1200)
    }

  }

  "JailField" should {
    "be created" in {
      val jail = JailField
      jail.index should be(11)
      jail.name should be("Jail")
    }
  }

  "GoToJailField" should {
    "have a name and an index" in {
      val goToJailField = GoToJailField()
      goToJailField.name should be("GoToJail")
      goToJailField.index should be(31)
    }
    "send a Player to jail" in {
      val player = Player("TestPlayer", 1000, 5)
      val goToJailField = GoToJailField()
      val updatedPlayer = goToJailField.goToJail(player)
      updatedPlayer.position should be(11)
      updatedPlayer.isInJail should be(true)
    }

    "not give Player money when moving over Go" in {
      val player = Player("TestPlayer", 1000, 5)
      val goToJailField = GoToJailField()
      val updatedPlayer = goToJailField.goToJail(player)
      updatedPlayer.balance should be(1000)
    }

  }

  "FreeParkingField" should {
    "have a name and an index and an amount" in {
      val freeParkingField = FreeParkingField(0)
      freeParkingField.name should be("FreeParking")
      freeParkingField.index should be(21)
      freeParkingField.amount should be(0)
    }
    "increase amount when player losemoney to Card" in {
      val player = Player("TestPlayer", 100, 21)
      val action = LoseMoney(100)
      val freeParkingField = FreeParkingField(0)
      val (updatedPlayer, updatedField) = action.apply(player, freeParkingField)
      updatedField.amount should be(100)
    }
    "give player amount when on field" in {
      val player = Player("TestPlayer", 100, 21)
      val freeParkingField = FreeParkingField(100)
      val updatedplayer = freeParkingField.apply(player)
      updatedplayer.balance should be(200)
    }
    "decrease amount when player collects money" in {
      val player = Player("TestPlayer", 100, 21)
      val freeParkingField = FreeParkingField(100)
      val updatedField = freeParkingField.resetAmount()
      updatedField.amount should be(0)
    }
  }

  "A ChanceField" should {
    "have a correct name and index" in {
      val chance = ChanceField(7)
      chance.name shouldBe "ChanceField"
      chance.index shouldBe 7
      chance.CardList should not be empty
    }
  }

  "A CommunityChestField" should {
    "be correctly initialized" in {
      val community = CommunityChestField(2)
      community.name shouldBe "communityCard"
      community.index shouldBe 2
    }
  }

  "A TaxField" should {
    "be correctly initialized with amount and index" in {
      val taxField = TaxField(200, 4)
      taxField.amount shouldBe 200
      taxField.index shouldBe 4
      taxField.name shouldBe "TaxField"
    }
  }

  "A TrainStationField" should {
    "be correctly initialized with index and no owner" in {
      val trainStation = TrainStationField("kp", 5,200, None)
      trainStation.index shouldBe 5
      trainStation.name shouldBe "kp"
      trainStation.owner shouldBe None
    }

    "be able to have an owner" in {
      val player = Player("Alice", 1500)
      val trainStation = TrainStationField("kp", 15,200,Some(player))
      trainStation.owner shouldBe defined
      trainStation.owner.get shouldBe player
    }

  }


  "A UtilityField" should {
    "be correctly initialized" in {
      val utility = UtilityField("Water Works", 12, 150, UtilityField.UtilityCheck.utility, None)
      utility.name shouldBe "Water Works"
      utility.index shouldBe 12
      utility.price shouldBe 150
      utility.utility shouldBe UtilityField.UtilityCheck.utility
      utility.owner shouldBe None
    }
  }

  "visit PropertyField and calculate rent correctly" in {
    val property = PropertyField("brown1", 1, 100, 10, Some(otherPlayer), PropertyField.Color.Brown)
    property.accept(rentVisitor) shouldBe 10
  }

  "visit TrainStationField and calculate rent correctly" in {
    val station = TrainStationField("Station1", 5, 200, Some(otherPlayer))
    station.accept(rentVisitor) shouldBe 25 // Since owned 1 station
  }

  "visit UtilityField and calculate rent correctly" in {
    val utility = UtilityField("Utility1", 12, 150, UtilityField.UtilityCheck.utility, Some(otherPlayer))
    utility.accept(rentVisitor) shouldBe diceResult * 4 // Since owned 1 utility
  }

  "visit GoToJailField and return 0" in {
    val goToJail = GoToJailField()
    goToJail.accept(rentVisitor) shouldBe 0
  }

  "visit FreeParkingField and return 0" in {
    val freeParking = FreeParkingField(100)
    freeParking.accept(rentVisitor) shouldBe 0
  }

  "visit ChanceField and return 0" in {
    val chance = ChanceField(7)
    chance.accept(rentVisitor) shouldBe 0
  }

  "visit CommunityChestField and return 0" in {
    val cc = CommunityChestField(8)
    cc.accept(rentVisitor) shouldBe 0
  }

  "visit TaxField and return amount" in {
    val tax = TaxField(75, 4)
    tax.accept(rentVisitor) shouldBe 75
  }

  "visit GoField and return 0" in {
    GoField.accept(rentVisitor) shouldBe 0
  }

  "visit JailField and return 0" in {
    JailField.accept(rentVisitor) shouldBe 0
  }

  "A BuyableField" should {

    "allow buying if player has enough money and no owner yet" in {
      val player = Player("Alice", balance = 500, position = 4)
      val property = PropertyField("kpAlee", 4, 100, 20, None, Red, Mortgage(1000))


      val (newField, updatedPlayer) = property.buy(player)

      val propertyField = newField.asInstanceOf[PropertyField]
      propertyField.owner should contain(player)
      updatedPlayer.balance shouldBe 400
    }

    "not allow buying if player has insufficient funds" in {
      val player = Player("Bob", balance = 100, position = 4)
      val property = PropertyField("kpAlee", 4, 100, 20, None, Red, Mortgage(1000))

      val (newField, updatedPlayer) = property.buy(player)
      val propertyField = newField.asInstanceOf[PropertyField]
      propertyField.owner shouldBe empty
      updatedPlayer shouldBe player
    }

    "not allow buying if the property already has an owner" in {
      val owner = Player("Charlie", balance = 300, position = 2)
      val player = Player("Dana", balance = 500, position = 4)
      val property = PropertyField("kpAlee", 4, 100, 20, Some(owner), Red, Mortgage(1000))

      val (newField, updatedPlayer) = property.buy(player)

      val propertyField = newField.asInstanceOf[PropertyField]
      propertyField.owner should contain(owner)
      updatedPlayer shouldBe player
    }
  }
}