package client;

import common.*;
import common.Message;
import java.io.*;
import java.lang.String;
import java.net.*;

/**
 * Client Class handles client communication with the server.
 */

public class Client extends Comms implements Runnable {

    private User user;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    private Restaurant restaurant;
    private boolean connected = false;

    @Override
    public void run() {

        Socket socket = null;

        try {

            boolean connected = false;
            while(!connected) {
                try {
                    socket = new Socket("localhost", 5056);
                    this.connected = true;
                    connected = true;
                }catch (Exception e){

                }
            }

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            this.setInputStream(inputStream);
            this.setOutputStream(outputStream);

            this.needPostcodes();
            //catch signals from the server
            while(true){

                restaurant = new Restaurant();

                //get message
                Message message = this.receiveMessage();
                if(message != null) {

                    //interpret message
                    switch (message.getType()) {

                        case "LoginReply":
                            this.user  = message.getRegisteredUser();
                            break; // optional

                        case "UpdateDishes":
                            this.restaurant = new Restaurant();
                            restaurant.getStock().setDishes(message.getDishes());
                            restaurant.setDishes(message.getDishes());
                            break;

                        case "CommunicateOrderList":
                            restaurant.getOrders().clear();
                            restaurant.setOrders(message.getOrderList());
                            break;

                        case "Postcodes":
                            restaurant.getPostcodes().clear();
                            restaurant.setPostcodes(message.getPostcodes());

                        default:
                            break;
                    }
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //sends new user to the server
    public void sendRegisterUser(User user){

        Message message = new Message("RegisterUser");
        message.registerUser(user);
        this.sendMessage(message);

    }

    //sends credentials to the server
    public void sendLogin(String username, String password){

        Message message = new Message("Login");
        message.login(username,password);
        this.sendMessage(message);
    }

    //sends order to the server
    public void sendOrder(Order order){

        Message message = new Message("CommunicateOrder");
        message.communicateOrder(order);
        this.sendMessage(message);
    }

    //sends an information to cancel the order
    public void sendCancelOrder(Order order){

        Message message = new Message("CancelOrder");
        message.communicateOrder(order);
        this.sendMessage(message);
    }

    public void needPostcodes(){

        Message message = new Message("NeedPostcodes");
        this.sendMessage(message);
    }

    public User getUser() {
        return user;
    }

    public boolean isConnected() {
        return connected;
    }
}
