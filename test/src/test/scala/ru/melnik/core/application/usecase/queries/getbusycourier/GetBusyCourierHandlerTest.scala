package ru.melnik.core.application.usecase.queries.getbusycourier

import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.infrastructure.adapter.postgres.{CourierRepository, PostgreSQLContextTest, UnitOfWork}

class GetBusyCourierHandlerTest extends PostgreSQLContextTest {

  "handle" in {
    withTransaction(tr => {
      val work = new UnitOfWork(tr)
      val repository = new CourierRepository(work, tr)
      val courier1 = Courier("Kirill", "Car", 2, ru.melnik.core.domain.sharedkernel.Location.minLocation)
      val courier2 = Courier("Andrey", "Foot", 1, ru.melnik.core.domain.sharedkernel.Location.minLocation)
      val courier3 = Courier("Ivan", "Foot", 1, ru.melnik.core.domain.sharedkernel.Location.minLocation)
      courier1.setBusy()
      courier2.setBusy()
      repository.addCourier(courier1)
      repository.addCourier(courier2)
      repository.addCourier(courier3)
      work.apply()

      val handler = new GetBusyCourierHandler(tr)

      handler.handle().couriers.size shouldBe 2
      handler.handle().couriers.contains(Couriers(courier1.id, courier1.name, Location(courier1.location.x, courier1.location.y), courier1.transport.id))
      handler.handle().couriers.contains(Couriers(courier2.id, courier2.name, Location(courier2.location.x, courier2.location.y), courier2.transport.id))
    })
  }

}
