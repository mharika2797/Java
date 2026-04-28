public class InterfaceSegregation {
    public static void main(String[] args) {
        System.out.println("Interfaces segregated with respect to their methods");

        OnClick once = new ClicksOnce();
        once.onClick();

        LongOnClick longClick = new ClicksLong();
        longClick.longOnClick();
    }
}


//Here we can have a single interface which stores both the onclick and longOnClick method
//But by doing like this any class which doesn't require either one of the methods has to
//override it as it is implementing it, so the methods although are very similar are
//segregated into two interfaces

interface OnClick{
    void onClick();
}

interface LongOnClick{
    void longOnClick();
}


class ClicksOnce implements OnClick{

    @Override
    public void onClick() {
        System.out.println("Single click");
    }
}

class ClicksLong implements LongOnClick{
    @Override
    public void longOnClick() {
        System.out.println("Long click");
    }
}

/*
Notes:
1) An interface's methods must be required by every implementing class —
    no class should be forced to implement a method it doesn't need
2) Interfaces must be segregated so that each interface only groups closely related methods
 */
