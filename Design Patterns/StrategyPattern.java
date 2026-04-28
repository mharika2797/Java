package com.company;

import java.util.ArrayList;
import java.util.List;

public class StrategyPattern {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new CartItem("Book", 30));
        cart.addItem(new CartItem("Pen", 5));

        // Traditional strategy objects
        cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
        cart.checkout();

        cart.setPaymentStrategy(new PayPalPayment("user@example.com"));
        cart.checkout();

        // Lambda strategy — no separate class needed for simple cases
        cart.setPaymentStrategy(amount -> System.out.println("Crypto payment of $" + amount));
        cart.checkout();
    }
}

// record — immutable value type (Java 16+), replaces a plain data class with getters
record CartItem(String name, int price) {}

class ShoppingCart {
    private final List<CartItem> items = new ArrayList<>();
    private PaymentStrategy paymentStrategy;

    void addItem(CartItem item)                  { items.add(item); }
    void setPaymentStrategy(PaymentStrategy s)   { this.paymentStrategy = s; }

    void checkout() {
        int total = items.stream().mapToInt(CartItem::price).sum();
        paymentStrategy.pay(total);
    }
}

// @FunctionalInterface lets strategies be expressed as lambdas
@FunctionalInterface
interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    CreditCardPayment(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public void pay(int amount) {
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        System.out.println("Paid $" + amount + " with credit card ending in " + last4);
    }
}

class PayPalPayment implements PaymentStrategy {
    private final String email;
    PayPalPayment(String email) { this.email = email; }

    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " via PayPal (" + email + ")");
    }
}

/*
Notes:
1) Strategy pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable at runtime
2) Also called Policy pattern
3) @FunctionalInterface allows passing a lambda for simple one-line strategies — no concrete class needed
4) Follows Open/Closed Principle — add a new payment method without touching ShoppingCart
5) Difference from Template Method: Strategy uses composition (has-a relationship), Template Method uses inheritance (is-a)
 */
