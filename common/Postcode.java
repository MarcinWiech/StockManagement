package common;

import java.io.Serializable;

/**
 * Postcode contains information about the distance of a postcode from the restaurant
 */

public class Postcode extends Model implements Serializable {

    private String code;
    private Number distance;

    public Postcode(String code, Number distance) {
        this.code = code;
        this.distance = distance;
    }

    @Override
    public String getName() {
        return code;
    }

    public Number getDistance() {
        return distance;
    }
}
