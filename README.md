# PriceBasket

PriceBasket is a Scala-based application that calculates the total price of a basket of goods while taking into account special offers. The pricing logic—including discounts for apples and a combined soup & bread offer—is driven by unit tests to ensure correctness through test-driven development.

## Features

- **Product Pricing:**

  Calculates the subtotal based on predefined prices:
  - Soup: 65p per tin
  - Bread: 80p per loaf
  - Milk: 130p per bottle
  - Apples: 100p per bag
 

- **Special Offers:**  
  - **Apples Discount:** 10% off each bag of apples (i.e., 10p discount per bag).
  - **Soup & Bread Offer:** For every 2 tins of soup, a loaf of bread is available at 50% off (i.e., 40p discount per eligible bread).

- **Interactive Enhancements:**  
  - If unrecognized products are entered, the program prints a message listing those items so you can check your basket.
  - If your basket qualifies for the soup & bread offer but contains no bread, you will be prompted to optionally add discounted bread loaves.

- **Unit Test Driven:**  
  The core business logic (pricing and basket validation) is covered by unit tests to ensure reliability and to facilitate further development.

## Prerequisites

Before you begin, ensure that you have the following installed:

- [Git](https://git-scm.com/)
- [Java JDK 8+](https://www.oracle.com/java/technologies/javase-downloads.html)
- [SBT (Scala Build Tool)](https://www.scala-sbt.org/download.html)

## Getting Started

### 1. Clone the Repository

Open your terminal and run:

```bash
git clone https://github.com/yourusername/pricebasket.git
cd pricebasket
```

### 2. Build the Project

Compile the project using SBT:

```bash
sbt compile
```

### 3. Run Unit Tests

Make sure everything is working as expected by running the tests:

```bash
sbt test
```

### 4. Run the Application

You can run the application by providing a list of items as command-line arguments. For example, to run a basket with two soups:

```bash
sbt "run Soup Soup"
```

## Expected Output

When you run the application, the output will include:
- A **Subtotal** that reflects the total price of all items before discounts.
- Any **Discounts** that have been applied (e.g., "Apples 10% off" or "Bread 50% off").
- The **Total Price** after discounts have been subtracted.

### Example 1: Basket with Apples, Milk, and Bread

If you run:
```
sbt "run Apples Milk Bread"
```

You can expect output similar to:

```
Subtotal: £3.10
Apples 10% off: 10p
Total price: £3.00
```

## Interactive Behavior

- Unknown Products:

If you include any products that are not recognized (i.e., not defined in the product list), the application will print a message such as

```
The following items are not recognized: unknownItem1, unknownItem2
Please check and re-enter the basket if needed.
```

- Discounted Bread Prompt:

If your basket qualifies for the soup & bread offer (at least 2 soups) but contains no bread, you will see a prompt like:

```
Hey, you are missing a discount on bread, would you like to add it to your basket? (Yes/Y to confirm)
```

If you confirm by typing "Yes" or "Y", you will then be asked:

```
How many loaves would you like to add?
```

The additional loaves of bread will be added to your basket and the pricing will be recalculated accordingly.

So for example, if you run:

```
sbt "run Soup Soup"
```

Since there are 2 soups (qualifying for a bread discount) but no bread, the program will prompt:

```
Hey, you are missing a discount on bread, would you like to add it to your basket? (Yes/Y to confirm)
```

If you respond with "Yes" (or "Y") and add 1 loaf of bread, the expected output becomes:

```
Subtotal: £2.10
Bread 50% off: 40p
Total price: £1.70
```

## Project Structure

```
pricebasket/
├── src/
│   ├── main/
│   │   └── scala/
│   │       └── PriceBasket.scala   # Main application and pricing logic
│   └── test/
│       └── scala/
│           └── PriceBasketSpec.scala  # Unit tests for the pricing logic
├── .gitignore   # Git ignore file for build artifacts, IDE settings, etc.
└── README.md    # This file
```
