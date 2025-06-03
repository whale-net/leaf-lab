# MQTT Integration

This project now supports MQTT communication alongside the existing HTTP API.

## Environment Configuration

To enable MQTT support, set the following environment variables:

```bash
# Enable MQTT support
MQTT_ENABLED=true

# MQTT broker connection
MQTT_BROKER_URL=tcp://localhost:1883
MQTT_CLIENT_ID=leaf-lab-client-123
MQTT_USERNAME=username    # Optional
MQTT_PASSWORD=password    # Optional

# MQTT connection settings (all optional)
MQTT_CLEAN_SESSION=true
MQTT_CONNECTION_TIMEOUT=10
MQTT_KEEP_ALIVE_INTERVAL=20
MQTT_MAX_RECONNECT_DELAY=128000
MQTT_AUTOMATIC_RECONNECT=true
```

## MQTT Topics

The system publishes to and subscribes from the following MQTT topics:

### Entity Creation Topics
- `leaf-lab/person/created` - When a person is created
- `leaf-lab/plant/created` - When a plant is created  
- `leaf-lab/sensor/created` - When a sensor is created
- `leaf-lab/sensor-result/created` - When a sensor result is created

### Real-time Sensor Readings
- `leaf-lab/plant/{plant_id}/sensor/{sensor_id}/reading` - Real-time sensor data

## MQTT Message Format

All MQTT messages contain the original model data wrapped with a unique message ID:

```json
{
  "person": {
    "id": 1,
    "name": "John Doe"
  },
  "messageId": "uuid-string"
}
```

## Testing MQTT Models

Run the MQTT test to validate model serialization:

```bash
sbt "runMain dev.whalenet.leaf_lab.MqttTest"
```

## Running with MQTT Support

1. **HTTP Only** (default):
   ```bash
   sbt run
   ```

2. **HTTP + MQTT**:
   ```bash
   MQTT_ENABLED=true MQTT_BROKER_URL=tcp://localhost:1883 sbt run
   ```

## Architecture

The MQTT integration maintains the existing models exactly as they were, wrapping them in MQTT-specific containers. This ensures minimal changes to the current codebase while adding MQTT capabilities.

- **MqttModels.scala** - MQTT wrappers and topic definitions
- **MqttConfig.scala** - MQTT configuration from environment
- **MqttService.scala** - MQTT client management and message handling  
- **MqttEnabledService.scala** - Enhanced service with MQTT publishing
- **Main.scala** - Updated to support both HTTP-only and HTTP+MQTT modes