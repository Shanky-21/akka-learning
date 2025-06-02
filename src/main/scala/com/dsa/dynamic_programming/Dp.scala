package com.dsa.dynamic_programming

object Dp extends App {

  // Functional approach using immutable Map for memoization
  def lcs(s1: String, s2: String): Int = {
    
    def lcsHelper(m: Int, n: Int, memo: Map[(Int, Int), Int]): (Int, Map[(Int, Int), Int]) = {
      val key = (m, n)
      
      // Check if already computed
      memo.get(key) match {
        case Some(result) => (result, memo)
        case None =>
          val (result, newMemo) = if (m == 0 || n == 0) {
            (0, memo)
          } else if (s1(m - 1) == s2(n - 1)) {
            val (subResult, updatedMemo) = lcsHelper(m - 1, n - 1, memo)
            (1 + subResult, updatedMemo)
          } else {
            val (result1, memo1) = lcsHelper(m - 1, n, memo)
            val (result2, memo2) = lcsHelper(m, n - 1, memo1)
            (math.max(result1, result2), memo2)
          }
          
          val finalMemo = newMemo + (key -> result)
          (result, finalMemo)
      }
    }
    
    val (result, _) = lcsHelper(s1.length, s2.length, Map.empty)
    result
  }

  // Alternative: Pure functional approach without explicit memoization
  def lcsPure(s1: String, s2: String): Int = {
    def helper(i: Int, j: Int): Int = {
      if (i >= s1.length || j >= s2.length) 0
      else if (s1(i) == s2(j)) 1 + helper(i + 1, j + 1)
      else math.max(helper(i + 1, j), helper(i, j + 1))
    }
    helper(0, 0)
  }

  // Bottom-up DP approach (most efficient and functional)
  def lcsDP(s1: String, s2: String): Int = {
    val m = s1.length
    val n = s2.length
    val dp = Array.ofDim[Int](m + 1, n + 1)
    
    for {
      i <- 0 to m
      j <- 0 to n
    } {
      if (i == 0 || j == 0) dp(i)(j) = 0
      else if (s1(i - 1) == s2(j - 1)) dp(i)(j) = dp(i - 1)(j - 1) + 1
      else dp(i)(j) = math.max(dp(i - 1)(j), dp(i)(j - 1))
    }
    
    dp(m)(n)
  }

  val s1 = "abcdefgh"
  val s2 = "defghijkl"

  println(s"Functional with Map memoization: ${lcs(s1, s2)}")
  println(s"Pure functional (slow): ${lcsPure(s1, s2)}")
  println(s"Bottom-up DP: ${lcsDP(s1, s2)}")
}
