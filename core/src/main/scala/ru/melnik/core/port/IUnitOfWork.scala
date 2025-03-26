package ru.melnik.core.port

import doobie.free.connection.ConnectionIO

trait IUnitOfWork {
  def transaction(f: Unit => Option[ConnectionIO[Int]]): Unit
  def apply(): Unit
}
