package common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User is a physical client of a restaurant.
 * User class contains all the necessary information about the user including username, password, distance from the restaurant.
 */

public class User extends Model implements Serializable{

    private String username,password,address;
    private Postcode postcode;
    private HashMap<Dish, Number> basket = new HashMap<>();
    private boolean loggedIn = false;

    //constructor
    public User(String username, String password, String address, Postcode postcode) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    //calculate the cost of the current basket
    public Number calculateBasketCost(){

        Number cost = 0;

        if(basket.size() > 0) {
            for (Dish dish : basket.keySet()) {
                Number tempcost = dish.getPrice().intValue() * basket.get(dish).intValue();
                cost = cost.intValue() + tempcost.intValue();
            }
        }
        return cost;
    }

    //change basket to the order
    public Order placeAnOrder(){

        Order tempOrder = new Order(this, new HashMap<>(basket));
        basket.clear();
        return tempOrder;
    }

    //getters and setters

    public HashMap<Dish, Number> getBasket() {
        return basket;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getName() {
        return username;
    }

    public Postcode getPostcode() {
        notifyUpdate("postcode", this.postcode, postcode);
        return postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
