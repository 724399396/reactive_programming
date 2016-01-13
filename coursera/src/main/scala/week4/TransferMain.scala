package week4

import akka.actor.{Props, Actor}
import akka.event.LoggingReceive
import week4.BankAccount.Deposit

/**
  * Created by weili on 16-1-13.
  */
class TransferMain extends Actor {
  val accountA = context.actorOf(Props[BankAccount], "accountA")
  val accountB = context.actorOf(Props[BankAccount], "accountB")

  accountA ! Deposit(100)

  def receive = LoggingReceive {
    case BankAccount.Done =>
      transfer(150)
  }

  def transfer(amount: BigInt) = {
    val wireTransfer = context.actorOf(Props[WireTransfer], "wireTransfer")
    wireTransfer ! WireTransfer.Transfer(accountA, accountB, amount)
    context.become(LoggingReceive {
      case WireTransfer.Done =>
        println("success")
        context stop self
    })
  }

}
