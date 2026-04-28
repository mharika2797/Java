package com.company;

import java.util.Arrays;

public class NullObjectPattern {
    public static void main(String[] args) {
        AbstractCustomer customer1 = CustomerFactory.getCustomer("Phani");
        AbstractCustomer customer2 = CustomerFactory.getCustomer("K");
        AbstractCustomer customer3 = CustomerFactory.getCustomer("Teja");
        AbstractCustomer customer4 = CustomerFactory.getCustomer("Ke");

        System.out.println("Customers list");
        System.out.println(customer1.getCustomerName());
        System.out.println(customer2.getCustomerName());
        System.out.println(customer3.getCustomerName());
        System.out.println(customer4.getCustomerName());
    }
}

//Abstract class which has is empty and customer name methods
// sealed restricts subclassing to only the two known implementations
abstract sealed class AbstractCustomer permits RealCustomer, NullCustomer {
    protected String name;
    public abstract boolean isEmpty();
    public abstract String getCustomerName();
}

class RealCustomer extends AbstractCustomer {

    public RealCustomer(String name) {
        this.name = name;
    }

    @Override
    public String getCustomerName() { return name; }

    @Override
    public boolean isEmpty() { return false; }
}

class NullCustomer extends AbstractCustomer {

    @Override
    public String getCustomerName() { return "Person not present in the database!"; }

    @Override
    public boolean isEmpty() { return true; }
}

class CustomerFactory {

    public static final String[] names = {"Phani", "Teja", "Kesha"};

    public static AbstractCustomer getCustomer(String name) {
        return Arrays.stream(names)
                .filter(n -> n.equalsIgnoreCase(name))
                .findFirst()
                .<AbstractCustomer>map(RealCustomer::new)
                .orElseGet(NullCustomer::new);
    }
}
