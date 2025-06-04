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
import org.typelevel.log4cats.Logger
import io.circe.generic.auto._


// for now, one universal service with everything
// maybe forever too
class Service(sensorResultRepository: Repository[SensorResult], sensorRepository: Repository[Sensor], plantRepository: Repository[Plant], personRepository: Repository[Person]) {

  implicit val logger: Logger[IO] = Slf4jFactory.create[IO].getLogger

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
      case req@POST -> Root / "sensor" =>
        for {
          sensor <- req.as[Sensor]
          resp <- insertSensor(sensor)
        } yield resp
      case GET -> Root / "sensor" / IntVar(id) =>
        for {
          resp <- findSensor(id)
        } yield resp
      case req@POST -> Root / "plant" =>  
        for {
          plant <- req.as[Plant]
          resp <- insertPlant(plant)
        } yield resp
      case GET -> Root / "plant" / IntVar(id) =>
        for {
          resp <- findPlant(id)
        } yield resp
      case req@POST -> Root / "person" =>
        for {
          person <- req.as[Person]
          resp <- insertPerson(person)
        } yield resp
      case GET -> Root / "person" / IntVar(id) =>
        for {
          resp <- findPerson(id)
        } yield resp
    }
    .orNotFound

  // SensorResult
  def insertSensorResult(result: SensorResult):  IO[Response[IO]] = {
    for {
      _ <- logger.info(s"Inserting sensor result: plant_id=${result.plant_id}, sensor_id=${result.sensor_id}, value=${result.value}")
      ret_result = sensorResultRepository.insert(result)
      _ <- logger.info(s"Successfully inserted sensor result with id: ${ret_result.id}")
      response <- Ok(s"$ret_result")
    } yield response
  }

  def findSensorResult(id: Int): IO[Response[IO]] = {
    val result = sensorResultRepository.get(id)
    result match {
      case Some(r) => Ok(s"$r")
      case None => NotFound(s"SensorResult with id $id not found")
    }
  }

  // Sensor
  def insertSensor(sensor: Sensor): IO[Response[IO]] = {
    val ret_sensor = sensorRepository.insert(sensor)
    Ok(s"$ret_sensor")
  }

  def findSensor(id: Int): IO[Response[IO]] = {
    val sensor = sensorRepository.get(id)
    sensor match {
      case Some(s) => Ok(s"$s")
      case None => NotFound(s"Sensor with id $id not found")
    }
  }

  // Plant
  def insertPlant(plant: Plant): IO[Response[IO]] = {
    val ret_plant = plantRepository.insert(plant)
    Ok(s"$ret_plant")
  }

  def findPlant(id: Int): IO[Response[IO]] = {
    val plant = plantRepository.get(id)
    plant match {
      case Some(p) => Ok(s"$p")
      case None => NotFound(s"Plant with id $id not found")
    }
  }

  // Person
  def insertPerson(person: Person): IO[Response[IO]] = {
    val ret_person = personRepository.insert(person)
    Ok(s"$ret_person")
  }

  def findPerson(id: Int): IO[Response[IO]] = {
    val person = personRepository.get(id)
    person match {
      case Some(p) => Ok(s"$p")
      case None => NotFound(s"Person with id $id not found")
    }
  }
}
