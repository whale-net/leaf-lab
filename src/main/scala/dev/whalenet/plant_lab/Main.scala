package dev.whalenet.plant_lab

// could one day be needed
//import io.circe.syntax._

import cats.effect.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import com.comcast.ip4s.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import io.circe.generic.auto._

import java.time.ZonedDateTime

implicit val srd: EntityDecoder[IO, SensorResult] = jsonOf[IO, SensorResult]

object Main extends IOApp {

  // val srRepo = new InMemorySensorResultRepository()
  val srRepo = new DBSensorResultRepository()
  val service = new Service(srRepo)
  // not really sure what I'm doing here but this fixed the error without the deprecated method
  implicit def catlogfactory: Slf4jFactory[IO] = Slf4jFactory.create[IO]

  private def sensorResultHandler(sensorResult: SensorResult): IO[Response[IO]] = {
//    val person = Person(123, "tester")
//    val plant = Plant(456, "My Precious", "Tomato", person)
//    val sensor = Sensor(789, "Temperature", "Celsius")
//    val sensorResult = SensorResult(-1, plant, sensor, "100.0", ZonedDateTime.now)
//    val persistedResult = sensorResult.copy(id=2468)
//    Ok(s"it worked $persistedResult")
    val ret_result = srRepo.save(sensorResult)
    Ok(s"$ret_result")
  }

  val httpApp: HttpApp[IO] = HttpRoutes
    .of[IO] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello, $name!")
      case req @ POST -> Root / "result" =>
        for {
          result <- req.as[SensorResult]
          resp   <- sensorResultHandler(result)
        } yield resp
    }
    .orNotFound

  val PlantLabServer = EmberServerBuilder
    .default[IO]
    .withHost(Host.fromString("0.0.0.0").get)
    .withPort(Port.fromInt(8080).get)
    .withHttpApp(httpApp)
    .build

  def run(args: List[String]): IO[ExitCode] = {

    DBConfig.init()

    PlantLabServer
      .use(_ => IO.never) // Keeps the server running indefinitely
      .as(ExitCode.Success)
  }
}
