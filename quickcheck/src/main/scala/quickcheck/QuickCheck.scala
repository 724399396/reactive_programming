package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("min2") = forAll { a: Int =>
    forAll {
      b: Int =>
        val min = a.min(b)
        findMin(insert(a,insert(b, empty))) == min
    }
  }

  property("gen1") = forAll { h: H =>
    val m = if (isEmpty((h))) 0 else findMin(h)
    findMin(insert(m,h)) == m
  }

  property("empty") = isEmpty(empty)

  property("del1") = forAll { h:H =>
    val m = if (isEmpty((h))) 0 else findMin(h)
    findMin(insert(m,deleteMin(h))) == m
  }

  property("del2") = forAll { a: Int =>
    empty == deleteMin(insert(a,empty))
  }

  def sequence(heap: H): List[Int] = {
    if (isEmpty(heap))
      List()
    else
      findMin(heap) :: sequence(deleteMin(heap))
  }

  property("meld1") = forAll { h1: H =>
    forAll {
     h2: H =>
        val m1 = findMin(h1)
        val m2 = findMin(h2)
        val merge = meld(h1,h2)
        val min = m1.min(m2)
        min == findMin(merge)
    }
  }

  property("inserting_sequence") = forAll { (l: List[A]) =>
    val h = l.foldRight(empty)(insert)
    sequence(h) == l.sorted
  }

  lazy val genHeap: Gen[H] = for {
    elem <- arbitrary[Int]
    heap <- oneOf(const(empty), genHeap)
  } yield insert(elem, heap)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)
}
