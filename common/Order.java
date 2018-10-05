package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Order class contains information about the order, and can partially execute the order
 */

public class Order extends Model implements Serializable {

    private String status, name;
    private HashMap<Dish,Number> basket = new HashMap<>();
    private boolean isComplete = false;
    private User user;
    private Number cost;


    //constructor
    public Order(User user, HashMap<Dish, Number> basket) {
        this.user = user;
        this.basket = basket;
        this.name = UUID.randomUUID().toString();
        this.status = "In progress";
    }

    //collect dishes for the order
    public void collectDishes(){

        //create temporary basket to keep track of dishes that are lacking in the order
        HashMap<Dish,Number> dishesToCollect = new HashMap<>(basket);

        //create a thread that will take care of collecting the dishes
        Thread collector = new Thread(new Runnable() {

            @Override
            public void run() {

                boolean dishesCollected = false;

                //while dishes are not collected
                while(!dishesCollected){

                    Restaurant restaurant = new Restaurant();

                    synchronized (restaurant.getStock().getDishesInStock()) {

                        //loop through all the dishes in the restaurant
                        for (Dish dish : restaurant.getStock().getDishesInStock().keySet()) {

                            //if an order contains a dish
                            if (dishesToCollectContain(dish)) {

                                Dish tempDish = findDishInDishesToCollect(dish);

                                //if there is a dish in the stock and an order still requires it
                                if (restaurant.getStock().getDishesInStock().get(dish).intValue() > 0 && dishesToCollect.get(tempDish).intValue() > 0) {

                                    //get dish from the stock, and decrement dish in the order
                                    restaurant.getStock().getDishesInStock().put(dish, restaurant.getStock().getDishesInStock().get(dish).intValue() - 1);
                                    dishesToCollect.put(tempDish, dishesToCollect.get(tempDish).intValue() - 1);

                                    //if the order collected this dish, remove
                                    if (dishesToCollect.get(tempDish).intValue() == 0) {
                                        dishesToCollect.remove(tempDish);
                                    }
                                }
                            }
                        }
                    }

                    //if order has been collected
                    if(dishesToCollect.isEmpty()){

                        setStatus("Ready for delivery");
                        dishesCollected = true;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //check if order contains the dish from the restaurant
            private boolean dishesToCollectContain(Dish dish){

                for(Dish dish1: dishesToCollect.keySet()){
                    if(dish.getName().equals(dish1.getName())){
                        return true;
                    }
                }
                return false;
            }

            //check if order contains the dish from the restaurant
            private Dish findDishInDishesToCollect(Dish dish){

                for(Dish dish1: dishesToCollect.keySet()){
                    if(dish.getName().equals(dish1.getName())){
                        return dish1;
                    }
                }
                return null;
            }


        });
        collector.start();
    }

    //getters and setters

    public String getStatus() {
        return status;
    }

    public void setComplete(boolean complete) {
        notifyUpdate("isComplete",this.isComplete,isComplete);
        isComplete = complete;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public HashMap<Dish, Number> getBasket() {
        return basket;
    }

    @Override
    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }

    public Number getCost(){
        float price = 0;
        for(Dish dish: this.getBasket().keySet()){
            price = price + dish.getPrice().intValue()*this.getBasket().get(dish).intValue();
        }
        this.cost=price;
        return cost;
    }

    public void setStatus(String status) {
        notifyUpdate("status", this.status, status);
        this.status = status;
    }
}

