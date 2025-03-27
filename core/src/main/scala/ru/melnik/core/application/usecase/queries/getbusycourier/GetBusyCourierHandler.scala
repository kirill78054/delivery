package ru.melnik.core.application.usecase.queries.getbusycourier

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux

import java.util.UUID

class GetBusyCourierHandler(tr: Aux[IO, Unit]) {
  require(tr != null, "tr is null")

  def handle(): GetBusyCourierResponse = {
    GetBusyCourierResponse(
      sql"SELECT id, name, location_x, location_y, transport_id FROM delivery_courier where courier_status = 'busy'"
        .query[(UUID, String, Int, Int, UUID)]
        .to[List]
        .transact(tr)
        .unsafeRunSync()
        .map(res => Couriers(res._1, res._2, Location(res._3, res._4), res._5))
    )
  }

}

case class GetBusyCourierResponse(couriers: Seq[Couriers])

case class Couriers(id: UUID, name: String, location: Location, transportId: UUID)

case class Location(x: Int, y: Int)