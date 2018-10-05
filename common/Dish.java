package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Dish extends Model contains all the necessary information about the dish instance in the system.
 */

public class Dish extends Model implements Serializable {

    private String name, description;
    private Number restockThreshold,restockAmount;
    private Number price;
    private HashMap<Ingredient,Number> requiredIngredients = new HashMap<Ingredient,Number>();

    public Dish(String name){
        this.name = name;
    }

    public Dish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
        notifyUpdate();
        this.name = name;
        this.description = description;
        this.price = price;
        this.restockThreshold = restockThreshold;
        this.restockAmount = restockAmount;
    }

    public void removeRequiredIngredients(Ingredient ingredient) {
        notifyUpdate("requiredIngredients",this.requiredIngredients,requiredIngredients);
        this.requiredIngredients.remove(ingredient);
    }

    //getters and setters for the dish

    public void addRequiredIngredients(Ingredient ingredient, Number quantity) {
        notifyUpdate("requiredIngredients",this.requiredIngredients,requiredIngredients);
        this.requiredIngredients.put(ingredient, quantity);
    }

    public void setRequiredIngredients(Map<Ingredient, Number> requiredIngredients) {
        notifyUpdate("requiredIngredients",this.requiredIngredients,requiredIngredients);
        ArrayList<Ingredient> tempIng = new ArrayList<>(requiredIngredients.keySet());
        ArrayList<Number> tempNum = new ArrayList<>(requiredIngredients.values());

        for(int i=0; i < requiredIngredients.size(); i++){
            this.requiredIngredients.put(tempIng.get(i),tempNum.get(i).intValue());
        }
    }

    public void setRestockThreshold(Number restockThreshold) {
        notifyUpdate("restockThreshold",this.restockThreshold,restockThreshold);
        this.restockThreshold = restockThreshold;
    }

    public void setRestockAmount(Number restockAmount) {
        notifyUpdate("restockAmount",this.restockAmount,restockAmount);
        this.restockAmount = restockAmount;
    }

    public void setPrice(Number price) {
        notifyUpdate("price",this.price,price);
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Number getPrice() {
        return price;
    }

    public HashMap<Ingredient, Number> getRequiredIngredients() {
        return requiredIngredients;
    }

    public Number getRestockThreshold() {
        return restockThreshold;
    }

    public Number getRestockAmount() {
        return restockAmount;
    }

    @Override
    public void notifyUpdate() {
        super.notifyUpdate();
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        super.addUpdateListener(listener);
    }


}
