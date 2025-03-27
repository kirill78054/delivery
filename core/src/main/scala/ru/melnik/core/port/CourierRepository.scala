package ru.melnik.core.port

import ru.melnik.core.domain.model.courieraggregate.Courier

import java.util.UUID

trait CourierRepository {
  def addCourier(courier: Courier): Unit
  def updateCourier(courier: Courier): Unit
  def findCourierById(courierId: UUID): Courier
  def findAllFreeCourier(): Seq[Courier]
}