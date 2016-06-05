package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import services.PocketMathService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by baskaran on 6/6/16.
  */
class ApplicationController @Inject()(pocketMathService: PocketMathService) extends Controller {

  def traders(maybeCity: Option[String]): Action[AnyContent] = Action.async {
    pocketMathService.getTraders(maybeCity) map {
      case Some(traders) =>
        Ok(Json.obj("status" -> Json.obj("code" -> 1000, "msg" -> "Traders data found"), "data" -> traders))
      case None =>
        NotFound(Json.obj("status" -> Json.obj("code" -> 1005, "msg" -> "No Traders data found")))
    } recover {
      case t: Throwable =>
        t.printStackTrace
        InternalServerError(Json.obj("status" -> Json.obj("code" -> 1001, "msg" -> "Failed to retrieve traders")))
    }
  }

  def transactions(maybeYear: Option[Int]): Action[AnyContent] = Action.async {
    pocketMathService.getTransactions(maybeYear) map {
      case Some(transactions) =>
        Ok(Json.obj("status" -> Json.obj("code" -> 2000, "msg" -> "Transactions data found"), "data" -> transactions))
      case None =>
        NotFound(Json.obj("status" -> Json.obj("code" -> 2005, "msg" -> "No transactions data found")))
    } recover {
      case t: Throwable =>
        t.printStackTrace
        InternalServerError(Json.obj("status" -> Json.obj("code" -> 2001, "msg" -> "Failed to retrieve transactions")))
    }
  }

  def avgTransactions(maybeCity: Option[String]): Action[AnyContent] = Action.async {
    pocketMathService.getTransactionsAvg(maybeCity) map {
      case Some(transactions) =>
        Ok(Json.obj("status" -> Json.obj("code" -> 3000, "msg" -> "Transactions data found"), "data" -> transactions))
      case None =>
        NotFound(Json.obj("status" -> Json.obj("code" -> 3005, "msg" -> "No transactions data found")))
    } recover {
      case t: Throwable =>
        t.printStackTrace
        InternalServerError(Json.obj("status" -> Json.obj("code" -> 3001, "msg" -> "Failed to retrieve transactions")))
    }
  }

  def transaction(value: String): Action[AnyContent] = Action.async {
    pocketMathService.getTransaction(value) map {
      case Some(transaction) =>
        Ok(Json.obj("status" -> Json.obj("code" -> 4000, "msg" -> "Transaction data found"), "data" -> transaction))
      case None =>
        NotFound(Json.obj("status" -> Json.obj("code" -> 4005, "msg" -> "No transaction data found")))
    } recover {
      case t: Throwable =>
        t.printStackTrace
        InternalServerError(Json.obj("status" -> Json.obj("code" -> 4001, "msg" -> "Failed to retrieve transaction")))
    }
  }

}
