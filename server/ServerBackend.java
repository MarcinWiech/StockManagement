package server;

import common.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ServerBackend implements ServerInterface, and handles data processing on server side
 */


public class ServerBackend implements ServerInterface {

    private Restaurant restaurant = new Restaurant();
    private Server server;
    private List<UpdateListener> updateListeners = new ArrayList<UpdateListener>();

    //constructor
    public ServerBackend() throws IOException {

        //load data
        DataPersistence dataPersistence = new DataPersistence();


        //start server
        server = new Server();

        //save data automatically

        Thread saveDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    //dataPersistence.saveAll("data.txt");

                    try {
                        dataPersistence.saveToAFile("saved.txt");
                    }
                    catch (NullPointerException e){
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        saveDataThread.start();

    }

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        new Configuration(filename).getConfiguration();
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {

    }

    @Override
    public void setStock(Dish dish, Number stock) {

        restaurant.getStock().getDishesInStock().put(dish,stock);
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {

        restaurant.getStock().getIngredientsInStock().put(ingredient,stock);
    }

    @Override
    public List<Dish> getDishes() {

        return restaurant.getStock().getDishes();
    }

    @Override
    public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {

        Dish tempDish = new Dish(name,description,price.floatValue(),restockThreshold.intValue(),restockAmount.intValue());
        restaurant.getStock().getDishesInStock().put(tempDish,0);
        restaurant.getStock().getDishes().add(tempDish);

        //send updated dishes to all clients
        for(ServerHandler serverHandler: server.getActiveUsers()){
            try {
                serverHandler.sendUpdateDishes();
            }
            catch(NullPointerException e){

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        restaurant.setStock(restaurant.getStock());
        return tempDish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {

        if(restaurant.getStock().getDishes().remove(dish)){

            //send updated dishes to all clients
            for(ServerHandler serverHandler: server.getActiveUsers()){
                try {
                    serverHandler.sendUpdateDishes();
                }
                catch(NullPointerException e){

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else{
            throw new UnableToDeleteException("Dish not in stock.");
        }

    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {

        dish.addRequiredIngredients(ingredient, quantity);
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {

        dish.removeRequiredIngredients(ingredient);
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {

        dish.setRequiredIngredients(recipe);
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {

        dish.setRestockThreshold(restockThreshold);
        dish.setRestockAmount(restockAmount);
    }

    @Override
    public Number getRestockThreshold(Dish dish) {
        return dish.getRestockAmount();
    }

    @Override
    public Number getRestockAmount(Dish dish) {
        return dish.getRestockAmount();
    }

    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        return dish.getRequiredIngredients();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        return restaurant.getStock().getDishesInStock();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return restaurant.getStock().getIngredients();
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {

        Ingredient tempIngredient = new Ingredient(name,unit,supplier,restockThreshold,restockAmount);
        restaurant.getStock().getIngredients().add(tempIngredient);
        restaurant.getStock().getIngredientsInStock().put(tempIngredient,0);
        restaurant.setStock(restaurant.getStock());

        return tempIngredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {

        if (restaurant.getStock().getIngredients().remove(ingredient)){
            restaurant.getStock().getIngredientsInStock().remove(ingredient);
        }
        else{
            throw new UnableToDeleteException("No such ingredient in stock");
        }
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        ingredient.setRestockAmount(restockAmount);
        ingredient.setRestockThreshold(restockThreshold);
    }

    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return ingredient.getRestockThreshold();
    }

    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return ingredient.getRestockAmount();
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        return restaurant.getStock().getIngredientsInStock();
    }

    @Override
    public List<Supplier> getSuppliers() {
        return restaurant.getSuppliers();
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier temp = new Supplier(name, distance);
        restaurant.getSuppliers().add(temp);
        notifyUpdate();
        return temp;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        restaurant.getSuppliers().remove(supplier);
        notifyUpdate();
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    @Override
    public List<Drone> getDrones() {
        return restaurant.getDrones();
    }

    @Override
    public Drone addDrone(Number speed) {

        Drone tempDrone = new Drone(speed);
        restaurant.getDrones().add(tempDrone);
        notifyUpdate();
        return tempDrone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {


        if(restaurant.getDrones().remove(drone)){
            drone.killme();
        }
        else{
            throw new UnableToDeleteException("No such a drone to remove.");
        }
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus();
    }

    @Override
    public List<Staff> getStaff() {
        return restaurant.getStaffList();
    }

    @Override
    public Staff addStaff(String name) {

        Staff tempStaff = new Staff(name);
        restaurant.getStaffList().add(tempStaff);
        notifyUpdate();
        return tempStaff;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {

        if(restaurant.removeStaffMember(staff)){
            staff.fireStaff();
        }
        else{
            throw new UnableToDeleteException("Such staff member does not exist.");
        }
        notifyUpdate();
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    @Override
    public List<Order> getOrders() {
        return restaurant.getOrders();
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        restaurant.getOrders().remove(order);
        notifyUpdate();
    }

    @Override
    public Number getOrderDistance(Order order) {
        return order.getUser().getPostcode().getDistance();
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
    public List<Postcode> getPostcodes() {
        return restaurant.getPostcodes();
    }

    @Override
    public void addPostcode(String code, Number distance) {
        restaurant.addPostcode(new Postcode(code,distance));
        notifyUpdate();
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        restaurant.getPostcodes().remove(postcode);
    }

    @Override
    public List<User> getUsers() {
        return restaurant.getUsers();
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {

        if(restaurant.getUsers().remove(user)){}
        else{
            throw new UnableToDeleteException("No such an user to remove.");
        }
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    for (ServerHandler serverHandler : server.getActiveUsers()) {
                        serverHandler.sendAllOrders();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void notifyUpdate() {
        //Call the updated method on every update listener
        for(UpdateListener listener : updateListeners) {
            listener.updated(new UpdateEvent());
        }
    }
}
