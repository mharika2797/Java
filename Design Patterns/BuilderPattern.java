package com.company;

public class BuilderPattern {
    public static void main(String[] args) {
        House h1 = new House.Builder("Concrete", "Tiles")
                .garage(true)
                .garden(true)
                .pool(false)
                .build();
        System.out.println(h1);

        // Minimal build — optional fields default to false
        House h2 = new House.Builder("Wood", "Shingles").build();
        System.out.println(h2);
    }
}

class House {
    private final String foundation;
    private final String roof;
    private final boolean hasGarage;
    private final boolean hasGarden;
    private final boolean hasPool;

    private House(Builder b) {
        this.foundation = b.foundation;
        this.roof       = b.roof;
        this.hasGarage  = b.hasGarage;
        this.hasGarden  = b.hasGarden;
        this.hasPool    = b.hasPool;
    }

    @Override
    public String toString() {
        return "House{foundation='" + foundation + "', roof='" + roof +
               "', garage=" + hasGarage + ", garden=" + hasGarden + ", pool=" + hasPool + "}";
    }

    static class Builder {
        private final String foundation;
        private final String roof;
        private boolean hasGarage;
        private boolean hasGarden;
        private boolean hasPool;

        Builder(String foundation, String roof) {
            this.foundation = foundation;
            this.roof = roof;
        }

        Builder garage(boolean v)  { this.hasGarage = v; return this; }
        Builder garden(boolean v)  { this.hasGarden = v; return this; }
        Builder pool(boolean v)    { this.hasPool   = v; return this; }

        House build() { return new House(this); }
    }
}

/*
Notes:
1) Builder pattern constructs a complex object step by step, separating construction from its representation
2) Solves the telescoping constructor anti-pattern — instead of one constructor per combination of optional fields,
    the builder holds state until build() is called
3) Required fields go in the Builder constructor; optional fields use setter-style methods that return `this` for chaining
4) Unlike Abstract Factory (families of related objects), Builder focuses on constructing one complex object incrementally
5) Real-world Java examples: StringBuilder, Locale.Builder, HttpRequest.newBuilder()
 */
