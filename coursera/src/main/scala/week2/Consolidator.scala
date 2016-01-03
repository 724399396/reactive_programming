package week2

/**
  * Created by liwei on 2016/1/3.
  */
class Consolidator(accounts: List[BankAccount]) extends Subscribe {
  accounts foreach (_.subscribe(this))

  private var total: Int = _
  compute()

  def compute() = {
    total = accounts.map(_.currentBalance).sum
  }

  def handle(pub: Publisher) = compute()

  def totalBalance = total
}
