package com.pwos.products

import scala.concurrent.duration.DurationInt

import cats.data.NonEmptyList
import cats.effect.IO
import cats.effect.Ref
import cats.effect.unsafe.IORuntime
import cats.syntax.all.*
import fs2.Stream

object Test extends App {

  implicit lazy val ioRuntime: IORuntime = IORuntime.global

  type ProductCandidate = String

  val ChunkSize: Long   = 1000
  val AllElements: Long = 90_000_000

  def getProductCandidates(offset: Long): Option[NonEmptyList[ProductCandidate]] = {
    if (offset < AllElements) {
      Thread.sleep(1000)
      NonEmptyList.of(offset.toString, (offset + 1).toString, "...").some
    } else {
      none
    }
  }

  def actionForChunk(value: NonEmptyList[Test.ProductCandidate]): Unit = {
    println(s"action for chunk which starts with ${value.head}")
  }

  val processedRef: IO[Ref[IO, Long]] = Ref.of[IO, Long](0)

  val app: IO[Unit] = processedRef.flatMap { processedCounter =>
    val statsStream: Stream[IO, Unit] = Stream
      .fixedDelay[IO](5.seconds)
      .evalMap { _ =>
        for {
          processed <- processedCounter.get
          _         <- IO.println(f"Processed $processed from $AllElements [${processed / AllElements.toDouble}%.4f%%]")
        } yield ()
      }

    val actionStream: Stream[IO, Unit] = Stream
      .unfoldEval(0L) { offset: Long =>
        println(s"offset = $offset")

        getProductCandidates(offset)
          .map { nonEmptyCandidates =>
            processedCounter
              .update(_ + ChunkSize)
              .as((actionForChunk(nonEmptyCandidates), offset + ChunkSize))
              .map(_.some)
          }
          .getOrElse(IO.pure(none))
      }

    actionStream.merge(statsStream).compile.drain
  }

  app.unsafeRunSync()
}
