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



  def longestEvenOddSubArray(arr: Array[Int]): Int = { // O(n^2)

    var res = 0

    for ( i <- 0 until arr.length) {

      var curr = 1

      for ( j <- i + 1 until arr.length) {

        if(
          (arr(j) % 2 == 0 && arr(j-1) % 2 != 0)
         || (arr(j) % 2 != 0 && arr(j-1) % 2 == 0)
         ) {

          curr += 1

        }

      }

      res = math.max(res, curr)

    }

    res

  }

  val input20 = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  val res20 = longestEvenOddSubArray(input20)
  println(s"res20 :: ${res20}")


  def longestEvenOddSubArray2(arr: Array[Int]): Int = { // O(n)

    // The optimization comes from:
    // 1. Single pass through array instead of nested loops - O(n) vs O(n^2)
    // 2. Maintaining running count (curr) and updating max (res) as we go
    // 3. Reset curr=1 when even-odd pattern breaks instead of starting new nested loop
    // 4. No need to recompute previous elements' even/odd status multiple times

    var res = 0

    var curr = 1

    for ( i <- 1 until arr.length) {

        if(
          (arr(i) % 2 == 0 && arr(i-1) % 2 != 0)
         || (arr(i) % 2 != 0 && arr(i-1) % 2 == 0)
         ) {

          curr += 1
          res = math.max(res, curr)

        } else curr = 1


    }

    res

  }

  val input21 = Array(1, 2, 3, 4, 6, 6, 6, 6, 9, 10)
  val res21 = longestEvenOddSubArray2(input21)
  println(s"res21 :: ${res21}")


  def longestEvenOddSubArray3(arr: Array[Int]): Int = { // O(n)

    // The optimization comes from:
    // 1. Single pass through array instead of nested loops - O(n) vs O(n^2)
    // 2. Maintaining running count (curr) and updating max (res) as we go
    // 3. Reset curr=1 when even-odd pattern breaks instead of starting new nested loop
    // 4. No need to recompute previous elements' even/odd status multiple times

    arr.zipWithIndex.tail.foldLeft((1, 0))((acc, x) => {

      val (curr, res) = acc 

      if(
        (x._1 % 2 == 0 && arr(x._2-1) % 2 != 0)
       || (x._1 % 2 != 0 && arr(x._2-1) % 2 == 0)
       ) {

        (curr + 1, math.max(res, curr + 1))

      } else (1, math.max(res, 1))


     })._2



    // for ( i <- 1 until arr.length) {

    //     if(
    //       (arr(i) % 2 == 0 && arr(i-1) % 2 != 0)
    //      || (arr(i) % 2 != 0 && arr(i-1) % 2 == 0)
    //      ) {

    //       curr += 1
    //       res = math.max(res, curr)

    //     } else curr = 1


    // }

    // res

  }

  val input22 = Array(1, 2, 3, 4, 6, 6, 6, 6, 9, 10)
  val res22 = longestEvenOddSubArray3(input22)
  println(s"res22 :: ${res22}")



  def helper23(arr: Array[Int]): Unit = {
    // This function demonstrates circular array traversal
    // For each starting position i, it prints indices in circular fashion
    
    for (i <- 0 until arr.length) { // Outer loop: Starting position
      
      if(arr(i) == 1) {
        println(s"i :: ${i}") 
      }

      // Inner loop: Traverse array circularly from position i
      // j starts from 1 because:
      // 1. We want to visit elements AFTER the current position i
      // 2. Starting from 0 would just revisit position i again
      // 3. This gives us n-1 steps forward from current position
      // 4. Combined with modulo, this creates the circular traversal
      for (j <- 1 until arr.length) {
        // Formula: (i + j) % length gives circular indices
        // Example for length 5:
        // i=0: (0+1)%5=1, (0+2)%5=2, (0+3)%5=3, (0+4)%5=4
        // i=1: (1+1)%5=2, (1+2)%5=3, (1+3)%5=4, (1+4)%5=0
        // i=2: (2+1)%5=3, (2+2)%5=4, (2+3)%5=0, (2+4)%5=1
        // And so on...
        val ind = (i + j) % arr.length

        println(s"i :: ${i}, j :: ${j}, ind :: ${ind}")
      }
    }
  }

  val input23 = Array(1, 2, 3, 4, 5)
  helper23(input23)


  def maxCircularSubArraySum(arr: Array[Int]): Int = {


    val m1 = maxSubArraySum(arr)

    if (m1 < 0) {

      m1

    } else {

      val curr_sum = arr.sum
      
      val new_arr = arr.map(-_)
      
      val m2 = curr_sum + maxSubArraySum(new_arr)
      
      math.max(m1, m2)

    }

  }

  val input24 = Array(-3, 8, -2, 4, -5, 6)
  val res24 = maxCircularSubArraySum(input24)
  println(s"res24 :: ${res24}")


  def majorityElement(arr: Array[Int]): Int = {

    val a = arr.zipWithIndex.groupBy(d => d._1).maxBy(_._2.length)._2

    if ( a.length > arr.length / 2) {

      a.head._2

    } else -1

  }

  val input25 = Array(1, 2, 2, 1, 1, 1, 2, 2, 2, 2)
  val res25 = majorityElement(input25)
  println(s"res25 :: ${res25}")



  def majorityElementBoyerMoore(arr: Array[Int]): Int = {

    var res = 0
    var count = 1

    for ( i <- 1 until arr.length) {

      if ( arr(i) == arr(res)) {
        count += 1
      } else count -= 1

      if ( count == 0 ) {

        res = i 
        count = 1
      }

    }

    count = 0

    for ( i <- 0 until arr.length) {

      if ( arr(i) == arr(res)) {
        count += 1
      }

    }

    if ( count > arr.length / 2) {

      res

    } else -1


    }

  val input26 = Array(1, 1, 2, 1, 1, 1, 2, 2, 2, 2)
  val res26 = majorityElementBoyerMoore(input26)
  println(s"res26 :: ${res26}")


  def majorityElementMooreFP(arr: Array[Int]): Int = {


    val res = arr.tail.foldLeft((1, 0))((acc, x) => {

      val (count, res) = acc

      if ( count == 0) {

        (1, x)

      } else if ( x == arr(res)) {

        (count + 1, res)

      } else (count - 1, res)

    })._2


    val count_of_candidate = arr.count(_ == arr(res))

    if ( count_of_candidate > arr.length / 2) {

      arr(res)

    } else -1

  }

  val input27 = Array(1, 1, 2, 1, 1, 1, 2, 2, 2, 2)
  val res27 = majorityElementMooreFP(input27)
  println(s"res27 :: ${res27}")


  def minConsecutiveFlips(arr: Array[Int]): Unit = {

    arr.zipWithIndex.tail.foreach{case (value, index) => {

      if ( value != arr(index -1)){
        if(value != arr(0)){
          println(s"from :: ${index} to ")
        } else {
          println(s"${index-1}")
        }
      }
    }}

    if ( arr(0) != arr(arr.length - 1)) {

      println(s"${arr.length - 1}")

    }

  }

  val input28 = Array(1, 0, 0, 1, 0, 0 )
  minConsecutiveFlips(input28)


  def maxSumOfKConsecutiveElements(arr: Array[Int], k: Int): Int = {


    arr.sliding(k).map(x => x.sum).max


  }

  val input29 = Array(1, 8, 30, -5, 20, 7)
  val res29 = maxSumOfKConsecutiveElements(input29, 3)
  println(s"res29 :: ${res29}")


  def maxSumOfKConsecutiveElements2(arr: Array[Int], k: Int): Int = {

    var res = Int.MinValue
    var curr_sum = arr.take(k).sum

    for ( i <- k until arr.length) {

      if(curr_sum > res) {

        res = curr_sum

      }
      curr_sum = curr_sum + arr(i) - arr( i - k)

    }

    // For the last window check 
    res = math.max(res, curr_sum)

    res

  }

  val input30 = Array(1, 8, 30, -5, 20, 7)
  val res30 = maxSumOfKConsecutiveElements2(input30, 3)
  println(s"res30 :: ${res30}")



  def maxSumOfKConsecutiveElements3(arr: Array[Int], k: Int): Int = {

    val initial_sum = arr.take(k).sum

    val (max_sum, current_sum) = arr.zipWithIndex
    .drop(k)
    .foldLeft((initial_sum, initial_sum)){case ((max_sum, current_sum), (value, indx))=> {

      val updated_sum = current_sum + value - arr(indx - k)
      val new_max_sum = math.max(max_sum, updated_sum)
      (new_max_sum, updated_sum)

    }}


    math.max(max_sum, current_sum)


  }

  val input31 = Array(1, 8, 30, -5, 20, 7)
  val res31 = maxSumOfKConsecutiveElements3(input31, 3)
  println(s"res31 :: ${res31}")


    // For non-negative numbers only
    def isSubArraySum(arr: Array[Int], target_sum: Int): Boolean = {
    var i = 0
    var j = 0
    var curr_sum = arr(0)

    while (j < arr.length) {

      if (curr_sum == target_sum) return true

      if (curr_sum < target_sum) {
        j += 1
        if (j < arr.length) curr_sum += arr(j)
      } else {
        curr_sum -= arr(i)
        i += 1
      }
    }

    false
  }

  val input32 = Array(1, 4, 20, 3, 10, 5)
  val res32 = isSubArraySum(input32, 33) 
  println(s"res32 :: ${res32}")


  def helper33(arr: Array[Int], target_sum: Int): Boolean = {

    var curr_sum = arr(0)
    var start = 0

    for ( end <- 1 until arr.length) {

      while ( curr_sum > target_sum && start < end - 1) {

        curr_sum -= arr(start)
        start += 1

      }

      if ( curr_sum == target_sum) return true

      if ( end < arr.length) {

        curr_sum += arr(end)

      }

    }

    curr_sum == target_sum

  }


  def nbonacciNumber(n: Int, m: Int): Array[Int] = {
    val res = scala.collection.mutable.ArrayBuffer.fill(n)(0)
    res(n - 1) = 1
    var start = 0
    var end = n - 1

    while (end < m - 1) {
      val curr_sum = res.slice(start, end + 1).sum
      res.append(curr_sum)
      start += 1
      end += 1
    }

    res.toArray
  }

  val res35 = nbonacciNumber(3, 8)
  println(s"res35 :: ${res35.mkString(",")}")



  // def nbonacciNumberFP(n: Int, m: Int): Array[Int] = {

  //   def build(res: List[Int], start: Int, end: Int): List[Int] = {
  //     if (end >= m) res.reverse
  //     else {
  //       val curr_sum = res.take(n).sum
  //       build(curr_sum :: res, start + 1, end + 1)
  //     }
  //   }

  //   val initial = List.fill(n - 1)(0) :+ 1
  //   build(initial.reverse, 0, n).reverse.toArray
  // }








  def nbonacciNumberFPRecursive(n: Int, m: Int): Array[Int] = {


    def build(initial : Array[Int]): Array[Int] = {

      if ( initial.length >= m) initial
      else {

        val new_element = initial.takeRight(n).sum

        val new_array = initial :+ new_element

        build(new_array)

      }
    }

    val initial_array = Array.fill(n-1)(0) :+ 1
    build(initial_array)

  }

  val res36 = nbonacciNumberFPRecursive(3, 8)
  println(s"res36 :: ${res36.mkString(",")}")



  def helper37(arr: Array[Int], l: Int, r: Int): Unit = {


    def build(initial: Array[Int], k: Int): Array[Int] = {

      if ( initial.length >= arr.length) initial
      else {

        val curr_sum = initial(k-1) + arr(k)
        val new_arr = initial :+ curr_sum

        build(new_arr, k+1)
      }

    }

    val initial_array = Array(arr(0))
    val prefix_sum = build(initial_array, 1)


    if ( l == 0) {

      prefix_sum(r)

    } else {

      prefix_sum(r) - prefix_sum(l-1)
      
    }

  }

  val input38 = Array(2, 8, 3, 9, 6, 5, 4)
  helper37(input38, 0, 2)



  def findEquilibrium(arr: Array[Int]): Unit = {
    var totalSum = arr.sum
    var leftSum = 0
    var flag = false

    for (i <- arr.indices) {
      totalSum -= arr(i)  // now totalSum is right sum

      if (leftSum == totalSum) {
        println(s"Equilibrium index found at $i, value: ${arr(i)}")
        flag = true
      }

      leftSum += arr(i)
    }

    if (!flag) println("No equilibrium index found")

  }

  val input39 = Array(3, 4, 8, -9, 20, 6)
  findEquilibrium(input39)
  
 

  /*

  Supp


  */

  


}
