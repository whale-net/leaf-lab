package dev.whalenet.plant_lab

import scala.collection.mutable
import scalikejdbc.*

trait SensorResultRepository {
  // todo: add more ways to interact with it, findById, findAll, delete

  def save(result: SensorResult): SensorResult
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

class DBSensorResultRepository extends SensorResultRepository {
  override def save(result: SensorResult): SensorResult = {
    if result.id > 0 then
      throw RuntimeException("cannot update yet")

    // todo there is almost certainly a better way to do this with this library
    return SensorResult.create(result.plant_id, result.sensor_id, result.value)
  }
}