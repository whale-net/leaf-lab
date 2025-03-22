package dev.whalenet.leaf_lab

import scala.collection.mutable
import scalikejdbc.{insert => sqlInsert, *}
import java.time.OffsetDateTime

trait Repository[T] {
  def insert(entity: T): T
  def findById(id: Int): Option[T]
}

trait DBRepository[T] extends Repository[T] {
  // Override base method to delegate to session-aware method
  override def insert(entity: T): T = insertWithSession(entity)

  // Implementation with session parameter
  def insertWithSession(entity: T)(implicit s: DBSession = AutoSession): T
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

    override def findById(id: Int): Option[SensorResult] = {
        // todo
        None
    }
}