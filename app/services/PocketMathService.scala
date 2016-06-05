package services

import javax.inject.Inject

import models.{Transaction, Trader}
import play.api.{Logger, Configuration}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by baskaran on 6/6/16.
  */
class PocketMathService @Inject()(config: Configuration, wsClient: WSClient) {

  lazy val maybePocketMathHost = config.getString("pocketmath.host")
  lazy val maybeTradersEndpoint = config.getString("pocketmath.endpoint.traders")
  lazy val maybeTransactionsEndpoint = config.getString("pocketmath.endpoint.transactions")
  lazy val maybeApiKey = config.getString("pocketmath.apiKey")

  def getTraders: Future[Option[List[Trader]]] = {
    if (maybePocketMathHost.isDefined && maybeTradersEndpoint.isDefined && maybeApiKey.isDefined) {
      val eventualWSResponse = wsClient.url(maybePocketMathHost.get + maybeTradersEndpoint.get).
        withRequestTimeout(5000 milliseconds).
        withHeaders("x-api-key" -> maybeApiKey.get).
        get()
      eventualWSResponse map { wsResponse =>
        Json.parse(wsResponse.json.toString).asOpt[List[Trader]]
      } recover {
        case t: Throwable =>
          Logger.error("exception while fetching traders")
          t.printStackTrace
          None
      }
    } else {
      Logger.info("No traders found")
      Future.successful(None)
    }
  }

  def getTransactions: Future[Option[List[Transaction]]] = {
    if (maybePocketMathHost.isDefined & maybeTransactionsEndpoint.isDefined && maybeApiKey.isDefined) {
      val eventualWSResponse = wsClient.url(maybePocketMathHost.get + maybeTransactionsEndpoint.get).
        withRequestTimeout(5000 milliseconds).
        withHeaders("x-api-key" -> maybeApiKey.get).
        get()
      eventualWSResponse map { wsResponse =>
        Json.parse(wsResponse.json.toString).asOpt[List[Transaction]]
      } recover {
        case t: Throwable =>
          Logger.error("exception while fetching transactions")
          t.printStackTrace
          None
      }
    } else {
      Logger.info("No transactions found")
      Future.successful(None)
    }
  }
}
