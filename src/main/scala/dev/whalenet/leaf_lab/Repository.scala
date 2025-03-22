package dev.whalenet.leaf_lab

import scala.collection.mutable
import scalikejdbc.{insert => sqlInsert, select => sqlSelect, *}
import java.time.OffsetDateTime

trait Repository[T] {
  def insert(entity: T): T
  def get(id: Int): Option[T] 
  // def delete(id: Int): Unit
}

trait DBRepository[T] extends Repository[T] {
  // Override base method to delegate to session-aware method
  override def insert(entity: T): T = insertWithSession(entity)

  // Implementation with session parameter
  def insertWithSession(entity: T)(implicit s: DBSession = AutoSession): T

  override def get(id: Int): Option[T] = { 
    getWithSession(id)
  }

  def getWithSession(id: Int)(implicit s: DBSession = AutoSession): Option[T] 

  // override def delete(id: Int): Unit = {
  //   deleteWithSession(id)
  // }

  // def deleteWithSession(id: Int)(implicit s: DBSession = AutoSession)
}

// TODO - use for testing
//class InMemorySensorResultRepository extends SensorResultRepository {
//  private val result_map = mutable.Map[Int, SensorResult]()
//
//  override def save(result: SensorResult): SensorResult = {
//    result_map(result.id) = result
//    result
//  }
//}

class DBSensorResultRepository extends DBRepository[SensorResult] {
  override def insertWithSession(result: SensorResult)(implicit
    s: DBSession = AutoSession,
  ): SensorResult = {
    if result.id > 0 then
      throw RuntimeException("cannot update yet")

    // NOTE: overriding the as_of
    val now = OffsetDateTime.now()

    // todo big T Try
    try {
      // sql"insert into lab.plant_sensor (plant_id, sensor_id, value, as_of) values (1, 1, '100.00', '2025-01-20')".update.apply()
      val insert_query = sqlInsert
        .into(SensorResult)
        .columns(
          SensorResult.column.plant_id,
          SensorResult.column.sensor_id,
          SensorResult.column.value,
          SensorResult.column.as_of,
        )
        .values(
          result.plant_id,
          result.sensor_id,
          result.value,
          now,
        )
      val id = insert_query.toSQL.updateAndReturnGeneratedKey.apply().toInt
      SensorResult(id, result.plant_id, result.sensor_id, result.value, now)
    }
    catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }

  override def getWithSession(id: Int)(implicit s: DBSession = AutoSession): Option[SensorResult] = { 
    try {
      val sr = SensorResult.syntax("sr")
      val maybeResult = withSQL {
        sqlSelect
          .from(SensorResult as sr)
          .where
          .eq(SensorResult.column.id, id)
      }.map(SensorResult(sr)).single.apply()
      maybeResult
    } catch {
      case e: Exception =>
        println(s"Error finding SensorResult with id $id: ${e.getMessage}")
        None
    }
  }
}

class DBSensorRepository extends DBRepository[Sensor] {
  override def insertWithSession(sensor: Sensor)(implicit s: DBSession = AutoSession): Sensor = {
    if sensor.id > 0 then
      throw RuntimeException("cannot update yet")

    try {
      val insertQuery = sqlInsert
        .into(Sensor)
        .columns(
          Sensor.column.name,
          Sensor.column.unit
        )
        .values(
          sensor.name,
          sensor.unit
        )
      val id = insertQuery.toSQL.updateAndReturnGeneratedKey.apply().toInt
      Sensor(id, sensor.name, sensor.unit)
    } catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }

  override def getWithSession(id: Int)(implicit s: DBSession = AutoSession): Option[Sensor] = { 
    try {
      val sr = Sensor.syntax("sr")
      withSQL {
        sqlSelect
          .from(Sensor as sr)
          .where
          .eq(Sensor.column.id, id)
      }.map(rs => Sensor(rs)).single.apply()
    } catch {
      case e: Exception =>
        println(s"Error finding Sensor with id $id: ${e.getMessage}")
        None
    }
  }
}

class DBPlantRepository extends DBRepository[Plant] {
  override def insertWithSession(plant: Plant)(implicit s: DBSession = AutoSession): Plant = {
    if plant.id > 0 then
      throw RuntimeException("cannot update yet")

    try {
      val insertQuery = sqlInsert
        .into(Plant)
        .columns(
          Plant.column.name,
          Plant.column.plant_type,
          Plant.column.owner_person_id,
        )
        .values(
          plant.name,
          plant.plant_type,
          plant.owner_person_id,
        )
      val id = insertQuery.toSQL.updateAndReturnGeneratedKey.apply().toInt
      Plant(id, plant.name, plant.plant_type, plant.owner_person_id)
    } catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }

  override def getWithSession(id: Int)(implicit s: DBSession = AutoSession): Option[Plant] = { 
    try {
      val p = Plant.syntax("p")
      withSQL {
        sqlSelect
          .from(Plant as p)
          .where
          .eq(Plant.column.id, id)
      }.map(rs => Plant(rs)).single.apply()
    } catch {
      case e: Exception =>
        println(s"Error finding Plant with id $id: ${e.getMessage}")
        None
    }
  }
}

class DBPersonRepository extends DBRepository[Person] {
  override def insertWithSession(person: Person)(implicit s: DBSession = AutoSession): Person = {
    if person.id > 0 then
      throw RuntimeException("cannot update yet")

    try {
      val insertQuery = sqlInsert
        .into(Person)
        .columns(
          Person.column.name
        )
        .values(
          person.name
        )
      val id = insertQuery.toSQL.updateAndReturnGeneratedKey.apply().toInt
      Person(id, person.name)
    } catch {
      case e: Exception =>
        println(s"Error during SQL execution: ${e.getMessage}")
        throw e
    }
  }

  override def getWithSession(id: Int)(implicit s: DBSession = AutoSession): Option[Person] = { 
    try {
      val p = Person.syntax("p")
      withSQL {
        sqlSelect
          .from(Person as p)
          .where
          .eq(Person.column.id, id)
      }.map(rs => Person(rs)).single.apply()
    } catch {
      case e: Exception =>
        println(s"Error finding Person with id $id: ${e.getMessage}")
        None
    }
  }
}

