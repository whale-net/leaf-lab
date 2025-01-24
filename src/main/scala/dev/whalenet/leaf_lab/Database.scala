package dev.whalenet.leaf_lab

import scalikejdbc.*
import scalikejdbc.config.*

object DBConfig {

  private val settings: ConnectionPoolSettings = ConnectionPoolSettings(
    initialSize = 4,
    maxSize = 8,
    connectionTimeoutMillis = 3000L,
  )

  def init(): Unit = {
    val dbUrl = sys.env("DB_URL")
    val dbUser = sys.env("DB_USER")
    val dbPassword = sys.env("DB_PASS")

    ConnectionPool.singleton(dbUrl, dbUser, dbPassword, settings)
    DBs.setupAll()
    println(s"Connected to database $dbUrl")
  }
}

/*
/*
Default SQL configuration

Used to create initial version. Probably should have migrations somewhere, but this is what it is for now.
 */

create table if not exists lab.person
(
    id          serial
        constraint person_pk
            primary key,
    keycloak_id varchar,
    name        varchar                                                  not null
);

alter table lab.person
    owner to leaf_lab_owner;

create table if not exists lab.plant
(
    id        serial
        constraint plant_pk
            primary key,
    person_id integer not null
        constraint plant_person_id_fk
            references lab.person,
    name      varchar not null,
    type      varchar not null
);

alter table lab.plant
    owner to leaf_lab_owner;

create index if not exists plant_person_id_index
    on lab.plant (person_id);

create table if not exists lab.sensor
(
    id   serial
        constraint sensor_pk
            primary key,
    name varchar not null,
    unit varchar not null
);

alter table lab.sensor
    owner to leaf_lab_owner;

create table if not exists lab.plant_sensor
(
    id        serial
        constraint plant_sensor_pk
            primary key,
    plant_id  integer   not null
        constraint plant_sensor_plant_id_fk
            references lab.plant,
    sensor_id integer   not null
        constraint plant_sensor_sensor_id_fk
            references lab.sensor,
    as_of     timestamp not null,
    value     varchar   not null
);

alter table lab.plant_sensor
    owner to leaf_lab_owner;

create index if not exists plant_sensor_plant_id_index
    on lab.plant_sensor (plant_id);

create index if not exists plant_sensor_sensor_id_index
    on lab.plant_sensor (sensor_id);
 */
