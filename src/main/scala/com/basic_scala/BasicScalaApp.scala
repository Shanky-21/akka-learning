package com.basic_scala

import scala.annotation.tailrec

object ScalaApp extends App {


  def helper(x: Int): Int = {

    if ( x < 0) 0
    else if ( x == 0 || x == 1 ) x
    else helper(x-1) + helper(x-2)
  
  }


  def tailHelper(x: Int, acc: Int) : Int = {

    @tailrec
    def loop(x: Int, a: Int, b: Int) : Int = {

      if ( x == 0 ) a
      else loop(x -1, b, a + b)

    }

    loop(x, 0, 1)
  }

  def tailHelper2(x: Int) : String = {

    @tailrec
    def loop(x: Int, a: Int, b: Int, acc: List[Int]) : List[Int] = {

      if ( x == 0 ) acc
      else loop(x-1, b, a + b, acc :+ a)
      
    }

    loop(x, 0, 1, List()).mkString(",")
  }

  val res2 = tailHelper2(12)
  println(s"res2 :: ${res2}") 



  val input1 = List(1,2,3,4,5)
  val res1 = input1.map(helper)
  println(s"res1 :: ${res1}")
  
}
