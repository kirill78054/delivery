package ru.melnik.core.domain.model.courieraggregate

import ru.melnik.core.domain.model.courieraggregate.CourierStatus.Busy

sealed abstract class CourierStatus(val name: String) {
  def setCourierStatus(status: CourierStatus): CourierStatus = {
    (this, status) match {
      case (Busy, Busy) => throw new IllegalArgumentException("courierStatus status is busy")
      case (_, _) => println(s"set courier status $this -> $status")
    }
    status
  }
}

object CourierStatus {
  case object Free extends CourierStatus("free")
  case object Busy extends CourierStatus("busy")

  def apply(name: String): CourierStatus = {
    name.toLowerCase() match {
      case "free" => Free
      case "busy" => Free
      case other => throw new IllegalArgumentException(s"Courier status not match: $other")
    }
  }
}