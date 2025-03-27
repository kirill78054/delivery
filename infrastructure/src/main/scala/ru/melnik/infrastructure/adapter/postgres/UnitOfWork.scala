package ru.melnik.infrastructure.adapter.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxFlatMapOps
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import ru.melnik.core.port.IUnitOfWork

class UnitOfWork(tr: Aux[IO, Unit]) extends IUnitOfWork {
  private var transactionCommands: Seq[ConnectionIO[Int]] = Seq.empty

  override def transaction(f: Unit => Option[ConnectionIO[Int]]): Unit = f.apply().foreach(command => transactionCommands :+= command)

  override def apply(): Unit = {
    if (transactionCommands.nonEmpty) {
      transactionCommands.reduceLeft(_ >> _).transact(tr).unsafeRunSync()
      transactionCommands = Seq.empty
    }
  }

}