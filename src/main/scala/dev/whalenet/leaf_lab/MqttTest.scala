package dev.whalenet.leaf_lab

import cats.effect.*
import io.circe.generic.auto._
import java.time.OffsetDateTime

// Simple test program to demonstrate MQTT functionality
object MqttTest extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    println("Running MQTT tests...")
    
    // Test MQTT models creation
    testMqttModels()
    
    IO(ExitCode.Success)
  }

  private def testMqttModels(): Unit = {
    println("\n=== Testing MQTT Models ===")
    
    // Create test data
    val testPerson = Person(1, "Test Person")
    val testPlant = Plant(1, "Test Plant", "Tomato", 1)
    val testSensor = Sensor(1, "Temperature", "Celsius")
    val testSensorResult = SensorResult(1, 1, 1, "25.5", OffsetDateTime.now())
    
    // Create MQTT messages
    val personMessage = MqttModels.createPersonMessage(testPerson)
    val plantMessage = MqttModels.createPlantMessage(testPlant)
    val sensorMessage = MqttModels.createSensorMessage(testSensor)
    val sensorResultMessage = MqttModels.createSensorResultMessage(testSensorResult)
    val sensorReadingMessage = MqttModels.createSensorReadingMessage(testSensorResult)
    
    // Test JSON serialization
    println(s"Person MQTT message topic: ${personMessage.topic}")
    println(s"Person JSON: ${MqttModels.toJson(personMessage)}")
    
    println(s"\nPlant MQTT message topic: ${plantMessage.topic}")
    println(s"Plant JSON: ${MqttModels.toJson(plantMessage)}")
    
    println(s"\nSensor MQTT message topic: ${sensorMessage.topic}")
    println(s"Sensor JSON: ${MqttModels.toJson(sensorMessage)}")
    
    println(s"\nSensor Result MQTT message topic: ${sensorResultMessage.topic}")
    println(s"Sensor Result JSON: ${MqttModels.toJson(sensorResultMessage)}")
    
    println(s"\nSensor Reading MQTT message topic: ${sensorReadingMessage.topic}")
    println(s"Sensor Reading JSON: ${MqttModels.toJson(sensorReadingMessage)}")
    
    // Test topic generation
    val dynamicTopic = MqttModels.Topics.sensorResultTopic(1, 1)
    println(s"\nDynamic sensor topic: $dynamicTopic")
    
    // Test JSON parsing
    val jsonString = MqttModels.toJson(personMessage)
    val parsed = MqttModels.fromJson[MqttModels.MqttPerson](jsonString)
    println(s"\nParsed person: $parsed")
    
    println("\n=== MQTT Models Test Complete ===")
  }
}