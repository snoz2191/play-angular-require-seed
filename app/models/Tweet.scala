package models

import play.api.Logger
import play.api.libs.json.{Writes, Reads, Json}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, BSONObjectIDIdentity, BSONStringHandler, BSONNumberLike}
import reactivemongo.bson.Producer.nameValue2Producer
import play.modules.reactivemongo.json.BSONFormats._
/*
 * Created by domingo on 08/08/14.
 */
case class Tweet(_id: Option[BSONObjectID], Text: String, PosiVote: Int, NeuVote: Int, NegVote: Int, User: Option[String])

object Tweet {
  /** serialize/Deserialize a Tweet into/from JSON value */
//  implicit val tweetFormat = Json.format[Tweet]

  implicit val TweetToJson : Writes[Tweet] = (
    (__ \ "_id").writeNullable[String].contramap[Option[BSONObjectID]](l => l.map(_.stringify)) and
      (__ \ "Text").write[String] and
      (__ \ "PosiVote").write[Int] and
      (__ \ "NeuVote").write[Int] and
      (__ \ "NegVote").write[Int] and
      (__ \ "User").writeNullable[String]
    )(unlift(Tweet.unapply))

  implicit val jsonToTweet : Reads[Tweet] = (
    (__ \ "_id").readNullable[String].map[Option[BSONObjectID]](l => l.map(BSONObjectID(_))) and
      (__ \ "Text").read[String] and
      (__ \ "PosiVote").read[Int] and
      (__ \ "NeuVote").read[Int] and
      (__ \ "NegVote").read[Int] and
      (__ \ "User").readNullable[String]
    )(Tweet.apply _)

  /** serialize a Tweet into a BSON */
  implicit object TweetBSONWriter extends BSONDocumentWriter[Tweet] {
    def write(tweet: Tweet): BSONDocument =
      BSONDocument(
        "_id" -> tweet._id.getOrElse(BSONObjectID.generate),
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
