package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Restaurants can be treated as a database. It contains all the information about the system.
 */

public class Restaurant extends Model implements Serializable{

    //all the data
    private static Stock stock = new Stock();
    private static List<Supplier> suppliers = new ArrayList<>();
    private static List<Order> orders = new ArrayList<>();
    private static List<Postcode> postcodes = new ArrayList<>();
    private static List<Staff> staffList = new ArrayList<>();
    private static List<Drone> drones = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static List<Dish> dishes = stock.getDishes();

    //getters and setters

    public List<User> getUsers() {
        return users;
    }

    public Stock getStock() {
        return stock;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Postcode> getPostcodes() {
        return postcodes;
    }

    public void addPostcode(Postcode postcode) {
        this.postcodes.add(postcode);
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public boolean removeStaffMember(Staff staff){
        return this.getStaffList().remove(staff);
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public void loadSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public void setStock(Stock stock) {
        notifyUpdate("stock", this.stock, stock);
        this.stock = stock;
    }

    public void setOrders(List<Order> orders) {
        notifyUpdate("orders", this.orders, orders);
        this.orders = orders;
    }

    public void setPostcodes(List<Postcode> postcodes) {
        notifyUpdate("postcodes", this.postcodes, postcodes);
        this.postcodes = postcodes;
    }

    public void loadStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    public void loadDrones(List<Drone> drones) {
        this.drones = drones;
    }

    public void loadUsers(List<User> users){
        Restaurant.users = users;
    }


    public void setDishes(List<Dish> dishes){
        notifyUpdate("dishes", this.dishes, dishes);
        this.dishes = dishes;
    }


    public static List<Dish> getDishes() {
        return dishes;
    }


    @Override
    public String getName() {
        return null;
    }
}
