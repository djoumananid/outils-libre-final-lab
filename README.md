# Pricing & Discount Engine

A Java pricing engine built as a Gradle project — lab exercise in **refactoring**, **unit testing**, and **Git workflow**.

---

## Project Structure

```
pricing-engine/
├── build.gradle
├── settings.gradle
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── src/
│   ├── main/java/com/lab/pricing/
│   │   ├── CustomerType.java       ← enum (tax rate + loyalty discount)
│   │   ├── DiscountCode.java       ← enum (promo codes → rates)
│   │   ├── OrderItem.java          ← value object (price × qty)
│   │   ├── PriceBreakdown.java     ← result object (full breakdown)
│   │   ├── PricingEngine.java      ← core logic (refactored)
│   │   └── Main.java               ← demo entry point
│   └── test/java/com/lab/pricing/
│       ├── OrderItemTest.java
│       ├── DiscountCodeTest.java
│       └── PricingEngineTest.java
└── tests/
    └── integration_test.py         ← Python integration tests
```

---

## Quick Start

```bash
# Build
./gradlew build

# Run demo
./gradlew run

# Run JUnit tests
./gradlew test

# Run Python integration tests
python tests/integration_test.py
```

---

## Pricing Rules

| Step | Rule |
|------|------|
| **Subtotal** | Sum of `unitPrice × quantity` for all items |
| **Promo discount** | `SAVE10` = 10%, `SAVE20` = 20%, `SAVE30` = 30% of subtotal |
| **Loyalty discount** | VIP customers: extra 5% off post-promo amount |
| **Tax (REGULAR)** | 8% of taxable amount |
| **Tax (VIP)** | 6% of taxable amount |
| **Final price** | `taxableAmount + tax` |

---

## Worked Example

```
Order: 1× $200.00, CustomerType: VIP, DiscountCode: SAVE20

  subtotal         = 200.00
  promo  (20%)     =  40.00   → after promo = 160.00
  loyalty (5% VIP) =   8.00   → total discount = 48.00
  taxableAmount    = 152.00
  tax (6% VIP)     =   9.12
  finalPrice       = 161.12
```

---

## Git Workflow (Recommended Commit Sequence)

```bash
git init
git add .
git commit -m "chore: initialize Gradle project structure"

git add src/main/java/com/lab/pricing/PricingEngine.java
git commit -m "feat: add bad-design starter code for refactoring exercise"

git add src/test/java/
git commit -m "test: add initial JUnit tests for PricingEngine"

git add src/main/java/com/lab/pricing/CustomerType.java \
        src/main/java/com/lab/pricing/DiscountCode.java
git commit -m "refactor: extract CustomerType and DiscountCode enums"

git add src/main/java/com/lab/pricing/OrderItem.java \
        src/main/java/com/lab/pricing/PriceBreakdown.java
git commit -m "refactor: add OrderItem value object and PriceBreakdown result"

git add src/main/java/com/lab/pricing/PricingEngine.java
git commit -m "refactor: rewrite PricingEngine with clean separation of concerns"

git add tests/integration_test.py
git commit -m "test: add Python integration tests"
```

---

## What Changed in the Refactor?

| Before (bad design) | After (refactored) |
|---|---|
| Single method `calc()` | `calculate()` + private helpers |
| String params `"VIP"`, `"SAVE10"` | Type-safe enums |
| Magic numbers `0.08`, `0.05` | Constants in enums |
| No validation | `validateInputs()` with clear messages |
| No result structure, returns raw `double` | `PriceBreakdown` with all fields |
| Parallel raw lists (`List<Double>`, `List<Integer>`) | `List<OrderItem>` value objects |
