package common;

import java.io.Serializable;
import java.util.List;

/**
 * Message class can convey all the necessary information between the client and the server
 */

public class Message implements Serializable {

    private String type, username, password;
    private User user;
    private List<Dish> dishes;
    private Order order;
    private List<Order> orderList;
    private List<Postcode> postcodes;

    public Message(String type) {
        this.type = type;
    }

    //messages

    public void registerUser(User user){
        this.user = user;
    }

    public void login(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void loginReply(User user){
        this.user = user;
    }

    public void communicateOrder(Order order){
        this.order = order;
    }

    public void updateDishes(List<Dish> dishes){
       this.dishes = dishes;
    }

    //access member variables

    public User getRegisteredUser() {
        return user;
    }

    public String getType() {
        return this.type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



    public List<Dish> getDishes() {
        return dishes;
    }



    public Order getOrder() {
        return order;
    }

    public void communicateOrderList(List<Order> orderList){
        this.orderList = orderList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    public void setPostcodes(List<Postcode> postcodes) {
        this.postcodes = postcodes;
    }
}
