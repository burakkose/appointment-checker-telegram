package net.koseburak.api

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j._
import fs2._
import fs2.async._
import net.koseburak.model.AppointmentHttpResponse.{AppointmentResponse, EmptyAppointmentResponse}
import net.koseburak.model.{AppConfig, Appointment, Status}
import org.http4s.client.blaze.{BlazeClientConfig, Http1Client}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.higherKinds

sealed trait BotProgram[F[_], G[_[_], _]] {
  def run: G[F, Unit]
}

class StreamingBotProgram[F[_]](config: AppConfig)(implicit E: Effect[F], ec: ExecutionContext)
    extends BotProgram[F, Stream] {
  import config._

  def run: Stream[F, Unit] = Http1Client.stream[F](BlazeClientConfig.insecure).flatMap { client =>
    val streamF = for {
      logger <- Slf4jLogger.fromClass[F](getClass)
      tracker <- Ref(Status()).map(new InMemoryStatusTracker[F](_))
      messenger <- E.delay(new TelegramMessengerApi[F](token.value, chatId, client))
      checker <- E.delay(new GnibAppointmentChecker[F](category.value, subCategory.value, typ.value, client, logger))
    } yield program(messenger, checker, tracker)
    Stream.force(streamF)
  }

  private def program(
      messenger: MessengerApi[F],
      checker: AppointmentChecker[F],
      tracker: StatusTracker[F]
  ): Stream[F, Unit] = {
    val mainS: Stream[F, Unit] =
      every(frequency.value.seconds)
        .evalMap(_ => checker.check)
        .evalMap {
          case AppointmentResponse(slots) =>
            for {
              _ <- tracker.success
              slotsPrettified = prettify(slots)
              message = s"""
                   |$slotsPrettified
                   |Fast access: ${appointmentSystemUri.value}
                 """.stripMargin
              _ <- messenger.sendMessage(message)
            } yield ()
          case _: EmptyAppointmentResponse => tracker.empty
          case other => tracker.error
        }
    val reporterS: Stream[F, Unit] =
      every(reporterFrequency.value.seconds)
        .evalMap(_ => tracker.resetAndGetMessage)
        .evalMap(messenger.sendMessage)
    mainS.merge(reporterS)
  }

  private def prettify(slots: NonEmptyList[Appointment]): String =
    slots.map(_.time).foldLeft("")((acc, time) => acc + time + "\n")

  private def every(d: FiniteDuration): Stream[F, Boolean] =
    Stream
      .every[F](d)
      .filter(identity)

}
