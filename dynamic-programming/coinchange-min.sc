def coinChange(coins: Array[Int], amount: Int): Int = {
  val dp = Array.fill[Option[Int]](amount + 1)(None)
  dp(0) = Some(0)
  for {
    coin <- coins
    i <- coin to amount
  } yield {
    //either dp(i) or dp(i - coin)
    dp(i) = (dp(i), dp(i - coin)) match {
      case (None, Some(prev)) =>
        Some(prev + 1)
      case (Some(current), Some(prev)) =>
        Some(current.min(prev + 1))
      case (existing, None) =>
        existing
    }
  }

  dp.last.getOrElse(-1)
}

// println(coinChange(Array(1, 2, 5), 11))
// println(coinChange(Array(37,233,253,483), 7163))
println(coinChange(Array(2,5,10,1), 27))
