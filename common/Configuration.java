package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Configuration interprets files of a specific structure and loads them onto the system
 */
public class Configuration {

    private String filename;
    private Restaurant restaurant = new Restaurant();

    public Configuration(String filename) {
        this.filename = filename;
    }

    public void getConfiguration() {

        try {
            //initialise BufferedReader
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            String line = bufferedReader.readLine();

            //remove data from the restaurant
            synchronized (restaurant) {

                restaurant.setStock(new Stock());
                restaurant.getSuppliers().clear();
                for (Order order : restaurant.getOrders()) {
                    order.setComplete(true);
                }
                restaurant.getOrders().clear();
                restaurant.getPostcodes().clear();
                try {
                    for (Staff staff : restaurant.getStaffList()) {
                        staff.resetActivities();
                        staff.fireStaff();
                    }
                }
                catch(Exception e){
                }
                restaurant.getStaffList().clear();
                for (Drone drone : restaurant.getDrones()) {
                    drone.killme();
                    drone.resetActivities();
                }
                restaurant.getDrones().clear();
                restaurant.getUsers().clear();
            }

            while (line != null) {

                //divide line on ":"
                List<String> dividedString = Arrays.asList(line.split(":"));

                //interpret lines depending on the first word in the line
                switch (dividedString.get(0)) {

                    case "SUPPLIER":

                        restaurant.getSuppliers().add(new Supplier(dividedString.get(1), (Number) Integer.parseInt(dividedString.get(2))));
                        restaurant.loadSuppliers(restaurant.getSuppliers());
                        break;

                    case "INGREDIENT":

                        Supplier tempSupplier = null;

                        for (Supplier supplier : restaurant.getSuppliers()) {
                            if (supplier.getName().equals(dividedString.get(3))) {
                                tempSupplier = supplier;
                            }
                        }

                        Ingredient tempIngredient = new Ingredient(dividedString.get(1), dividedString.get(2), tempSupplier, Integer.parseInt(dividedString.get(4)), Integer.parseInt(dividedString.get(5)));
                        restaurant.getStock().getIngredientsInStock().put(tempIngredient, 0);
                        restaurant.getStock().getIngredients().add(tempIngredient);
                        break;

                    case "DISH":

                        Dish tempDish = new Dish(dividedString.get(1), dividedString.get(2), Integer.parseInt(dividedString.get(3)), Integer.parseInt(dividedString.get(4)), Integer.parseInt(dividedString.get(5)));
                        List<String> dishIngredients = Arrays.asList(dividedString.get(6).split(","));

                        for (String string : dishIngredients) {
                            List<String> ingredientAndQuantiy = Arrays.asList(string.split("\\*"));

                            synchronized (restaurant.getStock().getIngredients()) {

                                for (Ingredient ingredient : restaurant.getStock().getIngredients()) {

                                    if (ingredientAndQuantiy.get(1).replaceAll("\\s+", "").equals(ingredient.getName())) {
                                        tempDish.addRequiredIngredients(ingredient, Integer.parseInt(ingredientAndQuantiy.get(0).replaceAll("\\s+", "")));
                                    }
                                }
                            }

                        }

                        synchronized (restaurant.getStock().getDishesInStock()) {
                            restaurant.getStock().getDishesInStock().put(tempDish, 0);
                            restaurant.getStock().getDishes().add(tempDish);
                        }

                        restaurant.setStock(restaurant.getStock());
                        break;

                    case "POSTCODE":

                        restaurant.getPostcodes().add(new Postcode(dividedString.get(1), Integer.parseInt(dividedString.get(2))));
                        break;

                    case "USER":

                        Postcode tempPostcode = null;

                        for (Postcode postcode : restaurant.getPostcodes()) {
                            if (postcode.getName().equals(dividedString.get(4))) {
                                tempPostcode = postcode;
                            }
                        }

                        restaurant.getUsers().add(new User(dividedString.get(1), dividedString.get(2), dividedString.get(3), tempPostcode));
                        break;

                    case "ORDER":

                        User tempUser = null;

                        for (User user : restaurant.getUsers()) {
                            if (user.getName().equals(dividedString.get(1))) {
                                tempUser = user;
                            }
                        }

                        List<String> basketStaff = Arrays.asList(dividedString.get(2).split(","));

                        HashMap<Dish, Number> basket = new HashMap<>();

                        for (String string : basketStaff) {
                            List<String> quantityAndStaff = Arrays.asList(string.split("\\*"));

                            for (Dish dish : restaurant.getStock().getDishes()) {
                                if (dish.getName().equals(quantityAndStaff.get(1).trim())) {
                                    basket.put(dish, Integer.parseInt(quantityAndStaff.get(0).replaceAll("\\s+", "")));
                                }
                            }
                        }

                        Order tempOrder = new Order(tempUser, basket);
                        tempOrder.collectDishes();
                        restaurant.getOrders().add(tempOrder);

                        break;

                    case "STOCK":

                        synchronized (restaurant.getStock()) {

                            for (Dish dish : restaurant.getStock().getDishes()) {

                                if (dish.getName().equals(dividedString.get(1))) {
                                    restaurant.getStock().getDishesInStock().put(dish, Integer.parseInt(dividedString.get(2)));
                                }
                            }

                            for (Ingredient ingredient : restaurant.getStock().getIngredients()) {

                                if (ingredient.getName().equals(dividedString.get(1))) {
                                    restaurant.getStock().getIngredientsInStock().put(ingredient, Integer.parseInt(dividedString.get(2)));
                                }
                            }
                        }
                        restaurant.setStock(restaurant.getStock());

                        break;

                    case "STAFF":

                        Staff tempStaff = new Staff(dividedString.get(1));
                        restaurant.getStaffList().add(tempStaff);
                        break;

                    case "DRONE":

                        Drone tempDrone = new Drone(Integer.parseInt(dividedString.get(1)));
                        restaurant.getDrones().add(tempDrone);
                        Thread thread = new Thread(tempDrone);
                        break;

                    default:
                        break;
                }

                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("BufferedReader error");
            e.printStackTrace();
        }
    }
}
