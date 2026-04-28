public class DependencyInversion {
    public static void main(String[] args) {
        BankInterest i = new PNC();
        //In the above case we are directly dependent on the PNC class.
        //By the Dependency Inversion Principle we must not be directly dependent on a class.
        //So the dependency must be inverted — we use a factory method to decouple
        //the caller from the specific implementation.
        System.out.println(i.getInterest());
        //Using the factory method to invert the dependency
        FactoryBankInterest f = new FactoryBankInterest();
        BankInterest b = f.getBankInterest("JP");
        System.out.println(b.getInterest());
    }
}

class FactoryBankInterest{
    public BankInterest getBankInterest(String bankName){
        if(bankName == null){
            return null;
        }
        else if(bankName.equalsIgnoreCase("PNC")){
            return new PNC();
        }
        else if(bankName.equalsIgnoreCase("JP")){
            return new JP();
        }
        else{
            return null;
        }
    }
}


abstract class BankInterest{
    int interest;
    abstract int getInterest();
}

class PNC extends BankInterest{
    @Override
    int getInterest() {
        return 1;
    }
}

class JP extends  BankInterest{
    @Override
    int getInterest() {
        return 2;
    }
}

/*
Notes
1_ Dependency Inversion states that high level modules must never depend on low level modules directly —
    that means there should be no hard dependency between a caller class and a specific implementation class
2_ If there is a dependency it should be inverted using a factory method or dependency injection
 */