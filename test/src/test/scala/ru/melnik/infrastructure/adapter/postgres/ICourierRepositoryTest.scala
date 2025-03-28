package ru.melnik.infrastructure.adapter.postgres

import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._
import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class ICourierRepositoryTest extends PostgreSQLContextTest {

  "addCourier" in {
    withTransaction(tr => {
      val repository = new ICourierRepository(tr)
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      repository.addCourier(courier)

      val maybeCourier = sql"SELECT id, name, transport_id, location_x, location_y, courier_status FROM delivery_courier where id = ${courier.id}"
        .query[(UUID, String, UUID, Int, Int, String)]
        .option
        .transact(tr)
        .unsafeRunSync()

      val maybeTransport = sql"SELECT id, name, speed FROM delivery_transport where id = ${courier.transport.id}"
        .query[(UUID, String, Int)]
        .option
        .transact(tr)
        .unsafeRunSync()

      maybeTransport.isDefined shouldBe true
      maybeTransport shouldBe Some(courier.transport.id, courier.transport.name, courier.transport.speed)
      maybeCourier.isDefined shouldBe true
      maybeCourier shouldBe Some(courier.id, courier.name, courier.transport.id, courier.location.x, courier.location.y, courier.courierStatus.name)
    })
  }

  "updateCourier" in {
    withTransaction(tr => {
      val repository = new ICourierRepository(tr)
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      repository.addCourier(courier)
      courier.setBusy()
      courier.move(Location(5, 5))
      repository.updateCourier(courier)

      val maybeCourier = sql"SELECT id, name, transport_id, location_x, location_y, courier_status FROM delivery_courier where id = ${courier.id}"
        .query[(UUID, String, UUID, Int, Int, String)]
        .option
        .transact(tr)
        .unsafeRunSync()

      val maybeTransport = sql"SELECT id, name, speed FROM delivery_transport where id = ${courier.transport.id}"
        .query[(UUID, String, Int)]
        .option
        .transact(tr)
        .unsafeRunSync()

      maybeTransport.isDefined shouldBe true
      maybeTransport shouldBe Some(courier.transport.id, courier.transport.name, courier.transport.speed)
      maybeCourier.isDefined shouldBe true
      maybeCourier shouldBe Some(courier.id, courier.name, courier.transport.id, courier.location.x, courier.location.y, courier.courierStatus.name)
    })
  }

  "findCourier" in {
    withTransaction(tr => {
      val repository = new ICourierRepository(tr)
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      repository.addCourier(courier)

      val maybeCourier = repository.findCourierById(courier.id)
      maybeCourier.transport shouldBe courier.transport
      maybeCourier.location shouldBe courier.location
      maybeCourier shouldBe courier
    })
  }

  "findAllFreeCourier" in {
    withTransaction(tr => {
      val repository = new ICourierRepository(tr)
      val courier1 = Courier("Kirill", "Car", 2, Location.minLocation)
      val courier2 = Courier("Andrey", "Food", 1, Location.minLocation)
      repository.addCourier(courier1)
      repository.addCourier(courier2)

      val maybeCourier = repository.findAllFreeCourier()

      maybeCourier.size shouldBe 2
      maybeCourier shouldBe Seq(courier1, courier2)
    })
  }

}
