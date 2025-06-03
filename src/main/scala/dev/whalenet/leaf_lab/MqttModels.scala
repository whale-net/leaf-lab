package dev.whalenet.leaf_lab

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._

// MQTT-specific models that wrap existing models
object MqttModels {
  
  // MQTT message wrapper that contains the topic and payload
  case class MqttMessageWrapper[T](topic: String, payload: T, qos: Int = 1)
  
  // MQTT topics for different entity types
  object Topics {
    val PERSON_CREATED = "leaf-lab/person/created"
    val PERSON_UPDATED = "leaf-lab/person/updated"
    
    val PLANT_CREATED = "leaf-lab/plant/created"
    val PLANT_UPDATED = "leaf-lab/plant/updated"
    
    val SENSOR_CREATED = "leaf-lab/sensor/created"
    val SENSOR_UPDATED = "leaf-lab/sensor/updated"
    
    val SENSOR_RESULT_CREATED = "leaf-lab/sensor-result/created"
    val SENSOR_RESULT_UPDATED = "leaf-lab/sensor-result/updated"
    
    // Topic for live sensor readings
    def sensorResultTopic(plantId: Int, sensorId: Int): String = 
      s"leaf-lab/plant/$plantId/sensor/$sensorId/reading"
  }
  
  // MQTT wrappers for existing models - keeping models as close as possible
  case class MqttPerson(person: Person, messageId: String = java.util.UUID.randomUUID().toString)
  case class MqttPlant(plant: Plant, messageId: String = java.util.UUID.randomUUID().toString)
  case class MqttSensor(sensor: Sensor, messageId: String = java.util.UUID.randomUUID().toString)
  case class MqttSensorResult(sensorResult: SensorResult, messageId: String = java.util.UUID.randomUUID().toString)
  
  // Helper methods to create MQTT messages
  def createPersonMessage(person: Person, topic: String = Topics.PERSON_CREATED): MqttMessageWrapper[MqttPerson] = {
    MqttMessageWrapper(topic, MqttPerson(person))
  }
  
  def createPlantMessage(plant: Plant, topic: String = Topics.PLANT_CREATED): MqttMessageWrapper[MqttPlant] = {
    MqttMessageWrapper(topic, MqttPlant(plant))
  }
  
  def createSensorMessage(sensor: Sensor, topic: String = Topics.SENSOR_CREATED): MqttMessageWrapper[MqttSensor] = {
    MqttMessageWrapper(topic, MqttSensor(sensor))
  }
  
  def createSensorResultMessage(sensorResult: SensorResult, topic: String = Topics.SENSOR_RESULT_CREATED): MqttMessageWrapper[MqttSensorResult] = {
    MqttMessageWrapper(topic, MqttSensorResult(sensorResult))
  }
  
  // Create sensor reading message for real-time data
  def createSensorReadingMessage(sensorResult: SensorResult): MqttMessageWrapper[MqttSensorResult] = {
    val topic = Topics.sensorResultTopic(sensorResult.plant_id, sensorResult.sensor_id)
    MqttMessageWrapper(topic, MqttSensorResult(sensorResult))
  }
  
  // JSON serialization helpers
  def toJson[T](message: MqttMessageWrapper[T])(implicit encoder: io.circe.Encoder[T]): String = {
    message.payload.asJson.noSpaces
  }
  
  def fromJson[T](json: String)(implicit decoder: io.circe.Decoder[T]): Either[io.circe.Error, T] = {
    decode[T](json)
  }
}