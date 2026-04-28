//Author: Phani Teja Kesha
//Simple Factory Design Pattern Example

package com.company;
public class FactoryMethodPattern {
    public static void main(String[] args) {
        //Interesting thing here is we are creating an object for the factory method only once and always using that method
        //to create new class objects for the parent class
        FactoryInternetPlan f = new FactoryInternetPlan();
        InternetPlan f1 = f.getPlanType("DomesticPlan");
        f1.getRate();
        System.out.println(f1.rate);
        f1.calculateBill(100);
        InternetPlan f2 = f.getPlanType("CommercialPlan");
        f2.getRate();
        f2.calculateBill(100);
    }

}

//Parent class InternetPlan
abstract class InternetPlan {
    protected double rate;
    abstract void getRate();
    public void calculateBill(int units) {
        System.out.println(units * rate);
    }
}

//Three subclasses which extend the abtract class InternetPlan
class DomesticPlan extends InternetPlan {
    void getRate() { rate = 10; }
}

class CommercialPlan extends InternetPlan {
    void getRate() { rate = 20; }
}

class InstitutionalPlan extends InternetPlan {
    void getRate() { rate = 5; }
}

//Factory class which sends the required subclass object
class FactoryInternetPlan {
    public InternetPlan getPlanType(String planType) {
        System.out.println(planType);
        if (planType == null) return null;
        return switch (planType.toLowerCase()) {
            case "domesticplan"     -> new DomesticPlan();
            case "commercialplan"   -> new CommercialPlan();
            case "institutionalplan"-> new InstitutionalPlan();
            default -> null;
        };
    }
}

/*
Notes:
1) This implements Simple Factory (also called Static Factory): one factory class decides which object to instantiate.
    This is NOT the GoF Factory Method — in true Factory Method, an abstract Creator defines the factory method
    and concrete subclasses override it to decide which product to instantiate.
2) promotes loose coupling in the code
3) Examples are internet and different types, Phone call and its different calls, UMBC parent class subclass Masters, bachelors, arts etc
    and fees are calculated by each subclass,create an example with respect to the company
 */
