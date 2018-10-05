package common;

import java.io.Serializable;
import java.util.*;

/**
 * Staff is responsible for making dishes
 */

public class Staff extends Model implements Runnable, Serializable {

    //member variables
    private String name,status;
    private Restaurant restaurant;
    //This list says how many times the dish must be made
    private static Map<Dish,Integer> dishesToBeMade = java.util.Collections.synchronizedMap(new HashMap<Dish, Integer>());
    //This list contains all the dishes that are or will be prepared
    private static List<Dish> securedDishes = Collections.synchronizedList(new ArrayList<>());
    private boolean running = true;
    private Dish dishToBeMade;

    //constructor
    public Staff(String name){
        this.name = name;
        this.status = "Idle";
        Thread watch = new Thread(this);
        watch.start();
    }


    @Override
    public void run() {

        while(running) {

            restaurant = new Restaurant();
            try {
                lookForDishesToMake();
                cookDish();
            }catch (NullPointerException e){
                running = false;
            }
        }

    }

    private void lookForDishesToMake(){

        synchronized (restaurant.getStock().getDishesInStock()) {

            //loop through all the dishes in the restaurant
            for (Dish dish : restaurant.getStock().getDishesInStock().keySet()) {
                synchronized (securedDishes) {

                    //if the dish is not in the list of dishes to be prepared
                    if (!securedDishes.contains(dish)) {

                        //if under threshold in the stock
                        if (dish.getRestockThreshold().intValue() > restaurant.getStock().getDishesInStock().get(dish).intValue()) {

                            //add the dish to both list
                            this.securedDishes.add(dish);
                            //calculate how many times the dish has to be made in order to reach threshold + restock amount
                            this.dishesToBeMade.put(dish, dish.getRestockAmount().intValue() + dish.getRestockThreshold().intValue() - restaurant.getStock().getDishesInStock().get(dish).intValue());
                        }
                    }
                }
            }
        }
    }

    private void cookDish(){

        boolean canCook = false;

        //if there are dishes that can be cooked
        if (dishesToBeMade.size() > 0) {

            synchronized (dishesToBeMade){
                synchronized (restaurant.getStock().getDishesInStock()) {

                    //if there is enough ingredients for a particular dish --> can cook
                    for (Dish dish : dishesToBeMade.keySet()) {

                        if (enoughIngredients(dish) && dishesToBeMade.get(dish) > 0) {
                            dishToBeMade = dish;
                            canCook = true;
                            break;
                        }
                    }

                    if(canCook && running){

                        //get ingredients from the stock
                        for(Ingredient ingredient: dishToBeMade.getRequiredIngredients().keySet()){
                            int currentNumber = restaurant.getStock().getIngredientsInStock().get(ingredient).intValue();
                            restaurant.getStock().getIngredientsInStock().put(ingredient, currentNumber-(dishToBeMade.getRequiredIngredients().get(ingredient).intValue()));
                        }

                        //update list of dishes to be made
                        int currentDishNumber = dishesToBeMade.get(dishToBeMade);
                        if(currentDishNumber == 1 && running){
                            dishesToBeMade.remove(dishToBeMade);
                        }
                        else{
                            dishesToBeMade.put(dishToBeMade, currentDishNumber-1);
                        }
                    }
                }
            }

            if(canCook && running) {

                //cook for a random time 20-60 seconds
                Random ran = new Random();
                int cookingTime = (ran.nextInt(40) + 20)*1000;

                try {
                    this.setStatus("Cooking " + dishToBeMade.getName());
                    Thread.sleep(cookingTime);

                    synchronized (restaurant.getStock().getDishesInStock()) {

                        //add cooked dish to the stock
                        if (running) {
                            restaurant.getStock().getDishesInStock().put(dishToBeMade, restaurant.getStock().getDishesInStock().get(dishToBeMade).intValue() + 1);
                        }
                    }

                        //if dishesToBeMade does not contain secured dish, remove it from secured dishes
                        if(!dishesToBeMade.containsKey(dishToBeMade) && running){
                            securedDishes.remove(dishToBeMade);
                        }
                        dishToBeMade = null;


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //finished cooking
        this.status = "Idle";

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //check if enough ingredients in the stock
    private boolean enoughIngredients(Dish dish){

        if(dish != null && dish.getRequiredIngredients() != null) {

            for (Ingredient ingredient : dish.getRequiredIngredients().keySet()) {

                if (dish.getRequiredIngredients().get(ingredient).intValue() > restaurant.getStock().getIngredientsInStock().get(ingredient).intValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void fireStaff() {
        restaurant.getStaffList().remove(this);
        this.running = false;
    }

    public void resetActivities(){
        this.dishesToBeMade.clear();
        this.securedDishes.clear();
    }

    //getters and setters

    @Override
    public String getName() {
        return name;
    }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
