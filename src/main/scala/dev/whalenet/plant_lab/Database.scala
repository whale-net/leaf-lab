package dev.whalenet.plant_lab

import scalikejdbc.*

object DBConfig {

  private val settings: ConnectionPoolSettings = ConnectionPoolSettings(
    initialSize = 4,
    maxSize = 8,
    connectionTimeoutMillis = 3000L,
    validationQuery = "select 1 as test_col"
  )

  def init(): Unit = {
    val dbUrl = sys.env("DB_URL")
    val dbUser = sys.env("DB_USER")
    val dbPassword = sys.env("DB_PASS")

    ConnectionPool.singleton(dbUrl, dbUser, dbPassword, settings)
    println(s"Connected to database $dbUrl")
  }
}

