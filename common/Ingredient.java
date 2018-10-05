package common;

import java.io.Serializable;

/**
 * Ingredient extends Model contains all the necessary information about the ingredient instance in the system.
 */

public class Ingredient extends Model implements Serializable {

    private String name, unit;
    private Supplier supplier;
    private Number restockThreshold,restockAmount;


    public Ingredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {
        this.name = name;
        this.unit = unit;
        this.supplier = supplier;
        this.restockThreshold = restockThreshold;
        this.restockAmount = restockAmount;
    }

    //getters and setters

    public String getUnit() {
        return unit;
    }

    public void setSupplier(Supplier supplier) {
        notifyUpdate("supplier",this.supplier,supplier);
        this.supplier = supplier;
    }

    public void setRestockThreshold(Number restockThreshold) {
        notifyUpdate("restockThreshold",this.restockThreshold,restockThreshold);
        this.restockThreshold = restockThreshold;
    }

    public void setRestockAmount(Number restockAmount) {
        notifyUpdate("restockAmount",this.restockAmount,restockAmount);
        this.restockAmount = restockAmount;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Number getRestockThreshold() {
        return restockThreshold;
    }

    public Number getRestockAmount() {
        return restockAmount;
    }

    @Override
    public String getName() {
        return name;
    }

}
