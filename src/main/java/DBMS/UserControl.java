package DBMS;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class UserControl {
    String username;
    String password;
    ArrayList<String> database;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getDatabase() {
        return database;
    }

    public void setDatabase(ArrayList<String> database) {
        this.database = database;
    }

    public static boolean doesUserHaveAccessToDB(String username, String db) throws FileNotFoundException {
        String path = "database/Users.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        UserControl[] usersControl = gson.fromJson(bufferedReader, UserControl[].class);
        for(UserControl userControl : usersControl){
            if(userControl.getUsername().equals(username)){
                if(userControl.getDatabase().contains(db)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean userExists(String username) throws FileNotFoundException {
        String path = "database/Users.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        UserControl[] usersControl = gson.fromJson(bufferedReader, UserControl[].class);
        return Arrays.stream(usersControl)
                .anyMatch(x -> x.getUsername().equals(username));
    }

    public static boolean grantAccessToDatabase(String username, String database) throws IOException {
        String path = "database/Users.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        UserControl[] usersControl = gson.fromJson(bufferedReader, UserControl[].class);
        for(UserControl userControl : usersControl){
            if(userControl.getUsername().equals(username)){
                if(userControl.getDatabase().contains(database)){
                    System.out.println("DBMS.User "+ username + " is already having access to the database");
                    return false;
                }else{

                    userControl.getDatabase().add(database);
                    updateUserAccess(path, usersControl);

                    System.out.println("Granted access to database " + database + " to user " + username);
                    return true;
                }
            }
        }
        return false;
    }

    private static void updateUserAccess(String path, UserControl[] usersControl) {
        Gson gson2 = new GsonBuilder()
                .setPrettyPrinting().create();

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            gson2.toJson(usersControl, fileWriter);

        } catch (Exception e) {
            System.out.println("Something went wrong while updating JSON ");
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Something went wrong while updating JSON ");
            }

        }
    }
}
