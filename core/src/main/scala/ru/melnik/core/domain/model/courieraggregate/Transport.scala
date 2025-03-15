package ru.melnik.core.domain.model.courieraggregate

import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class Transport private(val id: UUID, val name: String, val speed: Int) {

  def move(currentToTarget: (Location, Location)): Location = {
    val (current, target) = currentToTarget
    require(current != null, "current is null")
    require(target != null, "target is null")
    if (current == target) return current
    import math.{abs, max, min}

    def limit(diff: Int, cruisingRange: Int): Int = min(max(diff, -cruisingRange), cruisingRange)

    var cruisingRange = this.speed
    val moveX = limit(target.x - current.x, cruisingRange)
    cruisingRange -= abs(moveX)

    val moveY = limit(target.y - current.y, cruisingRange)
    Location(current.x + moveX, current.y + moveY)
  }

  override def equals(other: Any): Boolean = other match {
    case that: Transport => that.canEqual(this) && id == that.id
    case _ => false
  }

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Transport]

  override def hashCode(): Int = Seq(id).map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)

  override def clone(): AnyRef = new Transport(this.id, this.name, this.speed)
}

object Transport {
  val SPEED_MIN: Int = 1
  val SPEED_MAX: Int = 3

  def apply(name: String, speed: Int): Transport = {
    require(name != null && name.nonEmpty)
    require(speed >= SPEED_MIN && speed <= SPEED_MAX, s"speed must be between $SPEED_MIN and $SPEED_MAX, current speed: $speed")

    new Transport(
      id = UUID.randomUUID(),
      name = name,
      speed = speed,
    )
  }

}
