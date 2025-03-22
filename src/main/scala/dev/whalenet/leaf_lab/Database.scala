package dev.whalenet.leaf_lab

import scalikejdbc.*
import scalikejdbc.config.*

object DBConfig {

  private val settings: ConnectionPoolSettings = ConnectionPoolSettings(
    initialSize = 4,
    maxSize = 8,
    connectionTimeoutMillis = 3000L,
    warmUpTime = 10L, // Warmup connections
    timeZone = "UTC"
  )

  def init(): Unit = {
    val dbUrl = sys.env("DB_URL")
    val dbUser = sys.env("DB_USER")
    val dbPassword = sys.env("DB_PASS")

    // Configure HikariCP properties using system properties
    System.setProperty("hikaricp.dataSource.cachePrepStmts", "true")
    System.setProperty("hikaricp.dataSource.prepStmtCacheSize", "250")
    System.setProperty("hikaricp.dataSource.prepStmtCacheSqlLimit", "2048")
    System.setProperty("hikaricp.dataSource.useServerPrepStmts", "true")
    System.setProperty("hikaricp.dataSource.idleTimeout", "30000") // 30 seconds idle timeout
    System.setProperty("hikaricp.dataSource.maxLifetime", "1800000") // 30 minutes max connection lifetime
    System.setProperty("hikaricp.dataSource.leakDetectionThreshold", "60000") // 60 seconds for leak detection
    System.setProperty("hikaricp.aliveConnectionTimeout", "30000") // 30 seconds keepalive
    System.setProperty("hikaricp.autoCommit", "true")

    ConnectionPool.singleton(dbUrl, dbUser, dbPassword, settings)
    DBs.setupAll()
    println(s"Connected to database $dbUrl")
  }
}

/*
Default SQL configuration

Used to create initial version. Probably should have migrations somewhere, but this is what it is for now.

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
    owner_person_id integer not null
        constraint plant_person_id_fk
            references lab.person,
    name      varchar not null,
    plant_type      varchar not null
);

alter table lab.plant
    owner to leaf_lab_owner;

create index if not exists plant_person_id_index
    on lab.plant (owner_person_id);

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

-- assumes ID=1 for now. Could improve, but not doing until needed
insert into lab.person(keycloak_id, name) VALUES ('fake-value', 'John Testerman');
insert into lab.sensor(name, unit) VALUES ('John''s cold hand', 'feels');
insert into lab.plant(owner_person_id, name, plant_type) VALUES (1, 'unfortunate aloe', 'aloe');

 */
