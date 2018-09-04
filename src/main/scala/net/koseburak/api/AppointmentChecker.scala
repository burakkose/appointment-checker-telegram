package net.koseburak.api

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import net.koseburak.model.AppointmentHttpResponse
import net.koseburak.model.AppointmentHttpResponse.ErrorResponse
import org.http4s.Uri
import org.http4s.circe._
import org.http4s.client.Client

sealed trait AppointmentChecker[F[_]] {

  /**
   * Check available appointments
   */
  def check: F[AppointmentHttpResponse]
}

class GnibAppointmentChecker[F[_]: Sync](
    category: String,
    subCategory: String,
    typ: String,
    client: Client[F],
    logger: Logger[F]
) extends AppointmentChecker[F] {

  private val base = Uri.uri("https://burghquayregistrationoffice.inis.gov.ie/Website/AMSREG/AMSRegWeb.nsf/(getAppsNear)")
  private val query = base.setQueryParams(
    Map(
      "openpage" -> Seq(""),
      "cat" -> Seq(category),
      "sbcat" -> Seq(subCategory),
      "typ" -> Seq(typ)
    )
  )

  /**
   * Check available appointments
   */
  def check: F[AppointmentHttpResponse] =
    client
      .expect(query)(jsonOf[F, AppointmentHttpResponse])
      .flatMap { response =>
        logger.info(response.toString).map(_ => response)
      }
      .recoverWith {
        case ex =>
          logger.error(ex)("Request failed.").map(_ => ErrorResponse(ex.getMessage))
      }

}
