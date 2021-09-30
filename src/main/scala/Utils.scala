import org.apache.commons.text.similarity.LevenshteinDistance
import zio.{Chunk, ZIO}
import zio.stream.ZTransducer

def stringToChars: ZTransducer[Any, Nothing, String, Char] =
  ZTransducer
    .fromFunction[String, Chunk[Char]](s => Chunk.fromArray(s.toCharArray))
    .mapChunks(_.flatten)

val levenshteinDistance = LevenshteinDistance.getDefaultInstance

def dupes[R, E](maxDistance: Int)(tweets: Seq[Tweet]): ZIO[R, E, Seq[(Tweet, Tweet)]] =
  val dupes = tweets match
    case head :: tail =>
      tail.flatMap:
        pastTweet =>
          val distance = levenshteinDistance.apply(pastTweet.text, head.text)
          Option.when(distance < maxDistance)(head -> pastTweet)
    case _ =>
      Seq.empty

  ZIO.succeed(dupes)