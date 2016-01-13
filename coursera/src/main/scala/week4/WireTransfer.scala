package week4

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive

/**
  * Created by weili on 16-1-13.
  */
object WireTransfer {
  case class Transfer(from: ActorRef, to: ActorRef, amount: BigInt)
  case object Done
  case object Failed
}

class WireTransfer extends Actor {
  import WireTransfer._

  def receive = LoggingReceive {
    case Transfer(from, to, amount) =>
      from ! BankAccount.WithDraw(amount)
      context.become(awaitWithdraw(to,amount, sender))
  }

  def awaitWithdraw(to: ActorRef, amount: BigInt, client: ActorRef): Receive = LoggingReceive {
    case BankAccount.Done =>
      to ! BankAccount.Deposit(amount)
      context.become(awaitDeposit(client))
    case BankAccount.Failed =>
      client ! Failed
      context.stop(self)
  }

  def awaitDeposit(client: ActorRef): Receive = LoggingReceive {
    case BankAccount.Done =>
      client ! Done
      context.stop(self)
  }
}
