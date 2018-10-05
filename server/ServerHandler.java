package server;

import common.*;

import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ServerHandler dedicated connection to the given client
 */

public class ServerHandler extends Comms implements Runnable {

    private Restaurant restaurant = new Restaurant();
    private User user = null;

    public ServerHandler(Socket s, ObjectInputStream ois, ObjectOutputStream oos){
        super(s,ois,oos);
    }

    @Override
    public void run() {

        //get dishes from the restaurant
        this.sendUpdateDishes();

        //await messages
        while(true) {

            //wait for the message
            Message message = this.receiveMessage();

            if (message != null) {

                restaurant = new Restaurant();

                //interpret the message
                switch (message.getType()) {

                    case "RegisterUser":

                        restaurant.getUsers().add(message.getRegisteredUser());
                        this.user = message.getRegisteredUser();
                        break;

                    case "Login":

                        boolean loggedIn = false;
                        Message messageToBeSent = new Message("LoginReply");

                        //look if can login
                        for (User user : restaurant.getUsers()) {

                            if (user.getName().equals(message.getUsername()) && user.getPassword().equals(message.getPassword())) {

                                loggedIn = true;
                                messageToBeSent.loginReply(user);
                                this.sendMessage(messageToBeSent);
                                this.user = user;
                                //send user's orders
                                this.sendAllOrders();
                                break;
                            }
                        }
                        //if cant login send null
                        if(!loggedIn) {
                            messageToBeSent.loginReply(null);
                            this.sendMessage(messageToBeSent);

                        }
                        break;

                    case "CommunicateOrder":

                        message.getOrder().collectDishes();
                        restaurant.getOrders().add(message.getOrder());
                        break;

                    case "CancelOrder":

                        for(Order order: restaurant.getOrders()){

                            if(order.getName().equals(message.getOrder().getName())){
                                restaurant.getOrders().remove(order);
                            }
                        }
                        break;

                    case "NeedPostcodes":

                        this.sendPostcodes(new Restaurant().getPostcodes());
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public void sendUpdateDishes(){
        Message message = new Message("UpdateDishes");
        List<Dish> dishes = new Restaurant().getStock().getDishes();
        message.updateDishes(dishes);
        sendMessage(message);
    }

    //send all the orders of a particular user
    public void sendAllOrders(){
        if(user != null){

            List<Order> userOrders = new ArrayList<>();
            for(Order order: restaurant.getOrders()){
                if(order.getUser().getName().equals(user.getName())){
                    userOrders.add(order);
                }
            }
            Message message = new Message("CommunicateOrderList");
            message.communicateOrderList(userOrders);
            this.sendMessage(message);
        }
    }

    public void sendPostcodes(List<Postcode> postcodes){
        Message message = new Message("Postcodes");
        message.setPostcodes(postcodes);
        sendMessage(message);
    }

    public User getUser() {
        return user;
    }
}
