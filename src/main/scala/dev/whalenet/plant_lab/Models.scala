package dev.whalenet.plant_lab

import scalikejdbc.*

import java.time.ZonedDateTime


// representation of a Person record
case class Person (val id: Int, val name: String)

case class Plant (val id: Int, val name: String, val plant_type: String, val owner_person_id: Int)

case class Sensor(val id: Int, val name: String, val unit: String)


// Plant_Sensor, but better name
// storing value as string
case class SensorResult(val id: Int, val plant_id: Int, val sensor_id: Int, val value: String, val as_of: ZonedDateTime)
object SensorResult extends SQLSyntaxSupport[SensorResult] {
  override val schemaName: Option[String] = Some("plab")
  override val tableName: String = "plant_sensor"

  def apply(rs: WrappedResultSet): SensorResult = {
    SensorResult(
      rs.int("id"),
      rs.int("plant_id"),
      rs.int("sensor_id"),
      rs.string("value"),
      rs.zonedDateTime("as_of")
    )
  }

  // TODO should database code live with my models? Tables and schema mappings seme nice
  // but the actual implementation seems extreme.
  // maybe everything should be combined together anyways
  def create(plant_id: Int, sensor_id: Int, value: String)
            (implicit s: DBSession = AutoSession): SensorResult = {
    // NOTE: overriding the as_of
    val now = ZonedDateTime.now()
    val id =
      sql"""
        insert into $table (plant_id, sensor_id, value, as_of)
        values ($plant_id, $sensor_id, $value, '2025-01-20T14:37:51.528214229')
        """
        .updateAndReturnGeneratedKey.apply().toInt

    println(s"id=${id}")
    SensorResult(id, plant_id, sensor_id, value, now)
  }
}