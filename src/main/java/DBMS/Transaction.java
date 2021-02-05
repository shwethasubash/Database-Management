package DBMS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Transaction {
    String id;
    String user;
    CSVManager newCSVManager;
    final String transactionFilePath = "database/transactions.csv";

    Transaction(){
        newCSVManager = new CSVManager();
    }
    public void generateTransactionId(String user){
        Random rand = new Random();
        this.id  = user + "_" + rand.nextInt(100000000);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  void copyTable(String databaseName,String tableName) throws IOException {

        String copyTableName = getCopyTableName(databaseName,tableName);
        File orig = new File("database/"+databaseName+"/"+tableName+".csv");
        File copyFile = new File("database/"+databaseName+"/"+copyTableName+".csv");
        Files.copy(orig.toPath(), copyFile.toPath());
    }

    public String getCopyTableName(String databaseName,String tableName) throws IOException {
        String copyTableName = DBMS.getInstance().getTransactionId()+"_"+tableName;
        return copyTableName;
    }



    public void insertTransactionLog(String tableName,String typeOfQuery, LocalDateTime startOfQuery, LocalDateTime endOfQuery) throws IOException {
        HashMap<String,String> columnsAndValues = new HashMap<>();
        columnsAndValues.put("username",DBMS.getInstance().getUsername());
        columnsAndValues.put("transactionid",DBMS.getInstance().getTransactionId());
        columnsAndValues.put("table",tableName);
        columnsAndValues.put("transaction_state",TRANSACTION_STATE.PROCESS.name());
        columnsAndValues.put("startTimeStamp", String.valueOf(startOfQuery));
        columnsAndValues.put("endTimeStamp", String.valueOf(endOfQuery));
        columnsAndValues.put("executionTime",String.valueOf(Duration.between(startOfQuery,endOfQuery).getSeconds() + "." + Duration.between(startOfQuery,endOfQuery).getNano()));
        columnsAndValues.put("queryType",typeOfQuery);
        newCSVManager.insertCSV(transactionFilePath,columnsAndValues);
    }

    private void updateLog(String transactionState) throws IOException {
        HashMap<String,String> conditions = new HashMap<>();
        conditions.put("transactionid",DBMS.getInstance().getTransactionId());
        HashMap<String,String> set = new HashMap<>();
        set.put("transaction_state",transactionState);
        newCSVManager.updateCSV(transactionFilePath,conditions,set);
    }

    //Citation : https://www.codota.com/code/java/methods/java.io.File/renameTo
    private void replace(File destfile, File origfile) throws IOException {
        destfile.delete();
        if(!origfile.renameTo(destfile)){
            throw new IOException("Unable to rename file" + origfile.getName());
        }
    }


    public void commit(String database, String tableName){
        String copyTableName = DBMS.getInstance().getTransactionId()+"_"+tableName;
        try{
            File mainFile = new File("database/"+database+"/"+tableName+".csv");
            File copyFile = new File("database/"+database+"/"+copyTableName+".csv");

            replace(mainFile,copyFile);
            updateLog(TRANSACTION_STATE.COMMIT.name());


            DBMS dbms = DBMS.getInstance();
            dbms.getTables().removeIf(n -> Objects.equals(n, tableName));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rollback(String database, String tableName){
        String copyTableName = DBMS.getInstance().getTransactionId()+"_"+tableName;
        try{

            File copyFile = new File("database/"+database+"/"+copyTableName+".csv");
            copyFile.delete();
            updateLog(TRANSACTION_STATE.ROLLBACK.name());

            DBMS dbms = DBMS.getInstance();
            dbms.getTables().removeIf(n -> Objects.equals(n, tableName));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkIfAnyTransactionUsingSametable(String tableName) throws IOException {
        String filepath = transactionFilePath;
        HashMap<String,String> conditions = new HashMap<>();
        conditions.put("table",tableName);
        conditions.put("transaction_state",TRANSACTION_STATE.PROCESS.name());
        ArrayList<String> columns = new ArrayList<>();
        columns.add("username");
        columns.add("transactionid");
        columns.add("table");
        columns.add("transaction_state");
        columns.add("startTimeStamp");
        columns.add("endTimeStamp");
        columns.add("executionTime");
        columns.add("queryType");
        CSVManager newCSVManager = new CSVManager();
        if(newCSVManager.doesDataExistOnCondition(filepath,conditions,columns)){
            return true;
        }else{
            return false;
        }


    }

}
