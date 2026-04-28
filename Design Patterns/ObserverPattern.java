package com.company;

import java.util.ArrayList;
import java.util.List;

public class ObserverPattern {
    public static void main(String[] args) {
        StockMarket apple = new StockMarket("AAPL", 150.0);

        // Traditional observer — separate class
        apple.addObserver(new EmailAlert("trader@example.com"));

        // Lambda observer — no class needed for simple logic
        apple.addObserver(stock ->
                System.out.println("SMS: " + stock.getSymbol() + " is now $" + stock.getPrice()));

        apple.setPrice(155.0);
        apple.setPrice(148.0);
    }
}

// Subject — maintains a list of observers and notifies them on state change
class StockMarket {
    private final String symbol;
    private double price;
    private final List<StockObserver> observers = new ArrayList<>();

    StockMarket(String symbol, double price) {
        this.symbol = symbol;
        this.price  = price;
    }

    void addObserver(StockObserver o)    { observers.add(o); }
    void removeObserver(StockObserver o) { observers.remove(o); }

    void setPrice(double price) {
        this.price = price;
        observers.forEach(o -> o.update(this));
    }

    double getPrice()  { return price; }
    String getSymbol() { return symbol; }
}

// @FunctionalInterface allows observers to be written as lambdas
@FunctionalInterface
interface StockObserver {
    void update(StockMarket stock);
}

// Traditional concrete observer
class EmailAlert implements StockObserver {
    private final String email;

    EmailAlert(String email) { this.email = email; }

    @Override
    public void update(StockMarket stock) {
        System.out.println("Email to " + email + ": " + stock.getSymbol() +
                           " changed to $" + stock.getPrice());
    }
}

/*
Notes:
1) Observer pattern defines a one-to-many dependency: when the subject changes state, all registered observers are notified automatically
2) Also called Publish-Subscribe or Event-Listener pattern
3) @FunctionalInterface on the observer lets you pass a lambda directly — no concrete class needed for simple callbacks
4) java.util.Observable is deprecated since Java 9; rolling a custom subject (as shown here) is the modern approach
5) Common uses: event systems, MVC (model notifies views), UI listeners, reactive streams
 */
