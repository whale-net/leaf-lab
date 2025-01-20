package dev.whalenet.plant_lab

// for now, one universal service with everything
// maybe forever too
class Service(sensorResultRepository: SensorResultRepository) {
  def createSensorResult(result: SensorResult): SensorResult = sensorResultRepository.save(result)
}
