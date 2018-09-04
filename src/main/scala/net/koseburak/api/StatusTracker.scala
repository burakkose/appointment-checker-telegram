package net.koseburak.api

import cats.Monad
import cats.implicits._
import fs2.async.Ref
import net.koseburak.model.Status

sealed trait StatusTracker[F[_]] {
  def empty: F[Unit]
  def error: F[Unit]
  def success: F[Unit]
  def resetAndGetMessage: F[String]
}

class InMemoryStatusTracker[F[_]: Monad](private val ref: Ref[F, Status]) extends StatusTracker[F] {
  def empty: F[Unit] = ref.modify(s => s.copy(empty = s.empty + 1)).void
  def error: F[Unit] = ref.modify(s => s.copy(error = s.error + 1)).void
  def success: F[Unit] = ref.modify(s => s.copy(success = s.success + 1)).void
  def resetAndGetMessage: F[String] =
    for {
      status <- ref.get
      message = s"""
                 |Success: ${status.success}
                 |Empty: ${status.empty}
                 |Error: ${status.error}
                 |Total: ${status.success + status.empty + status.error}
     """.stripMargin
      _ <- ref.modify(_ => Status())
    } yield message
}
