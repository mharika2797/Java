package com.company;

public class AbstractFactoryPattern {
    public static void main(String[] args) {
        AbstractFactory obj1 = FactoryCreator.getFactory("Bank");
        Bank b = obj1.getBank("Pnc");
        AbstractFactory obj2 = FactoryCreator.getFactory("Loan");
        Loan l = obj2.getLoan("HomeLoan");
        System.out.println(b.getBankName());
        l.getInterestRate(10);
        System.out.println(l.rate);
        l.calculateLoan(100, 10);
    }
}

//This the factory Creator class to get the factories by passing an information such as Bank or loan
class FactoryCreator {
    public static AbstractFactory getFactory(String name) {
        if (name == null) return null;
        return switch (name.toLowerCase()) {
            case "bank" -> new BankFactory();
            case "loan" -> new LoanFactory();
            default -> null;
        };
    }
}

// The main abstract class extends both factory's and is like a factory having a number of factory's
abstract class AbstractFactory {
    abstract Bank getBank(String bankName);
    abstract Loan getLoan(String loanName);
}

//The bank factory class which takes the subclasses and emit the object of parent class refering to the subclass
class BankFactory extends AbstractFactory {
    public Bank getBank(String bankName) {
        if (bankName == null) return null;
        return switch (bankName.toLowerCase()) {
            case "pnc"        -> new Pnc();
            case "bofa"       -> new Bofa();
            case "capitalone" -> new CapitalOne();
            default           -> null;
        };
    }
    public Loan getLoan(String loanName) { return null; }
}

//The Loan factory class which takes the subclasses and emit the object of parent class refering to the subclass
class LoanFactory extends AbstractFactory {
    public Loan getLoan(String loanName) {
        if (loanName == null) return null;
        return switch (loanName.toLowerCase()) {
            case "homeloan"      -> new HomeLoan();
            case "educationloan" -> new EducationLoan();
            case "businessloan"  -> new BusinessLoan();
            default              -> null;
        };
    }
    public Bank getBank(String bankName) { return null; }
}

//Bank is a interface which has three subclasses
interface Bank {
    String getBankName();
}

class Bofa implements Bank {
    private final String BankName = "Bofa";
    @Override public String getBankName() { return BankName; }
}

class Pnc implements Bank {
    private final String BankName = "PNC";
    @Override public String getBankName() { return BankName; }
}

class CapitalOne implements Bank {
    private final String BankName = "CapitalOne";
    @Override public String getBankName() { return BankName; }
}

//Loan is a parent class which has three subclasses
abstract class Loan {
    double rate;
    abstract void getInterestRate(double rate);
    void calculateLoan(double loanAmount, double tenure) {
        System.out.println(loanAmount * tenure);
    }
}

class HomeLoan extends Loan {
    @Override
    void getInterestRate(double rate) { this.rate = 10; }
}

class BusinessLoan extends Loan {
    @Override
    void getInterestRate(double rate) { this.rate = 20; }
}

class EducationLoan extends Loan {
    @Override
    void getInterestRate(double rate) { this.rate = 5; }
}

/*
Notes:
1) Abstract factory pattern says that just define an interface or abstract class for creating families of related objects but without
    specifying thier concrete subclasses, This means abstract factory lets a class returns a factory of classes
2) Also known as a kit
3) Simple meaning is its a factory having a factory of subclasses and uses a absract class to create this factory of classes
 */
