package net.koseburak

import cats.effect.{Effect, IO}
import com.typesafe.config.ConfigFactory
import fs2.StreamApp
import net.koseburak.api.StreamingBotProgram
import net.koseburak.model.AppConfig
import pureconfig.module.catseffect._
import eu.timepit.refined.pureconfig._

import scala.concurrent.ExecutionContext.Implicits.global

object Boot extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    val E = Effect[IO]
    val streamF = for {
      config <- E.delay(ConfigFactory.load())
      appConf <- loadConfigF[IO, AppConfig](config)
      program <- E.delay(new StreamingBotProgram[IO](appConf))
    } yield program.run.last.map(_ => StreamApp.ExitCode.Success)
    fs2.Stream.force(streamF)
  }
}
