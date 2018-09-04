package net.koseburak.api

import cats.effect.Sync
import org.http4s.Uri
import org.http4s.client.Client

sealed trait MessengerApi[F[_]] {

  /**
   * Send a message
   */
  def sendMessage(message: String): F[Unit]
}

class TelegramMessengerApi[F[_]](token: String, chatId: Long, client: Client[F])(implicit F: Sync[F])
    extends MessengerApi[F] {

  private val base: Uri = Uri.uri("https://api.telegram.org") / s"bot$token"

  /**
   * Send a message
   */
  def sendMessage(message: String): F[Unit] = {
    val uri = base / "sendMessage" =? Map(
      "chat_id" -> Seq(chatId.toString),
      "parse_mode" -> Seq("Markdown"),
      "text" -> Seq(message)
    )

    client.expect[Unit](uri)
  }
}
