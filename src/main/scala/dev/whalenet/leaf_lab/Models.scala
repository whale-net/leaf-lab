package dev.whalenet.leaf_lab

import scalikejdbc.*

import java.time.{OffsetDateTime, ZonedDateTime}

// representation of a Person record
case class Person(id: Int, name: String)
object Person extends SQLSyntaxSupport[Person] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "person"

  def apply(sp: SyntaxProvider[Person])(rs: WrappedResultSet): Person = apply(sp.resultName)(rs)
  def apply(rn: ResultName[Person])(rs: WrappedResultSet): Person = {
    Person(
      rs.int(rn.id),
      rs.string(rn.name)
    )
  }
}


case class Plant(id: Int, name: String, plant_type: String, owner_person_id: Int)
object Plant extends SQLSyntaxSupport[Plant] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "plant"

  def apply(sp: SyntaxProvider[Plant])(rs: WrappedResultSet): Plant = apply(sp.resultName)(rs)
  def apply(rn: ResultName[Plant])(rs: WrappedResultSet): Plant = {
    Plant(
      rs.int(rn.id),
      rs.string(rn.name),
      rs.string(rn.plant_type),
      rs.int(rn.owner_person_id)
    )
  }
}


case class Sensor(id: Int, name: String, unit: String)
object Sensor extends SQLSyntaxSupport[Sensor] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "sensor"

  def apply(sp: SyntaxProvider[Sensor])(rs: WrappedResultSet): Sensor = apply(sp.resultName)(rs)
  def apply(rn: ResultName[Sensor])(rs: WrappedResultSet): Sensor = {
    Sensor(
      rs.int(rn.id),
      rs.string(rn.name),
      rs.string(rn.unit)
    )
  }
}


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

  def apply(sp: SyntaxProvider[SensorResult])(rs: WrappedResultSet): SensorResult = apply(sp.resultName)(rs)
  def apply(rn: ResultName[SensorResult])(rs: WrappedResultSet): SensorResult = {
    SensorResult(
      rs.int(rn.id),
      rs.int(rn.plant_id),
      rs.int(rn.sensor_id),
      rs.string(rn.value),
      rs.offsetDateTime(rn.as_of),
    )
  }
}
