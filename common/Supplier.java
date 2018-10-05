package common;

import java.io.Serializable;

/**
 * Supplier class contains information about a supplier that is name and distance from the restaurant.
 */

public class Supplier extends Model implements Serializable {

    private String name;
    private Number distance;

    //constructor
    public Supplier(String name, Number distance) {
        this.name = name;
        this.distance = distance;
    }

    //getters and setters

    public Number getDistance() {
        return this.distance;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {

        return getName();

    }

}
