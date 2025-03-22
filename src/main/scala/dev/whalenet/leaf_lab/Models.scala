package dev.whalenet.leaf_lab

import scalikejdbc.*

import java.time.{OffsetDateTime, ZonedDateTime}

// representation of a Person record
case class Person(id: Int, name: String)
object Person extends SQLSyntaxSupport[Person] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "person"

  def apply(rs: WrappedResultSet): Person = {
    Person(
      rs.int("id"),
      rs.string("name")
    )
  }

  def create(name: String)(implicit s: DBSession = AutoSession): Person = {
    try {
      val insert_query = insert
        .into(Person)
        .columns(
          column.name
        )
        .values(
          name
        )
      val id = insert_query.toSQL.updateAndReturnGeneratedKey.apply().toInt
      Person(id, name)
    }
    catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }
}


case class Plant(id: Int, name: String, plant_type: String, owner_person_id: Int)
object Plant extends SQLSyntaxSupport[Plant] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "plant"

  def apply(rs: WrappedResultSet): Plant = {
    Plant(
      rs.int("id"),
      rs.string("name"),
      rs.string("type"),
      rs.int("person_id")
    )
  }

  def create(name: String, plant_type: String, owner_person_id: Int)(implicit
    s: DBSession = AutoSession
  ): Plant = {
    try {
      val insert_query = insert
        .into(Plant)
        .columns(
          column.name,
          column.plant_type,
          column.owner_person_id
        )
        .values(
          name,
          plant_type,
          owner_person_id
        )
      val id = insert_query.toSQL.updateAndReturnGeneratedKey.apply().toInt
      Plant(id, name, plant_type, owner_person_id)
    }
    catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }
}


case class Sensor(id: Int, name: String, unit: String)
object Sensor extends SQLSyntaxSupport[Sensor] {
  override val schemaName: Option[String] = Some("lab")
  override val tableName: String = "sensor"

  def apply(rs: WrappedResultSet): Sensor = {
    Sensor(
      rs.int("id"),
      rs.string("name"),
      rs.string("unit")
    )
  }

  def create(name: String, unit: String)(implicit
    s: DBSession = AutoSession
  ): Sensor = {
    try {
      val insert_query = insert
        .into(Sensor)
        .columns(
          column.name,
          column.unit
        )
        .values(
          name,
          unit
        )
      val id = insert_query.toSQL.updateAndReturnGeneratedKey.apply().toInt
      Sensor(id, name, unit)
    }
    catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }
}


// Plant_Sensor, but better name
// storing value as string
// TODO - apply column name mapping strategy to other classes
// TODO - apply 
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
