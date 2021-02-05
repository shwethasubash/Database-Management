package DBMS;

import java.util.ArrayList;

public class DBMS {
    private String username;
    private String activeDatabase;
    private static DBMS instance;
    private String transactionId;
    private ArrayList<String> tables;

    DBMS(){
        tables = new ArrayList<>();
    }

    public static DBMS getInstance() {
        if (instance == null) {
            instance = new DBMS();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActiveDatabase() {
        return activeDatabase;
    }

    public void setActiveDatabase(String activeDatabase) {
        this.activeDatabase = activeDatabase;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ArrayList<String> getTables() {
        return tables;
    }

    public void setTables(ArrayList<String> tables) {
        this.tables = tables;
    }
}
