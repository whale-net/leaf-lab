package dev.whalenet.leaf_lab

import scalikejdbc.*

import java.time.{OffsetDateTime, ZonedDateTime}

// representation of a Person record
case class Person(id: Int, name: String)

case class Plant(id: Int, name: String, plant_type: String, owner_person_id: Int)

case class Sensor(id: Int, name: String, unit: String)

// Plant_Sensor, but better name
// storing value as string
case class SensorResult(
  id: Int,
  plant_id: Int,
  sensor_id: Int,
  value: String,
  as_of: OffsetDateTime,
)
object SensorResult extends SQLSyntaxSupport[SensorResult] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "plant_sensor"

  def apply(rs: WrappedResultSet): SensorResult = {
    SensorResult(
      rs.int("id"),
      rs.int("plant_id"),
      rs.int("sensor_id"),
      rs.string("value"),
      rs.offsetDateTime("as_of"),
    )
  }

  // TODO should database code live with my models? Tables and schema mappings seme nice
  // but the actual implementation seems extreme.
  // maybe everything should be combined together anyways
  def create(plant_id: Int, sensor_id: Int, value: String)(implicit
    s: DBSession = AutoSession,
  ): SensorResult = {

    // NOTE: overriding the as_of
    val now = OffsetDateTime.now()

    // todo big T Try
    try {
      // sql"insert into lab.plant_sensor (plant_id, sensor_id, value, as_of) values (1, 1, '100.00', '2025-01-20')".update.apply()
      val insert_query = insert
        .into(SensorResult)
        .columns(
          column.plant_id,
          column.sensor_id,
          column.value,
          column.as_of,
        )
        .values(
          plant_id,
          sensor_id,
          value,
          now,
        )
      val id = insert_query.toSQL.updateAndReturnGeneratedKey.apply().toInt
      SensorResult(id, plant_id, sensor_id, value, now)
    }
    catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }
}
