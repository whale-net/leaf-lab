package dev.whalenet.leaf_lab

// could one day be needed
//import io.circe.syntax._

import cats.effect.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.ember.server.*
import com.comcast.ip4s.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import io.circe.generic.auto._

implicit val srd: EntityDecoder[IO, SensorResult] = jsonOf[IO, SensorResult]

object Main extends IOApp {

  // not really sure what I'm doing here but this fixed the error without the deprecated method
  // this is for default[IO] in the EmberServerBuilder
  implicit def catlogfactory: Slf4jFactory[IO] = Slf4jFactory.create[IO]

  def run(args: List[String]): IO[ExitCode] = {

    DBConfig.init()

    // val srRepo = new InMemorySensorResultRepository()
    val srRepo = new DBSensorResultRepository()
    val service = new Service(srRepo)

    val LeafLabAPIServer = EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(8080).get)
      .withHttpApp(service.httpApp)
      .build

    LeafLabAPIServer
      .use(_ => IO.never) // Keeps the server running indefinitely
      .as(ExitCode.Success)
  }
}
