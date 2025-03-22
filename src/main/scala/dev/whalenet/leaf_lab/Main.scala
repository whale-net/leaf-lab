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

// TODO - need to move these to another locatioin
// but vscode is no good at that type of refactoring in scala
implicit val sensorResult_jsonOf: EntityDecoder[IO, SensorResult] = jsonOf[IO, SensorResult]
implicit val sensor_jsonOf: EntityDecoder[IO, Sensor] = jsonOf[IO, Sensor]
implicit val plant_jsonOf: EntityDecoder[IO, Plant] = jsonOf[IO, Plant]
implicit val person_jsonOf: EntityDecoder[IO, Person] = jsonOf[IO, Person]

object Main extends IOApp {

  // not really sure what I'm doing here but this fixed the error without the deprecated method
  // this is for default[IO] in the EmberServerBuilder
  implicit def catlogfactory: Slf4jFactory[IO] = Slf4jFactory.create[IO]

  def run(args: List[String]): IO[ExitCode] = {

    DBConfig.init()

    // val srRepo = new InMemorySensorResultRepository()
    val srRepo = new DBSensorResultRepository()
    val sRepo = new DBSensorRepository()
    val plRepo = new DBPlantRepository()
    val peRepo = new DBPersonRepository()
    val service = new Service(srRepo, sRepo, plRepo, peRepo)

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
