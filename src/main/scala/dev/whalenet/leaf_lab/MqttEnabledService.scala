package dev.whalenet.leaf_lab

import cats.effect.*
import cats.syntax.all.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import io.circe.generic.auto._

// Enhanced service that supports both HTTP and MQTT
class MqttEnabledService(
  sensorResultRepository: Repository[SensorResult], 
  sensorRepository: Repository[Sensor], 
  plantRepository: Repository[Plant], 
  personRepository: Repository[Person],
  mqttService: MqttService[IO]
) {

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

  // SensorResult - now with MQTT support
  def insertSensorResult(result: SensorResult): IO[Response[IO]] = {
    val ret_result = sensorResultRepository.insert(result)
    for {
      // Publish to MQTT when a sensor result is created
      _ <- mqttService.publish(MqttModels.createSensorResultMessage(ret_result))
      _ <- mqttService.publish(MqttModels.createSensorReadingMessage(ret_result))
      resp <- Ok(s"$ret_result")
    } yield resp
  }

  def findSensorResult(id: Int): IO[Response[IO]] = {
    val result = sensorResultRepository.get(id)
    result match {
      case Some(r) => Ok(s"$r")
      case None => NotFound(s"SensorResult with id $id not found")
    }
  }

  // Sensor - now with MQTT support
  def insertSensor(sensor: Sensor): IO[Response[IO]] = {
    val ret_sensor = sensorRepository.insert(sensor)
    for {
      // Publish to MQTT when a sensor is created
      _ <- mqttService.publish(MqttModels.createSensorMessage(ret_sensor))
      resp <- Ok(s"$ret_sensor")
    } yield resp
  }

  def findSensor(id: Int): IO[Response[IO]] = {
    val sensor = sensorRepository.get(id)
    sensor match {
      case Some(s) => Ok(s"$s")
      case None => NotFound(s"Sensor with id $id not found")
    }
  }

  // Plant - now with MQTT support
  def insertPlant(plant: Plant): IO[Response[IO]] = {
    val ret_plant = plantRepository.insert(plant)
    for {
      // Publish to MQTT when a plant is created
      _ <- mqttService.publish(MqttModels.createPlantMessage(ret_plant))
      resp <- Ok(s"$ret_plant")
    } yield resp
  }

  def findPlant(id: Int): IO[Response[IO]] = {
    val plant = plantRepository.get(id)
    plant match {
      case Some(p) => Ok(s"$p")
      case None => NotFound(s"Plant with id $id not found")
    }
  }

  // Person - now with MQTT support
  def insertPerson(person: Person): IO[Response[IO]] = {
    val ret_person = personRepository.insert(person)
    for {
      // Publish to MQTT when a person is created
      _ <- mqttService.publish(MqttModels.createPersonMessage(ret_person))
      resp <- Ok(s"$ret_person")
    } yield resp
  }

  def findPerson(id: Int): IO[Response[IO]] = {
    val person = personRepository.get(id)
    person match {
      case Some(p) => Ok(s"$p")
      case None => NotFound(s"Person with id $id not found")
    }
  }

  // Cleanup method to close MQTT connection
  def close(): IO[Unit] = mqttService.close()
}