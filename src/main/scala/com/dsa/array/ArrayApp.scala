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


  /* 
    Stock buy and sell via local minima and maxima approach.
  
   */
  // def helper2(arr: Array[Int], i: Int): Boolean = {

  //   if( arr.length >= 2){

  //     if(i == 0 && (arr(i) > arr(i + 1))) {
  //       false
  //     }

  //     else if(i == (arr.length - 1) && (arr(i) < arr(i -1))) {
  //       true
  //     } else false

  //   } else if (arr.isEmpty) false
  //   else true

  // }

  // def helper4(arr: Array[Int], i : Int): Boolean = {

  //   if ( arr.length >= 2){

  //     if ( i == 0 && (arr(i+1) < arr(i))) {
  //       true
  //     } else if ( i == (arr.length - 1) && (arr(i) < arr(i-1))) {
  //       true
  //     } else false


  //   } else if (arr.isEmpty) false
  //   else true


  // }

  // def helper3(arr: Array[Int]): Int = {

  //   var p = 0

  //   arr.foldLeft(0)((acc, x) => {

  //     val buy = helper2(arr, x)

  //     if (buy) {
  //       println(s"buy at ${x}")
  //     }

  //     val sell = helper4(arr, x)

  //     if (sell) {
  //       println(s"sell at ${x}")
  //       acc += buy 
  //     }

      


  //   })




  // }


  /* 
     1
     2
     3               x <- Sell 
     4
     5             x
     6
     7   x     x
     8
     9     x   <- Buy
     10  
        0 1 2 3 4 5 6 7 8 9
  
        Profit = 8 - 3 = 5

      
  
  
   */



  def bestApproachStockBuySellAddProfit(arr: Array[Int]): Int = {


    val res = arr.sliding(2).foldLeft(0)((acc, x) => {

      x match {

        case Array(a, b) => if (a < b) acc + (b - a) else acc
        case _ => acc

      }


    })

    res 

  }

  val input15 = Array(1, 5, 3, 8, 12)
  val res15 = bestApproachStockBuySellAddProfit(input15)
  println(s"res15 :: ${res15}")


  def trappingRainWater(arr: Array[Int]): Int = {

      /*
    ------------------------------------------------------------
    Explanation of lmax and rmax Computation for Trapping Rain Water
    ------------------------------------------------------------

    To calculate the amount of water that can be trapped at each index `i`,
    we need to know the tallest bar to the *left* and to the *right* of `i`.

    For each index i:
      lmax[i] = maximum height of bars strictly to the LEFT of index i
      rmax[i] = maximum height of bars strictly to the RIGHT of index i

    This is required because the water that can be trapped at index `i` is:
      water[i] = min(lmax[i], rmax[i]) - height[i]

    We compute these efficiently using `scanLeft` and `scanRight`:

    ----------------------
    lmax Computation
    ----------------------

    val lmax = arr
      .scanLeft(Int.MinValue)((maxSoFar, current) => math.max(maxSoFar, current))
      .dropRight(1)

    - `scanLeft` builds an array from left to right, accumulating the max height so far.
    - The first value is a seed (Int.MinValue), ensuring there's no value to the left of index 0.
    - For each index i, it records the max of elements before i (excluding current).
    - We use `.dropRight(1)` to exclude the final value, which includes the last element itself.

    Example:
      Input:        arr = [2, 1, 5, 0]
      scanLeft:           [Int.MinValue, 2, 2, 5, 5]
      dropRight(1):       [Int.MinValue, 2, 2, 5]  ← valid lmax

    ----------------------
    rmax Computation
    ----------------------

    val rmax = arr
      .scanRight(Int.MinValue)((current, maxSoFar) => math.max(maxSoFar, current))
      .drop(1)

    - `scanRight` works from right to left, also accumulating the max seen so far.
    - The last value is seeded with Int.MinValue to represent "no bars to the right."
    - For each index i, it gives the max of elements after i (excluding current).
    - We use `.drop(1)` to remove the initial seed value.

    Example:
      Input:        arr = [2, 1, 5, 0]
      scanRight:         [5, 5, 5, 0, Int.MinValue]
      drop(1):           [5, 5, 5, 0]  ← valid rmax

    With these `lmax` and `rmax` arrays, we can now compute water trapped at each index:
      water[i] = max(0, min(lmax[i], rmax[i]) - arr[i])

    This avoids redundant traversal and results in an O(n) time complexity.
  */


    val lmax = arr
      .scanLeft(Int.MinValue)((maxSoFar, current) => math.max(maxSoFar, current))
      .dropRight(1) // exclude current element from left max

    val rmax = arr
      .scanRight(Int.MinValue)((current, maxSoFar) => math.max(maxSoFar, current))
      .drop(1) // exclude current element from right max



    val p = arr.zipWithIndex.foldLeft(0)((acc, x) => {

      val (value, index) = x

      if (index != 0 && index != arr.length - 1) {

        val h = math.min(lmax(index), rmax(index))

        if (h > value) {
          acc + (h - value)
        } else acc

      } else acc

    })

    p
    

  }

  val input16 = Array(1, 5, 3, 8, 12)
  val res16 = trappingRainWater(input16)
  println(s"res16 :: ${res16}")


  def totalOccurencesOfOne(arr: Array[Int]): Int = {

    // val res = arr.foldLeft(0)((acc, x) => {

    //   if(x == 1 ) acc + 1 else acc

    // })

    // res


    // arr.filter(_ == 1).length
    // Using map:
    // arr.groupBy(identity).map { case (k,v) => k -> v.length }.getOrElse(1, 0)
    // - Creates a new Map with transformed values
    // - More memory efficient since it creates new Map
    // - Preferred in Scala 2.13+ as mapValues is deprecated
    
    // Using mapValues:
    // arr.groupBy(identity).mapValues(x => x.length).getOrElse(1, 0) 
    // - Returns a view, lazily transforms values when accessed
    // - Original Map values remain in memory
    // - Less memory efficient for large maps
    // - Deprecated in Scala 2.13+

    arr.groupBy(identity).mapValues(x => x.length).getOrElse(1, 0)



  }

  val input17 = Array(1, 1, 1, 0, 0, 0, 1)
  val res17 = totalOccurencesOfOne(input17)
  println(s"res17 :: ${res17}")


  def maxConsecutiveOnes(arr: Array[Int]) : Int = {


    arr.foldLeft((0, 0))((acc, x) => {

      val (maxCount, currentCount) = acc

      if ( x == 1){

        val newCurrentCount = currentCount + 1

        if ( newCurrentCount > maxCount) {

          (newCurrentCount, newCurrentCount)

        } else (maxCount, newCurrentCount)

      } else (maxCount, 0)

    })._1

  }

  val input18 = Array(1, 1, 1, 0, 0 , 1,  1, 1, 1)
  val res18 = maxConsecutiveOnes(input18)
  println(s"res18 :: ${res18}")



  def maxSubArraySum(arr: Array[Int]): Int = {
    // Initialize result to first element instead of 0 to handle negative numbers
    var maxSoFar = arr(0)
    var maxEndingHere = arr(0)

    for (i <- 1 until arr.length) {
      // For each element, either start new subarray or extend existing one
      maxEndingHere = math.max(arr(i), maxEndingHere + arr(i))
      maxSoFar = math.max(maxSoFar, maxEndingHere)
    }

    maxSoFar
  }

  val input19 = Array(-5, 1, -2, 3, -1, 2, -2)

  val res19 = maxSubArraySum(input19)
  println(s"res19 :: ${res19}")

  // println(s"res10 :: ${res10.mkString(",")}")

  
  


  
}
