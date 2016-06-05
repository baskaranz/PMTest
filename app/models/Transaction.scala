package models

import play.api.libs.json.Json

/**
  * Created by baskaran on 6/6/16.
  */
case class Transaction(traderId: String,
                       timestamp: Long,
                       value: Double)

object Transaction {
  implicit val format = Json.format[Transaction]
}

