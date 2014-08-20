package models

import play.api.libs.json.Json


/*
 * Created by domingo on 08/08/14.
 */
case class Tweet(id: Option[BSONObjectID], Text: String, PosiVote: Int, NeuVote: Int, NegVote: Int, User: Option[String])

object Tweet {
  /** serialize/Deserialize a Tweet into/from JSON value */
  implicit val tweetFormat = Json.format[Tweet]

  /** serialize a Tweet into a BSON */
  implicit object TweetBSONWriter extends BSONDocumentWriter[Tweet] {
    def write(tweet: Tweet): BSONDocument =
      BSONDocument(
        "_id" -> tweet.id.getOrElse(BSONObjectID.generate),
        "Text" -> tweet.Text,
        "PosiVote" -> tweet.PosiVote,
        "NeuVote" -> tweet.NeuVote,
        "NegVote" -> tweet.NegVote,
        "User" -> tweet.User)
  }

  /** deserialize a Tweet from a BSON */
  implicit object TweetBSONReader extends BSONDocumentReader[Tweet] {
    def read(doc: BSONDocument): Tweet = {
      Tweet(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("Text").get,
        doc.getAs[Int]("PosiVote").get,
        doc.getAs[Int]("NeuVote").get,
        doc.getAs[Int]("NegVote").get,
        doc.getAs[String]("User"))
    }
  }
}
