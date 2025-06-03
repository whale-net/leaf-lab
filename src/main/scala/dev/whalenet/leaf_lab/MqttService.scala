package dev.whalenet.leaf_lab

import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import org.eclipse.paho.mqttv5.client.*
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
import org.eclipse.paho.mqttv5.common.{MqttMessage => PahoMqttMessage, MqttException}
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import java.nio.charset.StandardCharsets

trait MqttService[F[_]] {
  def publish[T](message: MqttModels.MqttMessageWrapper[T])(implicit encoder: io.circe.Encoder[T]): F[Unit]
  def subscribe(topic: String, callback: (String, String) => F[Unit]): F[Unit]
  def close(): F[Unit]
}

class MqttServiceImpl[F[_]: Async](
  config: MqttConfig,
  sensorResultRepository: Repository[SensorResult],
  sensorRepository: Repository[Sensor],
  plantRepository: Repository[Plant],
  personRepository: Repository[Person]
) extends MqttService[F] {

  private val persistence = new MemoryPersistence()
  private var clientOpt: Option[MqttAsyncClient] = None

  private def getClient: F[MqttAsyncClient] = {
    Async[F].blocking {
      clientOpt match {
        case Some(client) if client.isConnected => client
        case _ =>
          val client = new MqttAsyncClient(config.brokerUrl, config.clientId, persistence)
          val connOpts = new MqttConnectionOptions()
          connOpts.setCleanStart(config.cleanSession)
          connOpts.setConnectionTimeout(config.connectionTimeout)
          connOpts.setKeepAliveInterval(config.keepAliveInterval)
          connOpts.setMaxReconnectDelay(config.maxReconnectDelay)
          connOpts.setAutomaticReconnect(config.automaticReconnect)
          
          config.username.foreach(connOpts.setUserName)
          config.password.foreach(p => connOpts.setPassword(p.getBytes(StandardCharsets.UTF_8)))
          
          // Set callback for incoming messages
          client.setCallback(new MqttCallback {
            override def disconnected(disconnectResponse: MqttDisconnectResponse): Unit = {
              println(s"MQTT client disconnected: ${disconnectResponse.getReasonString}")
            }
            
            override def mqttErrorOccurred(exception: MqttException): Unit = {
              println(s"MQTT error occurred: ${exception.getMessage}")
            }
            
            override def messageArrived(topic: String, message: PahoMqttMessage): Unit = {
              handleIncomingMessage(topic, new String(message.getPayload, StandardCharsets.UTF_8))
            }
            
            override def deliveryComplete(token: IMqttToken): Unit = {
              // Message delivery completed
            }
            
            override def connectComplete(reconnect: Boolean, serverURI: String): Unit = {
              println(s"MQTT client connected to $serverURI, reconnect: $reconnect")
            }
            
            override def authPacketArrived(reasonCode: Int, properties: MqttProperties): Unit = {
              // Auth packet arrived
            }
          })
          
          client.connect(connOpts).waitForCompletion()
          clientOpt = Some(client)
          client
      }
    }
  }

  override def publish[T](message: MqttModels.MqttMessageWrapper[T])(implicit encoder: io.circe.Encoder[T]): F[Unit] = {
    for {
      client <- getClient
      json = message.payload.asJson.noSpaces
      mqttMessage = new PahoMqttMessage(json.getBytes(StandardCharsets.UTF_8))
      _ = mqttMessage.setQos(message.qos)
      _ <- Async[F].blocking {
        client.publish(message.topic, mqttMessage).waitForCompletion()
        println(s"Published MQTT message to topic: ${message.topic}")
      }
    } yield ()
  }

  override def subscribe(topic: String, callback: (String, String) => F[Unit]): F[Unit] = {
    for {
      client <- getClient
      _ <- Async[F].blocking {
        client.subscribe(topic, 1).waitForCompletion()
        println(s"Subscribed to MQTT topic: $topic")
      }
    } yield ()
  }

  override def close(): F[Unit] = {
    Async[F].blocking {
      clientOpt.foreach { client =>
        if (client.isConnected) {
          client.disconnect().waitForCompletion()
        }
        client.close()
      }
      clientOpt = None
      println("MQTT client closed")
    }
  }

  // Handle incoming MQTT messages
  private def handleIncomingMessage(topic: String, payload: String): Unit = {
    println(s"Received MQTT message on topic: $topic, payload: $payload")
    
    // Parse and handle different message types based on topic
    try {
      topic match {
        case t if t.startsWith("leaf-lab/sensor-result/") =>
          decode[MqttModels.MqttSensorResult](payload) match {
            case Right(mqttSensorResult) =>
              // Insert sensor result into database
              val result = sensorResultRepository.insert(mqttSensorResult.sensorResult)
              println(s"Inserted sensor result from MQTT: $result")
            case Left(error) =>
              println(s"Error parsing sensor result from MQTT: $error")
          }
        
        case t if t.startsWith("leaf-lab/person/") =>
          decode[MqttModels.MqttPerson](payload) match {
            case Right(mqttPerson) =>
              val result = personRepository.insert(mqttPerson.person)
              println(s"Inserted person from MQTT: $result")
            case Left(error) =>
              println(s"Error parsing person from MQTT: $error")
          }
        
        case t if t.startsWith("leaf-lab/plant/") =>
          decode[MqttModels.MqttPlant](payload) match {
            case Right(mqttPlant) =>
              val result = plantRepository.insert(mqttPlant.plant)
              println(s"Inserted plant from MQTT: $result")
            case Left(error) =>
              println(s"Error parsing plant from MQTT: $error")
          }
        
        case t if t.startsWith("leaf-lab/sensor/") =>
          decode[MqttModels.MqttSensor](payload) match {
            case Right(mqttSensor) =>
              val result = sensorRepository.insert(mqttSensor.sensor)
              println(s"Inserted sensor from MQTT: $result")
            case Left(error) =>
              println(s"Error parsing sensor from MQTT: $error")
          }
        
        case _ =>
          println(s"Unknown topic pattern: $topic")
      }
    } catch {
      case e: Exception =>
        println(s"Error handling MQTT message: ${e.getMessage}")
    }
  }
}

object MqttService {
  def apply[F[_]: Async](
    config: MqttConfig,
    sensorResultRepository: Repository[SensorResult],
    sensorRepository: Repository[Sensor],
    plantRepository: Repository[Plant],
    personRepository: Repository[Person]
  ): MqttService[F] = new MqttServiceImpl[F](config, sensorResultRepository, sensorRepository, plantRepository, personRepository)
}