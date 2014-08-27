package controllers

import akka.actor.FSM.Failure
import akka.actor.Status.Success
import play.api.mvc._
import reactivemongo.core.commands.{GroupField, SumValue, Match, Aggregate}
import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import models.User._
import models.User.UserBSONReader
import models.User.UserBSONReader
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONNumberLike
import reactivemongo.bson.BSONDocumentIdentity
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.BSONArray
import reactivemongo.bson.Producer.nameValue2Producer
import models.Name.NameBSONWriter
import scala.concurrent.Future

/**
 * Created by domingo on 08/08/14.
 */
object Users extends Controller with MongoController {

  val collection = db.collection[BSONCollection]("tweets")

      def index () = Action.async {
        val query = Aggregate(collection.name,
          Seq(
            Match(BSONDocument("user" -> BSONDocument("$ne" -> ""))),
            GroupField("user")("score" -> SumValue(1))
          )
        )
        val cursor = collection.db.command(query) // get all the fields of all the tweets
        cursor.map {
         s =>  Ok(Json.toJson(s.toList.map{
           a => a.get("_id").get
             User(a.getAs[String]("_id").get, a.getAs[Int]("score").get)
         }))
        }
      }


}


