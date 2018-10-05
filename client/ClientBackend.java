package client;

import common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClientBackend implements ClientInterface, and handles data processing on client side
 */

public class ClientBackend implements ClientInterface {

    private Restaurant restaurant = new Restaurant();
    private boolean loggedIn = false;
    private List<UpdateListener> updateListeners = new ArrayList<UpdateListener>();
    private Client clientCommunication;

    public ClientBackend(){
        clientCommunication = new Client();
        Thread client = new Thread(clientCommunication);
        client.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public User register(String username, String password, String address, Postcode postcode) {

        if(clientCommunication.isConnected()) {
            User tempUser = new User(username, password, address, postcode);
            restaurant.getUsers().add(tempUser);
            tempUser.setLoggedIn(true);
            clientCommunication.sendRegisterUser(tempUser);
            loggedIn = true;
            return tempUser;
        }
        return null;
    }

    @Override
    public User login(String username, String password) {

        clientCommunication.sendLogin(username, password);

        //waits to receive the answer from the client
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if(clientCommunication.getUser() != null){
            loggedIn = true;
        }
        return clientCommunication.getUser();
    }

    @Override
    public List<Postcode> getPostcodes() {
        return restaurant.getPostcodes();
    }

    @Override
    public List<Dish> getDishes() {
        return restaurant.getStock().getDishes();
    }

    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        return user.getBasket();
    }

    @Override
    public Number getBasketCost(User user) {
        return user.calculateBasketCost();
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        user.getBasket().put(dish,quantity);
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        user.getBasket().remove(dish);
        user.getBasket().put(dish,quantity);
    }

    @Override
    public Order checkoutBasket(User user) {

        Order tempOrder = user.placeAnOrder();
        restaurant.getOrders().add(tempOrder);

        //communicate the order
        clientCommunication.sendOrder(tempOrder);
        return tempOrder;
    }

    @Override
    public void clearBasket(User user) {
        user.getBasket().clear();
    }

    @Override
    public List<Order> getOrders(User user) {
        if(user == null){
            return new ArrayList<Order>();
        }
        //restaurant contains only orders of this user
        return restaurant.getOrders();
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.isComplete();
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public void cancelOrder(Order order) {

        restaurant.getOrders().remove(order);
        clientCommunication.sendCancelOrder(order);
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

        if(loggedIn) {
            listener.updated(new UpdateEvent());
        }
        if(loggedIn) {
            updateListeners.add(listener);
        }
        //create a thread that refreshes the window


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(loggedIn) {
                        listener.updated(new UpdateEvent());
                    }
                }
            }
        }).start();


    }

    @Override
    public void notifyUpdate() {
        //Call the updated method on every update listener
        if(loggedIn) {
            for (UpdateListener listener : updateListeners) {
                listener.updated(new UpdateEvent());
            }
        }

    }
}

