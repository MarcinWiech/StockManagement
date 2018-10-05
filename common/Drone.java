package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class Drone extends Model implements Runnable, Serializable{

    private Number speed;
    private String status,name;
    private Restaurant restaurant;
    private static LinkedBlockingQueue<Ingredient> ingredientsInProgress = new LinkedBlockingQueue<>();
    private static List<Ingredient> ingredientsToBeTaken = Collections.synchronizedList(new ArrayList<>());
    private Ingredient ingredientToBeDelivered;
    private boolean running = true;

    public Drone(){
        this.name = UUID.randomUUID().toString();
    }

    public Drone(Number speed){
        this.speed = speed;
        this.name = UUID.randomUUID().toString();
        this.status = "Idle";
        Thread watch = new Thread(this);
        watch.start();
    }

    @Override
    public void run() {

        while (running){

            restaurant = new Restaurant();

            //looks for the lacking ingredients
            try {
                lookForLackingIngredients();
                fetchIngredient();
            }
            catch (NullPointerException e){
                running = false;
            }
            if(ingredientsInProgress.size() == 0){
                deliverOrders();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchIngredient(){
        if(ingredientsInProgress.size()>0 && speed.intValue() >0){

            ingredientToBeDelivered = ingredientsInProgress.poll();
            this.status = "Fetching " + ingredientToBeDelivered.getName();
            long distance = ingredientToBeDelivered.getSupplier().getDistance().longValue();

            //calculate travel time
            Number time = (((double)distance/speed.intValue())*360*1000*2);

            try {
                Thread.sleep(time.longValue());

                //add delivered ingredients to stock
                synchronized (restaurant.getStock()){
                    restaurant.getStock().getIngredientsInStock().put(ingredientToBeDelivered,ingredientToBeDelivered.getRestockThreshold().intValue()+ingredientToBeDelivered.getRestockAmount().intValue());
                }

                //remove from the list of ingredients to deliver
                this.ingredientsToBeTaken.remove(ingredientToBeDelivered);
                ingredientToBeDelivered = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        this.status = "Idle";
    }

    //look what ingredients need to be restored
    private void lookForLackingIngredients(){

        synchronized (restaurant.getStock().getIngredientsInStock()){

            for(Ingredient ingredient: restaurant.getStock().getIngredientsInStock().keySet()){

                if(!ingredientsToBeTaken.contains(ingredient)) {

                    if (ingredient.getRestockThreshold().intValue() > restaurant.getStock().getIngredientsInStock().get(ingredient).intValue()) {

                        this.ingredientsToBeTaken.add(ingredient);

                        try {
                            this.ingredientsInProgress.put(ingredient);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public Number getSpeed() {
        return speed;
    }

    public void setSpeed(Number speed) {
        notifyUpdate("speed",this.getSpeed(),speed);
        this.speed = speed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    //kill the drone when it finishes current delivery or idle
    public void killme(){
        this.running = false;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void deliverOrders(){
        Order toDeliver = null;

        //get order to delivery
        synchronized (restaurant.getOrders()){
            for(Order order: restaurant.getOrders()){
                if(order.getStatus().equals("Ready for delivery")){
                    order.setStatus("Being delivered");
                    this.setStatus("Delivering to " + order.getUser().getName());
                    toDeliver = order;
                    break;
                }
            }
        }

        //if there is an order to be deliver do it
        if(toDeliver != null){

            //calculate one way time
            Number time = (((double)toDeliver.getUser().getPostcode().getDistance().floatValue()/speed.intValue())*360*1000);

            //deliver
            try {
                Thread.sleep(time.longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            toDeliver.setStatus("Completed");
            toDeliver.setComplete(true);
            this.setStatus("Coming back form " + toDeliver.getUser().getName());

            //come back
            try {
                Thread.sleep(time.longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.setStatus("Idle");
        }
    }

    public void resetActivities(){
        this.ingredientsInProgress.clear();
        this.ingredientsToBeTaken.clear();
    }
}
