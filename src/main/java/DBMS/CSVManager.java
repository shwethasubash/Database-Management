package DBMS;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

//using tablesaw to do csv operations based on SQL query
public class CSVManager {

    public void readCSV(String filepath, HashMap<String,String> conditions, ArrayList<String> columns) throws IOException {
        Table table = Table.read().csv(filepath);
        //conditions.forEach((k, v) -> table.where(table.stringColumn(k).isEqualTo(v)));

        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            table = table.where(table.stringColumn(entry.getKey()).isEqualTo(entry.getValue()));
        }
        Table reduced = table.select(columns.toArray(new String[columns.size()]));
        for(String column : columns){
            System.out.print(column + "  ");
        }
        System.out.println("");
        for (Row row : reduced) {
            for(String column: columns){
                System.out.print(row.getString(column) + "  ");
            }
            System.out.println("");
        }
    }

    public boolean doesDataExistOnCondition(String filepath, HashMap<String,String> conditions, ArrayList<String> columns) throws IOException {
        Table table = Table.read().csv(filepath);
        //conditions.forEach((k, v) -> table.where(table.stringColumn(k).isEqualTo(v)));
        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            table = table.where(table.stringColumn(entry.getKey()).isEqualTo(entry.getValue()));
        }
        Table reduced = table.select(columns.toArray(new String[columns.size()]));
        if(reduced.rowCount() > 0){
            return true;
        }else return false;
    }

    public ArrayList<ArrayList<String>> getAllDataFromTable(String filepath) throws IOException {
        Table table = Table.read().csv(filepath);
        ArrayList<ArrayList<String>> allData = new ArrayList<>();
        List<String> headers = getHeaders(filepath);
        for (Row row : table) {
            ArrayList<String> rowData = new ArrayList<>();
            for(String column: headers){
                rowData.add(row.getString(column));
            }
            allData.add(rowData);
        }
        return allData;
    }

    public void updateCSV(String filepath, HashMap<String,String> conditions, HashMap<String,String> set) throws IOException{
            Table table = Table.read().csv(filepath);
            boolean matchesNotFound = true;
            for (Row row : table) {
                for (Map.Entry<String, String> entry : conditions.entrySet()) {
                    if(row.getString(entry.getKey()).equals(entry.getValue())){
                        for (Map.Entry<String, String> entry2 : set.entrySet()) {
                            row.setString(entry2.getKey(), entry2.getValue());
                        }
                        matchesNotFound = false;
                    }
                }
            }
            if(matchesNotFound){
                System.out.println("0 records matched.");
                SemanticController semanticController = new SemanticController();
                semanticController.rollback();
            }
        table.write().csv(filepath);
    }

    public void insertCSV(String filepath, HashMap<String,String> columnsAndValues) throws IOException{
        ArrayList<String> headers = (ArrayList<String>) getHeaders(filepath);
        ArrayList<String> allValues = new ArrayList<>(headers.size());

        for (int i = 0; i < headers.size(); i++) {
            if(columnsAndValues.containsKey(headers.get(i))) {
                allValues.add(columnsAndValues.get(headers.get(i)));
            }else{
                allValues.add("");
            }
        }

        BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(filepath),
                StandardOpenOption.APPEND);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        csvPrinter.printRecord(allValues);
        csvPrinter.close();
        writer.close();
    }

    public List getHeaders(String filename) throws IOException {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT;
        FileReader fileReader = new FileReader(filename);
        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat);
        CSVRecord csvRecord = csvFileParser.getRecords().get(0);
        ArrayList<String> headers = new ArrayList<>();
        for(String field : csvRecord){
            headers.add(field);
        }
        fileReader.close();
        csvFileParser.close();
        return headers;
    }

    public static void alterAddColumn(String filepath, ArrayList<String> columnNames) throws IOException{
        Table table = Table.read().csv(filepath);
        String [] s = new String[table.rowCount()];
        java.util.Arrays.fill(s,"");
        for(String column: columnNames){
                table.addColumns(StringColumn.create(column, s));
        }
        table.write().csv(filepath);
    }

    public static void alterRemoveColumn(String filepath, ArrayList<String> columnNames) throws IOException{
        Table table = Table.read().csv(filepath);
        for(String column: columnNames){
            table.removeColumns(column);
        }
        table.write().csv(filepath);
    }


    public static void createTableWithHeaders(String filepath,ArrayList<String> headers) throws IOException {
        Table temp = Table.create("new Table");
        for(String header:headers){
            temp.addColumns(StringColumn.create(header, new String[] {""}));
        }
        temp = temp.dropRange(0,1);
        temp.write().csv(filepath);
    }

    public void deleteCSV(String filepath, HashMap<String,String> conditions) throws IOException {
        Table table = Table.read().csv(filepath);
        //conditions.forEach((k, v) -> table.where(table.stringColumn(k).isEqualTo(v)));
        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            table = table.dropWhere(table.stringColumn(entry.getKey()).isEqualTo(entry.getValue()));
        }
        table.write().csv(filepath);
    }
}
