package DBMS;

import DBMS.attributetype.AttributeType;
import DBMS.metadata.Metadata;
import DBMS.metadata.MetadataManager;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataDump {

    CSVManager newCSVManager;

    DataDump(){
        newCSVManager = new CSVManager();
    }

    private String insertValueDump(String databaseName, String tableName) throws IOException {
        String insertquerystatements = "";
        ArrayList<ArrayList<String>> data = newCSVManager.getAllDataFromTable("database/"+databaseName+"/"+tableName+".csv");
        for(ArrayList<String> row : data){
            insertquerystatements = insertquerystatements + "insert into " + tableName + " values (";
            insertquerystatements = insertquerystatements + join(row) + "); \n";
        }
        return insertquerystatements;
    }

    private String createTableDump(String databaseName,String tableName) {
        MetadataManager metadataManager = new MetadataManager();
        Metadata metadata = metadataManager.getMetadataByDatabaseAndTable(databaseName,tableName);
        String createStatement = "";
        createStatement = createStatement + "create table " + metadata.getTableName() + "(";
        for (Map.Entry<String, AttributeType> entry2 : metadata.getColumns().entrySet()) {
            createStatement = createStatement + entry2.getKey() + " " + entry2.getValue().toString() + ",";
        }

        if(metadata.getPrimaryKeys().size()>0){
            createStatement = createStatement + "PRIMARY KEY ";
            createStatement = createStatement + String.join(",",metadata.getPrimaryKeys());
        }else{
            createStatement = createStatement.substring(0, createStatement.length()-1);
        }

        createStatement = createStatement + "); \n";

        return createStatement;
    }

    public void createDatabaseDump(String database) throws IOException {
        String dump = "";
        String databaseName = database;//DBMS.getInstance().getActiveDatabase();
        MetadataManager metadataManager = new MetadataManager();
        ArrayList<String> tableList = metadataManager.getTablesFromDatabase(databaseName);
        for(String table:tableList){
            dump = dump + createTableDump(databaseName,table);
        }
        for(String table:tableList){
            dump = dump + insertValueDump(databaseName,table);
        }


        FileUtils.writeStringToFile(new File("database/"+ databaseName + ".dump"), dump, StandardCharsets.UTF_8);

    }

    private String join(List<String> namesList) {
        return String.join(",", namesList
                .stream()
                .map(name -> ("'" + name + "'"))
                .collect(Collectors.toList()));
    }
}
