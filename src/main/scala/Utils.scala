import org.apache.commons.text.similarity.LevenshteinDistance
import zio.stream.ZPipeline
import zio.ZIO

val byteToChar: ZPipeline[Any, Nothing, Byte, Char] =
  ZPipeline.map[Byte, Char](_.toChar)

val levenshteinDistance = LevenshteinDistance.getDefaultInstance

def dupes[R, E](maxDistance: Int)(tweets: Seq[Tweet]): ZIO[R, E, Seq[(Tweet, Tweet)]] =
  val dupes = tweets match
    case head :: tail =>
      tail.flatMap:
        pastTweet =>
          val distance = levenshteinDistance(pastTweet.text, head.text)
          Option.when(distance < maxDistance)(head -> pastTweet)
    case _ =>
      Seq.empty

  ZIO.succeed(dupes)