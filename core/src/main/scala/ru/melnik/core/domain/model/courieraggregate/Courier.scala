package ru.melnik.core.domain.model.courieraggregate

import ru.melnik.core.domain.model.courieraggregate.CourierStatus.{Busy, Free}
import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class Courier private(
  val id: UUID,
  val name: String,
  val transport: Transport,
  var location: Location,
  var courierStatus: CourierStatus
) {

  def setBusy(): Unit = {
    courierStatus = courierStatus.setCourierStatus(Busy)
  }

  def setFree(): Unit = {
    courierStatus = courierStatus.setCourierStatus(Free)
  }

  def move(target: Location): Unit = {
    require(courierStatus == Busy, "courierStatus status is`t busy")
    location = transport.move(location, target)
  }

  def calcTimeToTarget(target: Location): Double = {
    require(target != null, "target location is null")
    require(courierStatus == Free, "courierStatus status is`t free")
    this.location.distanceTo(target).doubleValue() / transport.speed
  }

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Courier]

  override def equals(other: Any): Boolean = other match {
    case that: Courier =>
      that.canEqual(this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }


  override def toString = s"Courier($id, $name, $transport, $location, $courierStatus)"
}

object Courier {

  def apply(name: String, transportName: String, transportSpeed: Int, location: Location): Courier =
    Courier(
      id = UUID.randomUUID(),
      name = name,
      transport = Transport(transportName, transportSpeed),
      location = location,
      courierStatus = Free
    )

  private def apply(id: UUID,
                    name: String,
                    transport: Transport,
                    location: Location,
                    courierStatus: CourierStatus): Courier = {
    require(id != null, "id is null")
    require(name != null, "id is null")
    require(name.nonEmpty, "id is empty")
    require(transport != null, "transport is null")
    require(location != null, "location is null")
    require(courierStatus != null, "courierStatus is null")

    new Courier(
      id = id,
      name = name,
      transport = transport,
      location = location,
      courierStatus = courierStatus
    )
  }

  def fromDb(id: UUID,
             name: String,
             transport: Transport,
             location: Location,
             courierStatus: CourierStatus): Courier =
    Courier(
      id = id,
      name = name,
      transport = transport,
      location = location,
      courierStatus = courierStatus
    )

}
