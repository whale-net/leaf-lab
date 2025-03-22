package dev.whalenet.leaf_lab

// TODO - cleanup imports
import cats.effect.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import com.comcast.ip4s.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import io.circe.generic.auto._


// for now, one universal service with everything
// maybe forever too
class Service(sensorResultRepository: Repository[SensorResult]) {

  val httpApp: HttpApp[IO] = HttpRoutes
    .of[IO] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello... $name!")
      case req@POST -> Root / "result" =>
        for {
          result <- req.as[SensorResult]
          resp <- insertSensorResult(result)
        } yield resp
      case GET -> Root / "result" / IntVar(id) =>
        for {
          resp <- findSensorResult(id)
        } yield resp
    }
    .orNotFound

  def insertSensorResult(result: SensorResult):  IO[Response[IO]] = {
    val ret_result = sensorResultRepository.insert(result)
    Ok(s"$ret_result")
  }

  def findSensorResult(id: Int): IO[Response[IO]] = {
    val result = sensorResultRepository.get(id) // Renamed from findById
    result match {
      case Some(r) => Ok(s"$r")
      case None => NotFound(s"SensorResult with id $id not found")
    }
  }
}
