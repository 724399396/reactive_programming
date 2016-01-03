def REPEAT(command : => Unit)(condition : => Boolean): Unit = {
  command
  if (condition) ()
  else REPEAT(command)(condition)
}

var a = 5

REPEAT {a -= 1; println("a")} (a == 0)


def REPEAT1(command: => Unit)(syntax: => Unit)
           (condition: => Boolean): Unit = {
  command
  if (condition) ()
  else REPEAT1(command)(syntax)(condition)
}

val UNTIL = identity _

a = 5

REPEAT1 {
  a -= 1; println("b")
} (UNTIL) (a == 0)
