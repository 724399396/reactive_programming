trait Generator[+T] {
  self =>

  def generator: T

  def map[S](f : T => S):Generator[S] = new Generator[S] {
    override def generator: S = f(self.generator)
  }

  def flatMap[S](f: T => Generator[S]):Generator[S] = new Generator[S] {
    override def generator: S = f(self.generator).generator
  }
}

val integers = new Generator[Int] {
  val random = new java.util.Random
  def generator = random.nextInt()
}

def booleans = for(x <- integers) yield x > 0

def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap {
  x => u map (y => (x,y))
}

def single[T](x: T): Generator[T] = new Generator[T] {
  override def generator: T = x
}

def choose(lo: Int, hi: Int): Generator[Int] =
  for (x <- integers) yield lo + x % (hi - lo)

def oneOf[T](xs: T*): Generator[T] =
  for (x <- choose(0, xs.length)) yield xs(x)

def lists: Generator[List[Int]] = for {
  isEmpty <- booleans
  list <- if (isEmpty) emptyList else nonEmptyList
} yield list

def emptyList = single(Nil)

def nonEmptyList = for {
  head <- integers
  tail <- lists
} yield head :: tail

trait Tree

case class Inner(left: Tree, right: Tree) extends Tree

case class Leaf(x: Int) extends Tree

def trees: Generator[Tree] = for {
  isLeaf <- booleans
  tree <- if (isLeaf) leaf else inners
} yield tree

def leaf: Generator[Leaf] =
  for (x <- integers) yield Leaf(x)

def inners: Generator[Inner] = for {
  left <- trees
  right <- trees
} yield Inner(left, right)

trees.generator

def test[T](g: Generator[T], numTimes: Int = 100)
           (t: T => Boolean): Unit = {
  for(i <- 0 until numTimes) {
    val x = g.generator
    assert(t(x), "Test failed for " + x)
  }
  println("Test passed " + numTimes + " times test")
}

test(pairs(lists,lists)) {
  case (xs,ys) => (xs ++ ys).length > xs.length
}