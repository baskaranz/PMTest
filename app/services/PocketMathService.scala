package services

import javax.inject.Inject

import models.{Transaction, Trader}
import org.joda.time.DateTime
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

  def  getTraders(maybeCity: Option[String]): Future[Option[List[Trader]]] = {
    if (maybePocketMathHost.isDefined && maybeTradersEndpoint.isDefined && maybeApiKey.isDefined) {
      val eventualWSResponse = wsClient.url(maybePocketMathHost.get + maybeTradersEndpoint.get).
        withRequestTimeout(5000 milliseconds).
        withHeaders("x-api-key" -> maybeApiKey.get).
        get()
      eventualWSResponse map { wsResponse =>
        val maybeTraders: Option[List[Trader]] = Json.parse(wsResponse.json.toString).asOpt[List[Trader]]
        maybeTraders match {
          case Some(traders) =>
            if(maybeCity.isDefined) {
              Some(traders.filter(_.city.toLowerCase == maybeCity.get.toLowerCase).sortBy(f => f.name))
            } else {
              Some(traders)
            }
          case None =>
            None
        }
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

  def getTransactionsAvg(maybeCity: Option[String]): Future[Option[Double]] = {
    if (maybePocketMathHost.isDefined & maybeTransactionsEndpoint.isDefined && maybeApiKey.isDefined) {
      val eventualResult = getTraders(maybeCity) map {
        case Some(traders) =>
          getTransactions(None) map {
            case Some(transactions) =>
              var tradersList = traders.map(_.id)
              if (maybeCity.isDefined) {
                tradersList = traders.filter(_.city.toLowerCase == maybeCity.get.toLowerCase).map(_.id)
                Some((transactions.filter(p => tradersList.contains(p.traderId)).map(_.value).sum) / tradersList.size)
              } else {
                Some(transactions.map(_.value).sum / transactions.size)
              }
            case None =>
              None
          }
        case None =>
          Future.successful(None)
      } recover {
        case t: Throwable =>
          Logger.error("exception while fetching transactions")
          t.printStackTrace
          Future.successful(None)
      }
      eventualResult.flatMap(f => f)
    } else {
      Logger.info("No transactions found")
      Future.successful(None)
    }
  }

  def getTransactions(maybeYear: Option[Int]): Future[Option[List[Transaction]]] = {
    if (maybePocketMathHost.isDefined & maybeTransactionsEndpoint.isDefined && maybeApiKey.isDefined) {
      val eventualWSResponse = wsClient.url(maybePocketMathHost.get + maybeTransactionsEndpoint.get).
        withRequestTimeout(5000 milliseconds).
        withHeaders("x-api-key" -> maybeApiKey.get).
        get()
      eventualWSResponse map { wsResponse =>
        val maybeTransactions = Json.parse(wsResponse.json.toString).asOpt[List[Transaction]]
        maybeTransactions match {
          case Some(transactions) =>
            if(maybeYear.isDefined) {
              Some(transactions.filter(f => new DateTime(f.timestamp * 1000L).getYear == maybeYear.get).sortBy(-_.value))
            } else {
              Some(transactions)
            }
          case None =>
            None
        }
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

  def getTransaction(value: String): Future[Option[Transaction]] = {
    if (maybePocketMathHost.isDefined & maybeTransactionsEndpoint.isDefined && maybeApiKey.isDefined) {
      val eventualWSResponse = wsClient.url(maybePocketMathHost.get + maybeTransactionsEndpoint.get).
        withRequestTimeout(5000 milliseconds).
        withHeaders("x-api-key" -> maybeApiKey.get).
        get()
      eventualWSResponse map { wsResponse =>
        val maybeTransactions = Json.parse(wsResponse.json.toString).asOpt[List[Transaction]]
        maybeTransactions match {
          case Some(transactions) =>
            if(value == "high") {
              Some(transactions.sortBy(-_.value).head)
            } else if(value == "low") {
              Some(transactions.sortBy(_.value).head)
            } else {
              None
            }
          case None =>
            None
        }
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
