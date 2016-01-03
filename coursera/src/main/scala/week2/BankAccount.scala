package week2

/**
  * Created by liwei on 2016/1/3.
  */
class BankAccount extends Publisher {
  private var balance: Int = 0

  def currentBalance = balance

  def deposit(amount: Int): Unit = {
    if (amount > 0) {
      balance += amount
      publish()
    }
  }

  def withdraw(amount: Int): Unit = {
    if (0 < amount && amount <= balance) {
      balance -= amount
      publish()
    } else throw new Error("insufficient funds")
  }
}
