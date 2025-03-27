package ru.melnik.core.application.usecase.queries.getnofinishorder

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux

import java.util.UUID
import doobie.postgres.implicits._

class GetNotFinishOrderHandler(tr: Aux[IO, Unit]) {
  require(tr != null, "tr is null")

  def handle(): GetBusyCourierResponse = {
    GetBusyCourierResponse(
      sql"SELECT id, location_x, location_y FROM delivery_order where order_status in('created', 'assigned')"
        .query[(UUID, Int, Int)]
        .to[List]
        .transact(tr)
        .unsafeRunSync()
        .map(res => Order(res._1, Location(res._2, res._3)))
    )
  }

}

case class GetBusyCourierResponse(couriers: Seq[Order])

case class Order(id: UUID, location: Location)

case class Location(x: Int, y: Int)