package server;
import common.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DataPersistence {

    public void saveToAFile(String filename){

        File file = new File(filename);
        try (PrintWriter printWriter = new PrintWriter(filename)) {

            Restaurant restaurant = new Restaurant();
            synchronized (restaurant){

                for(Supplier supplier: restaurant.getSuppliers()){
                    printWriter.println("SUPPLIER:" + supplier.getName() + ":" + supplier.getDistance());
                }

                printWriter.println();

                for(Ingredient ingredient: restaurant.getStock().getIngredients()){
                    printWriter.println("INGREDIENT:" + ingredient.getName() + ":" + ingredient.getUnit() + ":" +  ingredient.getSupplier() + ":" + ingredient.getRestockThreshold() + ":" + ingredient.getRestockAmount());
                }

                printWriter.println();

                for(Dish dish: restaurant.getStock().getDishes()){
                    printWriter.print("DISH:" + dish.getName() + ":" + dish.getDescription() + ":" + dish.getPrice() + ":" + dish.getRestockThreshold() + ":" + dish.getRestockAmount() + ":");

                    int counter = 0;
                    for(Ingredient ingredient: dish.getRequiredIngredients().keySet()){

                        printWriter.print(dish.getRequiredIngredients().get(ingredient) + " * " + ingredient.getName());
                        counter = counter + 1;

                        if(counter < dish.getRequiredIngredients().size()){
                            printWriter.print(",");
                        }
                        else{
                            printWriter.println();
                        }
                    }
                }

                printWriter.println();

                for(Postcode postcode: restaurant.getPostcodes()){

                    printWriter.println("POSTCODE:" + postcode.getName() + ":" + postcode.getDistance());
                }

                printWriter.println();

                for(User user: restaurant.getUsers()){

                    printWriter.println("USER:" + user.getName() + ":" + user.getPassword() + ":" + user.getAddress() + ":" + user.getPostcode());
                }

                printWriter.println();

                for(Order order: restaurant.getOrders()){

                    printWriter.print("ORDER:" + order.getUser().getName() + ":");
                    int counter = 0;

                    for(Dish dish: order.getBasket().keySet()){

                        printWriter.print(order.getBasket().get(dish) + " * " + dish.getName());
                        counter = counter + 1;

                        if(counter < order.getBasket().size()){
                            printWriter.print(",");
                        }
                        else{
                            printWriter.println();
                        }
                    }
                }

                printWriter.println();

                for(Map.Entry<Ingredient,Number> map: restaurant.getStock().getIngredientsInStock().entrySet()){

                    printWriter.println("STOCK:" + map.getKey().getName() + ":" +  map.getValue().intValue());
                }


                for(Map.Entry<Dish,Number> map: restaurant.getStock().getDishesInStock().entrySet()){

                    printWriter.println("STOCK:" + map.getKey().getName() + ":" +  map.getValue().intValue());
                }

                printWriter.println();

                for(Staff staff: restaurant.getStaffList()){

                    printWriter.println("STAFF:" + staff.getName());
                }

                printWriter.println();

                for(Drone drone: restaurant.getDrones()){

                    printWriter.println("DRONE:" + drone.getSpeed());
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



}
