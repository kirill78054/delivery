package ru.melnik.infrastructure.adapter.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.Transactor
import doobie.implicits._
import doobie.util.log.{LogEvent, LogHandler}
import doobie.util.transactor.Transactor.Aux
import doobie.util.update.Update0
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.melnik.infrastructure.adapter.postgres.PostgreSQLContextTest.execFileScript

import scala.io.Source

class PostgreSQLContextTest extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  protected var tr: Aux[IO, Unit] = _

  private val printSqlLogHandler: LogHandler[IO] = (logEvent: LogEvent) => IO {
    println(logEvent.sql)
  }

  override protected def beforeEach(): Unit = {
    Class.forName("org.postgresql.Driver")
    tr = Transactor.fromDriverManager[IO].apply(
      driver = "org.postgresql.Driver",
      url = "jdbc:postgresql://0.0.0.0:5432/delivery",
      user = "mel",
      password = "mel",
      logHandler = Some(printSqlLogHandler)
    )

    dropTables()
    execFileScript("init_tables.sql", tr)
  }

  override protected def afterEach(): Unit = dropTables()

  def dropTables(): Unit = execFileScript("drop_tables.sql", tr)
}

object PostgreSQLContextTest {

  def execFileScript(path: String, tr: Aux[IO, Unit]): Unit = {
    val tablesSrc = Source.fromFile(getClass.getClassLoader.getResource(s"sql/$path").getPath)

    tablesSrc.getLines().map(line => line.replace("\n", "")).mkString("").split(";")
      .foreach(Update0(_, None).run.transact(tr).unsafeRunSync())

    tablesSrc.close()
  }

}