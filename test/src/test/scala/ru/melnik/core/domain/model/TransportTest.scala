package ru.melnik.core.domain.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.melnik.core.domain.model.courieraggregate.Transport
import ru.melnik.core.domain.model.courieraggregate.Transport.{SPEED_MAX, SPEED_MIN}
import ru.melnik.core.domain.sharedkernel.Location

class TransportTest extends AnyWordSpec with Matchers {

  "Transport invariants" should {
    "create an instance with valid name and speed" in {
      val name = "Car"
      val speed = 2
      val transport = Transport(name, speed)

      transport.name shouldBe name
      transport.speed shouldBe speed
      transport.id should not be null
    }

    "throw IllegalArgumentException for null name" in {
      an[IllegalArgumentException] should be thrownBy Transport(null, 2)
    }

    "throw IllegalArgumentException for empty name" in {
      an[IllegalArgumentException] should be thrownBy Transport("", 2)
    }

    s"throw IllegalArgumentException for speed below $SPEED_MIN" in {
      an[IllegalArgumentException] should be thrownBy Transport("Car", 0)
    }

    s"throw IllegalArgumentException for speed above $SPEED_MAX" in {
      an[IllegalArgumentException] should be thrownBy Transport("Car", 4)
    }
  }

  "Transport equal" should {
    val name = "Car"
    val speed = 2

    "have equal Transport when created with the same parameters" in {
      val transport1 = Transport(name, speed)
      val transport2 = Transport(name, speed)

      transport1.id should not be transport2.id
      transport1 should not be transport2
      transport1 equals transport2 shouldBe false
    }

    "be equal by Transport when compared" in {
      val transport1 = Transport(name, speed)
      val transport2 = transport1.clone()

      transport1 equals transport2 shouldBe true
      transport1 shouldBe transport2
      transport1.hashCode() shouldBe transport2.hashCode()
    }
  }

  "Transport move" should {
    "not move if the current location is already the target location" in {
      val transport = Transport("Car", 1)
      val start = Location(5, 5)
      val finish = Location(5, 5)
      transport.move(start -> finish) should be(start)
    }

    "Transport move to finish" in {
      val transport = Transport("Car", 3)
      val start = Location(1, 1)
      val finish = Location(5, 5)

      val location1 = transport.move(start -> finish)
      val location2 = transport.move(location1 -> finish)
      val location3 = transport.move(location2 -> finish)

      location3 shouldBe finish
    }

    "move catch null start location" in {
      val transport = Transport("Car", 3)
      an[IllegalArgumentException] should be thrownBy transport.move(null, Location(5, 5))
    }

    "move catch null finish location" in {
      val transport = Transport("Car", 3)
      an[IllegalArgumentException] should be thrownBy transport.move(Location(5, 5), null)
    }

  }

}
