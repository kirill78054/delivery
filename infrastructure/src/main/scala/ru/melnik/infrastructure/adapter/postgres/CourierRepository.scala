package ru.melnik.infrastructure.adapter.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import ru.melnik.core.domain.model.courieraggregate.{Courier, CourierStatus, Transport}
import ru.melnik.core.domain.sharedkernel.Location
import ru.melnik.core.port.ICourierRepository

import java.util.UUID

class CourierRepository(uw: UnitOfWork, tr: Aux[IO, Unit]) extends ICourierRepository {
  require(uw != null)
  require(tr != null)

  override def addCourier(courier: Courier): Unit = {
    uw.transaction(_ => Some(
      sql"""
            insert into delivery_transport (id, name, speed) values (${courier.transport.id}, ${courier.transport.name}, ${courier.transport.speed})
        """
        .update
        .run
    ))

    uw.transaction(_ => Some(
      sql"""
            insert into delivery_courier (id, name, transport_id, location_x, location_y, courier_status)
              values (${courier.id}, ${courier.name}, ${courier.transport.id}, ${courier.location.x}, ${courier.location.y}, ${courier.courierStatus.name})
        """
        .update
        .run
    ))
  }

  override def updateCourier(courier: Courier): Unit = {
    uw.transaction(_ => Some(
      sql"""
          update delivery_transport set name = ${courier.transport.name} , speed = ${courier.transport.speed} where id = ${courier.transport.id}
        """
        .update
        .run
    ))

    uw.transaction(_ => Some(
      sql"""
          update delivery_courier set
          name = ${courier.name}
          , location_x = ${courier.location.x}
          , location_y = ${courier.location.y}
          , courier_status = ${courier.courierStatus.name}
          where id = ${courier.id}
        """
        .update
        .run
    ))
  }

  override def findCourierById(courierId: UUID): Courier = {
    val maybeCourier = sql"SELECT id, name, transport_id, location_x, location_y, courier_status FROM delivery_courier where id = $courierId"
      .query[(UUID, String, UUID, Int, Int, String)]
      .option
      .transact(tr)
      .unsafeRunSync()

    maybeCourier.map(courier => {
        Courier.fromDb(
          courier._1,
          courier._2,
          findTransport(courier._3),
          Location(courier._4, courier._5),
          CourierStatus(courier._6)
        )
      })
      .getOrElse(throw new NoSuchElementException(s"Courier not found, id = $courierId"))
  }

  private def findTransport(id: UUID): Transport = {
    val maybeTransport = sql"SELECT id, name, speed FROM delivery_transport where id = $id"
      .query[(UUID, String, Int)]
      .option
      .transact(tr)
      .unsafeRunSync()

    val transportTuple = maybeTransport.getOrElse(throw new NoSuchElementException(s"Transport not found, transport_id = $id"))
    Transport.fromDb(transportTuple._1, transportTuple._2, transportTuple._3)
  }

  override def findAllFreeCourier(): Seq[Courier] = {
    sql"SELECT id, name, transport_id, location_x, location_y, courier_status FROM delivery_courier where courier_status = 'free'"
      .query[(UUID, String, UUID, Int, Int, String)]
      .to[List]
      .transact(tr)
      .unsafeRunSync()
      .map(courier => {
        Courier.fromDb(
          courier._1,
          courier._2,
          findTransport(courier._3),
          Location(courier._4, courier._5),
          CourierStatus(courier._6)
        )
      })
  }

}
