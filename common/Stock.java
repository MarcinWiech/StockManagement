package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stock contains ingredients and dishes that are stored in the Sushi restaurant. Class Restaurant contains an instance of a stock.
 */

public class Stock implements Serializable {

    //contained ingredients and dishes
    private HashMap<Dish, Number> dishesInStock = new HashMap<>();
    private List<Dish> dishes = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private HashMap<Ingredient, Number> ingredientsInStock = new HashMap<>();

    //getters and setters

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public HashMap<Ingredient, Number> getIngredientsInStock() {
        return ingredientsInStock;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public HashMap<Dish, Number> getDishesInStock() {
        return dishesInStock;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

}
