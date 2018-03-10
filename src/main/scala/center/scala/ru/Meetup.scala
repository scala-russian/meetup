package center.scala.ru

import com.twitter.finagle.Http
import com.twitter.io.Buf
import com.twitter.util.{Await, Try}
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import io.finch.Decode.Aux
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.netty.handler.codec.http.QueryStringDecoder
import shapeless._

object Meetup extends StrictLogging {
  def main(args: Array[String]): Unit = {
    Await.ready(Http.server.serve("127.0.0.1:8080", API.route.toService))
    logger.info("meetup application stopped")
  }
}

object API extends StrictLogging {

  val prefix: Endpoint[HNil] = path("v1")

  val visitors: Endpoint[String] = get("visitors") {
    Ok("hello")
  }

  implicit val form: Aux[Map[String, String], Application.WwwFormUrlencoded] = {
    Decode.instance((buf, charset) =>
      Try {
        val body = QueryStringDecoder.decodeComponent(Buf.decodeString(buf, charset))
        body
          .split("&")
          .map { l =>
            val arr = l.split("=")
            arr(0) -> arr(1)
          }
          .toMap
    })
  }

  val addVisitor: Endpoint[String] =
    post("visitors" :: body[Map[String, String], Application.WwwFormUrlencoded]) {
      (e: Map[String, String]) =>
        logger.info("visitor: " + e.toString())
        Ok("ok")
    }

  val addSpeaker: Endpoint[String] = post("speakers" :: body[Map[String, String], Application.WwwFormUrlencoded]) {
    (e: Map[String, String]) =>
      logger.info("speaker: " + e.toString())
      Ok("ok")
  }

  Stream() #::: Stream()

  val route = prefix :: (visitors :+: addVisitor :+: addSpeaker)
}
