package DBMS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLToCSV {

    CSVManager newCSVManager;

    SQLToCSV(){
        newCSVManager = new CSVManager();
    }

    public void insertQuery(String databaseName,String tableName,HashMap<String,String> columnsAndValues) throws IOException {
        newCSVManager.insertCSV("database/"+databaseName+"/"+tableName+".csv",columnsAndValues);
    }

    public void createTable(String databaseName,String tableName,ArrayList<String> columns) throws IOException {
        newCSVManager.createTableWithHeaders("database/"+databaseName+"/"+tableName+".csv",columns);
    }

    public List getHeaders(String databaseName, String tableName) throws IOException {
        return newCSVManager.getHeaders("database/"+databaseName+"/"+tableName+".csv");
    }

    public void updateTable(String databaseName, String tableName,HashMap<String,String> set,HashMap<String,String> conditions) throws IOException {
        newCSVManager.updateCSV("database/"+databaseName+"/"+tableName+".csv",conditions,set);
    }

    public void deleteRows(String databaseName, String tableName,HashMap<String,String> conditions) throws IOException {
        newCSVManager.deleteCSV("database/"+databaseName+"/"+tableName+".csv",conditions);
    }
    public void selectTable(String database, String table, ArrayList<String> columns, HashMap<String,String> conditions) throws IOException {
        newCSVManager.readCSV("database/"+database+"/"+table+".csv",conditions,columns);
    }
}
