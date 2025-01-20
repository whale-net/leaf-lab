package dev.whalenet.plant_lab

import java.time.ZonedDateTime



// representation of a Person record
case class Person (val id: Int, val name: String)

case class Plant (val id: Int, val name: String, val plant_type: String, val owner_person_id: Int)

case class Sensor(val id: Int, val name: String, val unit: String)


// Plant_Sensor, but better name
// storing value as string
case class SensorResult(val id: Int, val plant_id: Int, val sensor_id: Int, val value: String, val as_of: ZonedDateTime)
