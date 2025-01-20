package dev.whalenet.plant_lab

import scala.collection.mutable

trait SensorResultRepository {
  // todo: add more ways to interact with it, findById, findAll, delete

  def save(result: SensorResult): SensorResult
}

// TODO - for testing
class InMemorySensorResultRepository extends SensorResultRepository {
  private val result_map = mutable.Map[Int, SensorResult]()

  override def save(result: SensorResult): SensorResult = {
    result_map(result.id) = result
    result
  }
}

