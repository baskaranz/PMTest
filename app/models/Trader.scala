package models

import play.api.libs.json.Json

/**
  * Created by baskaran on 6/6/16.
  */
case class Trader(id: String,
                  name: String,
                  city: String)

object Trader {
  implicit val format = Json.format[Trader]
}