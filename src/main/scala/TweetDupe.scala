import org.apache.commons.text.similarity.LevenshteinDistance
import zio.{App, Chunk, Schedule, ZIO, console}
import zio.stream.{ZStream, ZTransducer}
import zio.json.*

import java.io.{File, FileNotFoundException}

case class Tweet(id: BigInt, text: String, date: String)

object Tweet:
  implicit val decoder: JsonDecoder[Tweet] = DeriveJsonDecoder.gen[Tweet]

object TweetDupe extends App:

  val numToKeep = 999
  val maxDistance = 10

  def app(file: File) =
    ZStream.fromFile(file.toPath)
      .transduce(ZTransducer.utf8Decode >>> stringToChars >>> JsonDecoder[Tweet].decodeJsonTransducer())
      .filter(_.text.size > 50)
      .scan(Seq.empty[Tweet])(_.take(numToKeep).prepended(_))
      .mapMPar(16)(dupes(maxDistance))
      .flattenIterables
      .foreach(dupe => console.putStrLn(dupe.toString))

  override def run(args: List[String]) =
    val exec = for
      file <- ZIO.fromOption(args.headOption).map(File(_)).filterOrDieMessage(_.exists())("Specify a valid file")
      _ <- app(file)
    yield ()

    exec.exitCode