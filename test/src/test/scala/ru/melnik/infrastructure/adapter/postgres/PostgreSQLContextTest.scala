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
import org.testcontainers.containers.PostgreSQLContainer
import ru.melnik.infrastructure.adapter.postgres.PostgreSQLContextTest.execFileScript

import scala.io.Source

class PostgreSQLContextTest extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  private val postgreSQLContainer = {
    val postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
    postgreSQLContainer.withDatabaseName("delivery")
    postgreSQLContainer.withUsername("mel")
    postgreSQLContainer.withPassword("mel")
    postgreSQLContainer
  }

  override protected def beforeEach(): Unit = {
    postgreSQLContainer.start()
    withTransaction(tr => {
      dropTables(tr)
      execFileScript("init_tables.sql", tr)
    })
  }

  def withTransaction(f: Aux[IO, Unit] => Unit): Unit = {
    val printSqlLogHandler: LogHandler[IO] = (logEvent: LogEvent) => IO {
      println(logEvent.sql)
    }

    Class.forName("org.postgresql.Driver")
    val tr = Transactor.fromDriverManager[IO].apply(
      driver = "org.postgresql.Driver",
      url = postgreSQLContainer.getJdbcUrl,
      user = postgreSQLContainer.getUsername,
      password = postgreSQLContainer.getPassword,
      logHandler = Some(printSqlLogHandler)
    )
    f(tr)
  }

  override protected def afterEach(): Unit = {
    withTransaction(dropTables)
    postgreSQLContainer.stop()
  }

  def dropTables(tr: Aux[IO, Unit]): Unit = execFileScript("drop_tables.sql", tr)
}

object PostgreSQLContextTest {

  def execFileScript(path: String, tr: Aux[IO, Unit]): Unit = {
    val tablesSrc = Source.fromFile(getClass.getClassLoader.getResource(s"sql/$path").getPath)

    tablesSrc.getLines().map(line => line.replace("\n", "")).mkString("").split(";")
      .foreach(Update0(_, None).run.transact(tr).unsafeRunSync())

    tablesSrc.close()
  }

}