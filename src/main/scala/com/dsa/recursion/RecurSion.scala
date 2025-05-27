package com.dsa.recursion

import scala.annotation.tailrec
import akka.actor.Status.Success
import akka.actor.FSM.Failure
import scala.util.Try

object RecurSion extends App {


  @tailrec
  def sumOfDigits(n : Int, start: Int, end: Int, accu: Int =  0): Int = {


    val numString = n.toString


    if ( start == end ) accu + numString(start).asDigit
    else if ( start > end ) accu
    else {


      val newAccu = accu + numString(start).asDigit + numString(end).asDigit
      sumOfDigits(n, start + 1, end - 1, newAccu)


    }


  }

  val n = 258
  val res = Try { sumOfDigits(258, 0, 2) }

  println(s"res :: ${res}")



  def maxRopeCut(n: Int, a: Int, b: Int, c: Int): Int = {

    if ( n == 0 ) 0
    else if ( n < 0 ) -1
    else {

      // We can cut the rope in three ways

      val res1 = 1 + maxRopeCut(n - a, a, b, c)
      val res2 = 1 + maxRopeCut(n - b, a, b, c)
      val res3 = 1 + maxRopeCut(n - c, a, b, c)

      val maxRes = Math.max(res1, Math.max(res2, res3))

      if ( maxRes == 0 ) -1
      else maxRes
    }

  }


    val input = 23
    val res2 = maxRopeCut(input, 11, 9, 12)

    val input2 = 5
    val res3 = maxRopeCut(input2, 2, 5, 1)

    val input3 = 5
    val res4 = maxRopeCut(input3, 4, 2, 6)
    println(s"res2 :: ${res2}")
    println(s"res3 :: ${res3}")
    println(s"res4 :: ${res4}")



  def generateSubsets(currString: String, str: String): List[String] = {

    if(str.isEmpty) List(currString)

    else {


      val res1 = generateSubsets(currString + str.head, str.tail)
      val res2 = generateSubsets(currString, str.tail)

      res1 ++ res2
    }

  }

  val input4 = "abc"
  val res5 = generateSubsets("", input4)
  println(s"res5 :: ${res5}")
  
  def generateSubsets2(str: String): List[String] = {

    if(str.isEmpty) List("")
    else {

      val res1 = generateSubsets2(str.tail)
      val res2 = res1.map(s => str.head + s)

      res1 ++ res2
    }
  }

  val input5 = "abc"
  val res6 = generateSubsets2(input5)
  println(s"res6 :: ${res6}")



  def toh(n: Int, A: Char, B: Char, C: Char): Unit = {

    if(n == 1) {
      println(s"Move disk 1 from ${A} to ${C}")
    } else {

      // move n-1 disks from A to B
      toh(n-1, A, C, B)

      // move nth disk from A to C
      println(s"Move disk ${n} from ${A} to ${C}")

      // move n-1 disks from B to C
      toh(n-1, B, A, C)
      
      
    }

  }

  toh(3, 'A', 'B', 'C')


  def jos(n: Int, k : Int): Int = {

    if ( n == 1) 0
    else (jos(n-1, k) + k) % n

  }

  val res7 = jos(5, 3)
  println(s"res7 :: ${res7}")
  

  def sumOfSubsets(n: Int, arr: List[Int]): Int = {

    if ( n == 0 ) 1
    else if (arr.isEmpty) {

      if ( n == 0 ) 1
      else 0

    }
    else {

      val res1 = sumOfSubsets(n - arr.head, arr.tail)
      val res2 = sumOfSubsets(n, arr.tail )

      res1 + res2

    }

  }

  val input6 = List(1, 2, 3, 4, 5)
  val res8 = sumOfSubsets(5, input6)
  println(s"res8 :: ${res8}")

  val input7 = List(10, 5, 2, 3, 6)
  val res9 = sumOfSubsets(8, input7)
  println(s"res9 :: ${res9}")


  def swap(s: String, i: Int, j: Int): String = {

    val charArray = s.toCharArray
    val temp = charArray(i)
    charArray(i) = charArray(j)
    charArray(j) = temp
    charArray.mkString("")
  }

  def permutationsOfString(s: String, i: Int): List[String] = {

    if ( i == s.length - 1 ) List(s)

    else {

      var res1 = List[String]()


      for ( j <- i to s.length - 1) {

        val newString = swap(s, i, j)

        val res2 = permutationsOfString(newString, i + 1)

        println(s"res2 :: ${res2}")

        res1 = res1 ++ res2

      }

      res1

    }
  }

  val input8 = "abcd"
  val res10 = permutationsOfString(input8, 0)
  println(s"res10 :: ${res10}")


  
}
