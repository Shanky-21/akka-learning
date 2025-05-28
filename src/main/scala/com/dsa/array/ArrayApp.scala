package com.dsa.array

import java.security.Identity

object ArrayApp extends App {


  def largest(arr: Array[Int]): Int = {

    var max = Int.MinValue
    arr.foreach(x => {

      if(x > max) {
        max = x
      }

    })

    max
  }

  val input = Array(1, 2, 3, 4, 5)
  val res = largest(input)
  println(s"res :: ${res}")


  def secondLargest1(arr: Array[Int]): Int = {


    var m1 = Int.MinValue
    var m2 = Int.MinValue

    arr.foreach(x => {

      if(x > m1){
        m2 = m1
        m1 = x
      } else if ( x > m2) {
        m2 = x
      }

    })

    m2

  }

  def secondLargest(arr: Array[Int]): Int = {


    var m1 = arr.max
    var m2 = arr.reduceOption((a,b) => if((a > b) && ( a != m1)) a else if ( a != m1 && b != m1 ) b else Int.MinValue )

    m2.getOrElse(-1)

  }

  val input2 = Array(1, 2, 3, 5, 4)
  val res2 = secondLargest(input2)
  println(s"res2 :: ${res2}")


  def isSorted(arr: Array[Int]): Boolean = {

    val bool = arr.sliding(2).forall((x) => x match {

      case Array(a,b) =>  a <= b
      case _ => true

    })

    bool
    
  }

  val input3 = Array(1, 2, 3, 4, 5)
  val res3 = isSorted(input3)
  println(s"res3 is sorted :: ${res3}")


  def doReverse(arr: Array[Int]): Array[Int] = {

    // arr.reverse

    // This is an alternative implementation to reverse an array
    // Instead of using built-in reverse, we use foldLeft to build a new array
    // For each element x in the input array:
    // 1. Create a single element array Array(x)
    // 2. Concatenate it with the accumulator (acc) using ++
    // 3. The accumulator starts as an empty array Array[Int]()
    // 4. Since we prepend each element, the result is the reversed array
    arr.foldLeft(Array[Int]())((acc, x) => Array(x) ++ acc)

  }

  val input4 = Array(1, 2, 3, 4, 5)
  val res4 = doReverse(input4)
  println(s"res4 :: ${res4.mkString(",")}")



  def removeDuplicates(arr: Array[Int]): Array[Int] = {

    arr.distinct

  }

  val input5 = Array(1, 2, 3, 4, 5, 1, 2, 3)
  val res5 = removeDuplicates(input5)
  println(s"res5 :: ${res5.mkString(",")}")


  def moveZerosToEnd(arr: Array[Int]): Array[Int] = {


    arr.foldLeft(Array[Int]())((acc, x) => if ( x == 0 ) acc ++ Array(x) else Array(x) ++ acc)

  }

  def moveZerosToEnd2(arr: Array[Int]): Array[Int] = {

    val nonZero = arr.filter(_ != 0)
    val zero = arr.filter(_ == 0)
    nonZero ++ zero

  }

  def moveZerosToEnd3(arr: Array[Int]): Array[Int] = {
    // partition splits the array into two arrays based on a predicate function
    // The predicate function (_ != 0) returns:
    //   - true for non-zero elements which go into the first array (nonZero)
    //   - false for zero elements which go into the second array (zero)
    // It returns a tuple containing both arrays: (elements where pred is true, elements where pred is false)
    // This is more efficient than using filter twice since it only traverses the array once

    val (nonZero, zero) = arr.partition(_ != 0)
    println(s"nonZero :: ${nonZero.mkString(",")}")
    println(s"zero :: ${zero.mkString(",")}")
    nonZero ++ zero
  }

  val input6 = Array(0, 1, 2,0, 3, 4, 5, 0, 1, 2, 3)
  val res6 = moveZerosToEnd3(input6)
  println(s"res6 :: ${res6.mkString(",")}")



  def moveZeroOOPWay(arr: Array[Int]): Array[Int] = {

    var c = 0

    for ( i <- 0 until arr.length) {

      if ( arr(i) != 0)
        {
          arr(c) = arr(i)
          arr(i) = 0
          c += 1
        }
    }

    arr

  }


  val input7 = Array(0, 1, 2,0, 3, 4, 5, 0, 1, 2, 3)
  val res7 = moveZeroOOPWay(input7)
  println(s"res7 :: ${res7.mkString(",")}")


  def leftRotateAnArrayByOne(arr: Array[Int]): Array[Int] = {


    val temp = arr.head
    val res = arr.tail

    res ++ Array(temp)


  }

  val input8 = Array(1, 2, 3, 4, 5)
  val res8 = leftRotateAnArrayByOne(input8)
  println(s"res8 :: ${res8.mkString(",")}")


  def leftRotateAnArrayByD(arr: Array[Int], d: Int): Array[Int] = {

    val temp = arr.take(d)

    val res = arr.drop(d)

    res ++ temp

  }

  val input9 = Array(1, 2, 3, 4, 5)
  val res9 = leftRotateAnArrayByD(input9, 2)
  println(s"res9 :: ${res9.mkString(",")}")


  def leaderInAnArray(arr: Array[Int]): Unit = {


    var max_ = Int.MinValue
    arr.reverse.foreach(x => {

      if ( x > max_) {
        println(s"x :: ${x}")
        max_ = x
      }

      x

    })


  }

  val input10 = Array(1, 2, 3, 4, 5)
  leaderInAnArray(input10)

  val input11 = Array(7, 10, 4, 3, 6, 5, 2)
  leaderInAnArray(input11)



  def maxDifference(arr: Array[Int]): Int = {
    if (arr.length < 2) return 0
    
    arr.foldLeft((arr.head, 0))((acc, x) => {
      val (minSoFar, maxDiff) = acc
      val newMaxDiff = math.max(maxDiff, x - minSoFar)
      val newMin = math.min(minSoFar, x)
      (newMin, newMaxDiff)
    })._2
  }

  val input12 = Array(7, 10, 4, 3, 7, 5, 2)
  val res12 = maxDifference(input12)
  println(s"res12 :: ${res12}")


  def maxDifference2(arr: Array[Int]): Int = {
    if (arr.length < 2) return 0
    
    arr.foldLeft((arr.head, 0))((acc, x) => {
      val (minSoFar, maxDiff) = acc
      val newMaxDiff = math.max(maxDiff, x - minSoFar)
      val newMin = math.min(minSoFar, x)
      (newMin, newMaxDiff)
    })._2
  }

  // scanLeft approach for maximum difference
  def maxDifferenceUsingScanLeft(arr: Array[Int]): Int = {
    if (arr.length < 2) return 0
    
    // scanLeft creates a running minimum from left to right
    // It starts with Int.MaxValue and applies math.min to each element
    // The result is an array where each position contains the minimum seen so far
    val minPrefix = arr.scanLeft(Int.MaxValue)(math.min).init // .init removes the last element (which would be the overall minimum)
    
    println(s"Original array: ${arr.mkString(", ")}")
    println(s"Min prefix array: ${minPrefix.mkString(", ")}")
    
    // For each element, calculate the difference with the minimum seen before it
    val differences = arr.zip(minPrefix).map { case (curr, minBefore) => 
      val diff = curr - minBefore
      println(s"Element: $curr, Min before: $minBefore, Difference: $diff")
      diff
    }
    
    println(s"All differences: ${differences.mkString(", ")}")
    
    // Return the maximum difference
    differences.max
  }

  val input13 = Array(7, 10, 4, 3, 7, 5, 2)
  val res13 = maxDifferenceUsingScanLeft(input13)
  println(s"res13 (scanLeft approach) :: ${res13}")




  def helper(arr: Array[Int]):Unit = {


    arr.groupBy(identity).mapValues(x => x.length).find(x => x._2 > 1).map(println)

  }

  val input14 = Array(1, 2, 3, 4, 5, 1, 2, 3)
  helper(input14)


  


  // println(s"res10 :: ${res10.mkString(",")}")

  
  


  
}
