package ru.melnik.core.domain.sharedkernel

import ru.melnik.core.domain.sharedkernel.Location.{MAX, MIN}

import scala.util.Random

case class Location(x: Int, y: Int) {
  require(x >= MIN && x <= MAX, s"x must be between $MIN and $MAX, x: $x")
  require(y >= MIN && y <= MAX, s"y must be between $MIN and $MAX, y: $y")

  def distanceTo(other: Location): Int = (x - other.x).abs + (y - other.y).abs
}

object Location {
  val MIN: Int = 1
  val MAX: Int = 10

  def createRandom(): Location = {
    def createRandomInt(): Int = MIN + Random.nextInt(MAX - MIN + 1)

    Location(createRandomInt(), createRandomInt())
  }
}
