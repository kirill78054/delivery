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

}
