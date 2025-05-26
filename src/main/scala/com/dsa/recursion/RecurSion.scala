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

  
}
