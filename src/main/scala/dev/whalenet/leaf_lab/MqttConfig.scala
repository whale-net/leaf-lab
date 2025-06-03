package dev.whalenet.leaf_lab

// MQTT configuration
case class MqttConfig(
  brokerUrl: String,
  clientId: String,
  username: Option[String] = None,
  password: Option[String] = None,
  cleanSession: Boolean = true,
  connectionTimeout: Int = 10, // seconds
  keepAliveInterval: Int = 20, // seconds
  maxReconnectDelay: Int = 128000, // milliseconds
  automaticReconnect: Boolean = true
)

object MqttConfig {
  def fromEnvironment(): MqttConfig = {
    MqttConfig(
      brokerUrl = sys.env.getOrElse("MQTT_BROKER_URL", "tcp://localhost:1883"),
      clientId = sys.env.getOrElse("MQTT_CLIENT_ID", s"leaf-lab-${java.util.UUID.randomUUID().toString.take(8)}"),
      username = sys.env.get("MQTT_USERNAME"),
      password = sys.env.get("MQTT_PASSWORD"),
      cleanSession = sys.env.get("MQTT_CLEAN_SESSION").map(_.toBoolean).getOrElse(true),
      connectionTimeout = sys.env.get("MQTT_CONNECTION_TIMEOUT").map(_.toInt).getOrElse(10),
      keepAliveInterval = sys.env.get("MQTT_KEEP_ALIVE_INTERVAL").map(_.toInt).getOrElse(20),
      maxReconnectDelay = sys.env.get("MQTT_MAX_RECONNECT_DELAY").map(_.toInt).getOrElse(128000),
      automaticReconnect = sys.env.get("MQTT_AUTOMATIC_RECONNECT").map(_.toBoolean).getOrElse(true)
    )
  }
}