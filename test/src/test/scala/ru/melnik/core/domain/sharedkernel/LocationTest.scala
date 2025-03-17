package ru.melnik.core.domain.sharedkernel

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LocationTest extends AnyWordSpec with Matchers {
  "Location invariants" should {
    "first shouldBe second" in {
      val first = Location(1, 10)
      val second = Location(1, 10)
      first shouldEqual second
    }

    "first shouldBe equal second" in {
      val first = Location(1, 10)
      val second = Location(1, 10)
      first === second shouldBe true
    }

    "first not shouldBe second" in {
      val second = Location(1, 2)
      Location.minLocation should not equal second
    }

    s"x less ${Location.MIN}" in {
      val exception = intercept[IllegalArgumentException](Location(0, 1))
      exception.getMessage shouldEqual s"requirement failed: x must be between ${Location.MIN} and ${Location.MAX}, x: 0"
    }

    s"x greater ${Location.MAX}" in {
      val exception = intercept[IllegalArgumentException](Location(11, 1))
      exception.getMessage shouldEqual s"requirement failed: x must be between ${Location.MIN} and ${Location.MAX}, x: 11"
    }

    s"y less ${Location.MIN}" in {
      val exception = intercept[IllegalArgumentException](Location(1, 0))
      exception.getMessage shouldEqual s"requirement failed: y must be between ${Location.MIN} and ${Location.MAX}, y: 0"
    }

    s"y greater ${Location.MAX}" in {
      val exception = intercept[IllegalArgumentException](Location(1, 11))
      exception.getMessage shouldEqual s"requirement failed: y must be between ${Location.MIN} and ${Location.MAX}, y: 11"
    }
  }

  "create random Location" should {
    "location should not be null" in {
      Location.createRandom() should not be null
    }
  }

  "distance to between Location" should {
    "simple two location" in {
      val first = Location(4,9)
      val second = Location(2,6)
      first.distanceTo(second) shouldBe 5
    }
    "distant two location" in {
      val first = Location(1,1)
      val second = Location(10,10)
      first.distanceTo(second) shouldBe 18
    }
    "distant two other location" in {
      val first = Location(10,10)
      val second = Location(1,1)
      first.distanceTo(second) shouldBe 18
    }
    "equal two location" in {
      val first = Location(5,5)
      val second = Location(5,5)
      first.distanceTo(second) shouldBe 0
    }
    "near two location" in {
      val first = Location(1,1)
      val second = Location(1,2)
      first.distanceTo(second) shouldBe 1
    }
  }
}
