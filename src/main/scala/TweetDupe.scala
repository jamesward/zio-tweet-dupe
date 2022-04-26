import zio.{Console, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.stream.ZStream
import zio.json.*

import java.io.{File, FileNotFoundException}

case class Tweet(id: BigInt, text: String, date: String)

object Tweet:
  implicit val decoder: JsonDecoder[Tweet] = DeriveJsonDecoder.gen[Tweet]

object TweetDupe extends ZIOAppDefault:

  val numToKeep = 999
  val maxDistance = 10

  def app(file: File) =
    ZStream.fromFile(file)
      .via(byteToChar >>> JsonDecoder[Tweet].decodeJsonPipeline())
      .filter(_.text.length > 50)
      .scan(Seq.empty[Tweet])(_.take(numToKeep).prepended(_))
      .mapZIOPar(16)(dupes(maxDistance))
      .flattenIterables
      .foreach(dupe => Console.printLine(dupe.toString))

      /*
      .scan(0):
        case (num, (tweet1, tweet2)) =>
          num + 1
      .foreach(n => Console.printLine(s"Num dupes = $n"))
      */

  def run =
    for
      args <- ZIOAppArgs.getArgs
      file <- ZIO.fromOption(args.headOption).map(File(_)).filterOrDieMessage(_.exists())("Specify a valid file")
      _ <- app(file)
    yield ()
