class Account {
  private var balance: Int = 0

  def deposit(amount: Int): Unit = {
    if (amount > 0) balance += amount
  }

  def withdraw(amount: Int): Int = {
    if (0 < amount && amount <= balance) {
      balance -= amount
      balance
    } else throw new Error("insufficient funds")
  }
}

val account = new Account
account deposit 50
account withdraw 20
account withdraw 20
account withdraw 15


