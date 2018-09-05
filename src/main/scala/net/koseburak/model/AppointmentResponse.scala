package net.koseburak.model

import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._

final case class Appointment(id: String, time: String)

sealed trait AppointmentHttpResponse
object AppointmentHttpResponse {

  final case class AppointmentResponse(slots: List[Appointment]) extends AppointmentHttpResponse
  final case class EmptyAppointmentResponse(empty: String) extends AppointmentHttpResponse
  final case class ErrorResponse(error: String) extends AppointmentHttpResponse

  implicit val decodeResponse: Decoder[AppointmentHttpResponse] =
    List[Decoder[AppointmentHttpResponse]](
      Decoder[AppointmentResponse].widen,
      Decoder[EmptyAppointmentResponse].widen,
      Decoder[ErrorResponse].widen,
    ).reduceLeft(_ or _)
}
