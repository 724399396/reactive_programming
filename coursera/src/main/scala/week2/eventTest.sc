import week2.{Consolidator, BankAccount}

val a = new BankAccount
val b = new BankAccount
val c = new Consolidator(List(a,b))

c.totalBalance

a deposit 100
c.totalBalance

b deposit 20

c.totalBalance