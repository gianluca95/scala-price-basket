package pricebasket

import org.scalatest.funsuite.AnyFunSuite
import PriceBasket._

class PriceBasketSpec extends AnyFunSuite {

  test("Basket with a single item (Milk) - no offers apply") {
    val result = calculatePricing(List("milk"))
    assert(result.subtotal == 130)
    assert(result.discounts.isEmpty)
    assert(result.total == 130)
  }

  test("Basket with Apples, Milk, Bread - only Apples offer applies") {
    val result = calculatePricing(List("apples", "milk", "bread"))
    // Apples = 100, Milk = 130, Bread = 80; subtotal = 310
    assert(result.subtotal == 310)

    // Apples discount: 1 * 10 = 10
    assert(result.discounts.exists(_.offer == "Apples 10% off"))
    val applesDisc = result.discounts.find(_.offer == "Apples 10% off").map(_.amount).getOrElse(0)
    assert(applesDisc == 10)

    // Total = 310 - 10 = 300
    assert(result.total == 300)
  }

  test("Basket with Soup, Soup, Bread - only Soup & Bread offer applies") {
    val result = calculatePricing(List("soup", "soup", "bread"))
    // Soup = 65 each, Bread = 80; subtotal = 65+65+80 = 210
    assert(result.subtotal == 210)

    // For 2 soups, eligible for one bread discount: 40p discount
    assert(result.discounts.exists(_.offer == "Bread 50% off"))
    val breadDisc = result.discounts.find(_.offer == "Bread 50% off").map(_.amount).getOrElse(0)
    assert(breadDisc == 40)

    // Total = 210 - 40 = 170
    assert(result.total == 170)
  }

  test("Basket with both offers: Soup, Soup, Apples, Bread, Apples") {
    // Items breakdown:
    // Soup: 2 (each 65p), Bread: 1 (80p), Apples: 2 (each 100p)
    // Subtotal = 2*65 + 80 + 2*100 = 410
    val result = calculatePricing(List("soup", "soup", "apples", "bread", "apples"))
    assert(result.subtotal == 410)

    // Apples discount: 2 * 10 = 20; Bread discount: 1 * 40 = 40; Total discount = 60
    val applesDisc = result.discounts.find(_.offer == "Apples 10% off").map(_.amount).getOrElse(0)
    val breadDisc  = result.discounts.find(_.offer == "Bread 50% off").map(_.amount).getOrElse(0)
    assert(applesDisc == 20)
    assert(breadDisc == 40)
    assert(result.total == 410 - 60)
  }

  test("Basket with no applicable offers (empty basket)") {
    val result = calculatePricing(List())
    assert(result.subtotal == 0)
    assert(result.discounts.isEmpty)
    assert(result.total == 0)
  }

  test("validateBasket correctly separates valid and unknown items") {
    val (valid, unknown) = validateBasket(List("Soup", "unknown", "Bread", "Error"))
    assert(valid.sorted == List("bread", "soup").sorted)
    assert(unknown.sorted == List("unknown", "Error").sorted)
  }
}
