package dev.whalenet.plant_lab

import java.time.ZonedDateTime


// base shared class
abstract class PersonBase {
  def name: String
}

// representation of a Person record
case class Person (val id: Int, val name: String)
  extends PersonBase

// representation of the properties required to create a person
// note: this does not require ID because it's auto assigned
// any route that creates a person should accept a PersonCreate and return a Person
// this is a handy api pattern, unsure if scala friendly
case class PersonCreate(val name: String)
  extends PersonBase


abstract class PlantBase {
  def name: String
  def plant_type: String
  def owner: Person
}

case class Plant (val name: String, val plant_type: String, val owner: Person)
  extends PlantBase


abstract class SensorBase {
  def name: String
  def unit: String
}

case class Sensor(val name: String, val unit: String)
  extends SensorBase


// storing as string
// unsure if I should store in the models file
case class SensorResult(val sensor: Sensor, val plant: Plant, val value: String, val as_of: ZonedDateTime)
