package com.company;

public class SingletonPattern {
    public static void main(String[] args) {
        //Early instatntiation of singleton class
        SingletonEarly x = SingletonEarly.getObject();
        SingletonEarly y = SingletonEarly.getObject();
        SingletonEarly z = SingletonEarly.getObject();
        //SingletonEarly l = new SingletonEarly(); Errror as default constructor is private
        x.print();
        y.print();
        z.print();
        //Lazy instantiation of singleton class
        SingletonLazy x1 = SingletonLazy.getObject();
        SingletonLazy y1 = SingletonLazy.getObject();
        SingletonLazy z1 = SingletonLazy.getObject();
        //SingletonLazy l = new SingletonLazy();
        x1.print();
        y1.print();
        z1.print();
        // Enum singleton — recommended modern approach
        SingletonEnum.INSTANCE.print();
    }
}

class SingletonEarly {
    private static SingletonEarly obj = new SingletonEarly();
    private SingletonEarly() {}  //Why this?
    public static SingletonEarly getObject() { return obj; }
    public void print() { System.out.println(obj.hashCode()); }
}

class SingletonLazy {
    private static volatile SingletonLazy obj;  // volatile prevents instruction reordering in double-checked locking
    private SingletonLazy() {}
    public static SingletonLazy getObject() {
        if (obj == null) {
            synchronized (SingletonLazy.class) {
                if (obj == null) {
                    obj = new SingletonLazy();
                }
            }
        }
        return obj;
    }
    public void print() { System.out.println(obj.hashCode()); }
}

// Enum singleton — thread-safe and serialization-safe by the JVM guarantee; no boilerplate needed
enum SingletonEnum {
    INSTANCE;
    public void print() { System.out.println(this.hashCode()); }
}

/*
Notes:
1) define a class that has only instance and provides a global point of access to it is singleton pattern
2) Three implementations: early, lazy (double-checked locking), and enum
3) Advantage is saves memory as each object is not created at each request but reused again and again
    used in logging, caching , thread pools, config settings etc
4) to create a singleton pattern, we require static member, private constructer, static factory method.
5) volatile on the lazy instance is required — without it, the CPU can reorder writes and another thread
    may observe a partially constructed object before the null check completes
6) Enum singleton is the most recommended modern approach — JVM guarantees one instance,
    handles serialization correctly, and is immune to reflection attacks
 */
