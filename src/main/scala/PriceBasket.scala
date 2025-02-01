package pricebasket

object PriceBasket {

  // Define product prices in pence
  private val productPrices: Map[String, Int] = Map(
    "soup"   -> 65,  // 65p per tin
    "bread"  -> 80,  // 80p per loaf
    "milk"   -> 130, // 130p per bottle
    "apples" -> 100  // 100p per bag
  )

  // Case classes to hold the result of pricing
  case class Discount(offer: String, amount: Int)
  case class PricingResult(subtotal: Int, discounts: List[Discount], total: Int)

  /**
   * Validate the basket by separating valid items from unknown items.
   * Returns a tuple of (validItems, unknownItems).
   */
  def validateBasket(items: List[String]): (List[String], List[String]) = {
    val (valid, unknown) = items.partition(item => productPrices.contains(item.toLowerCase))
    (valid.map(_.toLowerCase), unknown)
  }

  /**
   * Calculate pricing for a given basket of items.
   * Items are provided as a List[String] (e.g. List("apples", "milk", "bread"))
   */
  def calculatePricing(items: List[String]): PricingResult = {
    // Assume items are already normalized and filtered to valid items.
    val counts: Map[String, Int] = items.groupBy(identity).view.mapValues(_.size).toMap

    // Calculate subtotal
    val subtotal: Int = items.map(productPrices).sum

    // Calculate Apples discount: 10% off each bag (i.e. 10p discount per bag)
    val applesCount = counts.getOrElse("apples", 0)
    val applesDiscount: Int = applesCount * 10  // 10% of 100p

    // Calculate Soup and Bread offer: For every 2 soups, one bread is half price (i.e. 40p discount)
    val soupCount = counts.getOrElse("soup", 0)
    val breadCount = counts.getOrElse("bread", 0)
    val eligibleBreadDiscounts = Math.min(breadCount, soupCount / 2)
    val breadDiscount: Int = eligibleBreadDiscounts * 40  // half of 80p is 40p

    // Gather discount offers (only include if discount > 0)
    val discountList = List(
      if (applesDiscount > 0) Some(Discount("Apples 10% off", applesDiscount)) else None,
      if (breadDiscount > 0) Some(Discount("Bread 50% off", breadDiscount)) else None
    ).flatten

    val totalDiscount = discountList.map(_.amount).sum
    val total = subtotal - totalDiscount

    PricingResult(subtotal, discountList, total)
  }

  /** Format totals as pounds, e.g., £3.10 */
  def formatTotal(pence: Int): String = {
    f"£${pence / 100}%d.${pence % 100}%02d"
  }

  /** Format discounts: if less than 100p, show in pence (e.g., 10p), otherwise as pounds. */
  def formatDiscount(pence: Int): String = {
    if (pence < 100) s"${pence}p"
    else f"£${pence / 100}%d.${pence % 100}%02d"
  }

  /**
   * The main function which parses command-line arguments and prints the output.
   *
   * This version adds two features:
   *
   * 1. If any provided product is not recognized, a message is printed to inform the user.
   * 2. If the basket qualifies for the soup & bread offer (i.e. at least 2 soups) but contains no bread,
   *    the user is prompted to add discounted bread to the basket.
   *
   * Usage: PriceBasket item1 item2 item3 ...
   */
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Please provide a list of items. For example:")
      println("PriceBasket Apples Milk Bread")
    } else {
      val rawBasket = args.toList

      // Validate basket: separate valid items from unknown ones.
      val (validBasket, unknownItems) = validateBasket(rawBasket)
      if (unknownItems.nonEmpty) {
        println(s"The following items are not recognized: ${unknownItems.mkString(", ")}")
        println("Please check and re-enter the basket if needed.")
      }

      // Check for the eligible soup-based bread discount.
      val soupCount = validBasket.count(_ == "soup")
      val breadCount = validBasket.count(_ == "bread")
      var finalBasket = validBasket

      if (soupCount / 2 > 0 && breadCount == 0) {
        println("Hey, you are missing a discount on bread, would you like to add it to your basket? (Yes/Y to confirm)")
        val answer = scala.io.StdIn.readLine().trim.toLowerCase
        if (answer == "yes" || answer == "y") {
          println("How many loaves would you like to add?")
          val loavesInput = scala.io.StdIn.readLine().trim
          val additionalBreadCount = try {
            loavesInput.toInt
          } catch {
            case _: NumberFormatException => 0
          }
          if (additionalBreadCount > 0) {
            finalBasket = validBasket ++ List.fill(additionalBreadCount)("bread")
          } else {
            println("Invalid number entered, proceeding with original basket.")
          }
        }
      }

      // Calculate the pricing result based on the (possibly updated) basket.
      val result = calculatePricing(finalBasket)

      // Output the results.
      println(s"Subtotal: ${formatTotal(result.subtotal)}")
      if (result.discounts.isEmpty)
        println("(No offers available)")
      else
        result.discounts.foreach { discount =>
          println(s"${discount.offer}: ${formatDiscount(discount.amount)}")
        }
      println(s"Total price: ${formatTotal(result.total)}")
    }
  }
}
