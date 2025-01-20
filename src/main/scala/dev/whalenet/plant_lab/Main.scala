package dev.whalenet.plant_lab

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import com.comcast.ip4s._

import org.typelevel.log4cats.slf4j.Slf4jFactory


object Main extends IOApp {

  // not really sure what I'm doing here but this fixed the error without the deprecated method
  implicit def catlogfactory: Slf4jFactory[IO] = Slf4jFactory.create[IO]

  val httpApp: HttpApp[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")
  }.orNotFound

  val PlantLabServer = EmberServerBuilder
    .default[IO]
    .withHost(Host.fromString("0.0.0.0").get)
    .withPort(Port.fromInt(8080).get)
    .withHttpApp(httpApp)
    .build



  def run(args: List[String]): IO[ExitCode] = {
    PlantLabServer
      .use(_ => IO.never) // Keeps the server running indefinitely
      .as(ExitCode.Success)
  }
}
