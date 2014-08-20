package controllers

import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import models.Tweet
import models.Tweet.tweetFormat
import models.Tweet.TweetBSONReader
import models.Tweet.TweetBSONWriter
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.concurrent.Future

/**
 * Created by domingo on 08/08/14.
 */
  object Tweets extends Controller with MongoController {

    val collection = db.collection[BSONCollection]("Tweets")

    /** list all tweets */
    def index (UserUpd: String) = Action.async {
      val cursor = collection.find(BSONDocument("User" -> ""), BSONDocument()).cursor[Tweet] // get all the fields of all the tweets
      cursor.collect[List]().map(s => Ok(Json.toJson(s)))// convert it to a JSON and return it
    }

    def getTweet (UserUpd: String) = Action.async {
      collection.find(BSONDocument()).one[Tweet].flatMap{ t =>
        println("blahhh"+ t)
        t.map{ tweet =>
          println("dentro del option tweet")
          collection.update(BSONDocument("_id" -> tweet.id),BSONDocument("User" -> UserUpd)).map{
            _ => println("blah")
            Ok(Json.toJson(tweet))
          }.recover { case _ => println("Fuck1")
                                InternalServerError }
        }.getOrElse( Future(BadRequest) )
      }.recover { case _ => println("Fuck2")
                            InternalServerError }
    }

    /** create a tweet from the given JSON */
    def create() = Action.async(parse.json) { request =>
      request.body.asOpt[Tweet].map { tweet =>
        collection.insert(tweet).map {
          _ => Ok(Json.toJson(tweet))
        }.recover { case _ => InternalServerError}
      }.getOrElse(Future(BadRequest))
    }
//
//
    //
    //        val Text = request.body.\("Text").toString().replace("\"", "")
    //        val PosVote = request.body.\("PosVote").as[Int]
    //        val NeuVote = request.body.\("NeuVote").as[Int]
    //        val NegVote = request.body.\("NegVote").as[Int]
    //        val User = request.body.\("User").asOpt[String].map{ u =>
    //          u.toLowerCase
    //        }.getOrElse("")
    //        val tweet = Tweet(Option(BSONObjectID.generate), Text, PosVote, NeuVote, NegVote,  Some(User)) // create the tweet
    //        collection.insert(tweet).map(
    //          _ => Ok(Json.toJson(tweet))) // return the created tweet in a JSON
    //      }
//
//    /** retrieve the tweet for the given id as JSON */
//    def show(id: String) = Action.async(parse.empty) {
//      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
//      // get the tweet having this id (there will be 0 or 1 result)
//      val futureTweet = collection.find(BSONDocument("_id" -> objectID)).one[Tweet]
//      futureTweet.map {
//        S => Ok(Json.toJson(S))
//      }.recover { case _ => InternalServerError}
//    }
//
    /** update the tweet for the given id from the JSON body */
    def updateTweet(id: String) = Action.async(parse.json) { request =>
//      request.body.asOpt[Tweet].map { tweet =>
//                collection.update(BSONDocument("_id" -> tweet.id), tweet).map {
//                  _ => Ok(Json.toJson(tweet))
//                }.recover { case _ => InternalServerError}
//      }.getOrElse(Future(BadRequest))
//
      val objectID = BSONObjectID.apply(id) // get the corresponding BSONObjectID
      val Text = request.body.\("Text").toString().replace("\"", "")
      val PosVote = request.body.\("PosVote").as[Int]
      val NeuVote = request.body.\("NeuVote").as[Int]
      val NegVote = request.body.\("NegVote").as[Int]
      val User = request.body.\("User").asOpt[String]
      val modifier = BSONDocument(// create the modifier tweet
        "$set" -> BSONDocument(
          "Text" -> Text,
          "PosVote" -> PosVote,
          "NeuVote" -> NeuVote,
          "NegVote" -> NegVote,
          "User" -> User
        ))
      collection.update(BSONDocument("_id" -> objectID), modifier).map(
        _ => Ok(Json.toJson(Tweet(Option(objectID), Text, PosVote, NeuVote, NegVote, User)))) // return the modified tweet in a JSON
    }
//
//    /** delete a tweet for the given id */
//    def delete(id: String) = Action.async(parse.empty) {
//      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
//      collection.remove(BSONDocument("_id" -> objectID)).map(// remove the tweet
//        _ => Ok(Json.obj())).recover{ case _ => InternalServerError} // and return an empty JSON while recovering from errors if any
//    }
  }


