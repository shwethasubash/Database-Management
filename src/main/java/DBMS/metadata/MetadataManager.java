package DBMS.metadata;

import DBMS.UserControl;
import DBMS.attributetype.AttributeType;
import DBMS.attributetype.AttributeTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetadataManager {

    private final Gson gson;

    public MetadataManager() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(AttributeType.class, new AttributeTypeDeserializer())
                .create();
        createDatabaseDirectory();
    }

    private void createDatabaseDirectory() {
        try {
            Files.createDirectories(Paths.get("database"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Metadata loadMetadata(String filePath) {

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String metadataJson = contentBuilder.toString();
        return gson.fromJson(metadataJson, Metadata.class);
    }

    public Map<String, List<Metadata>> createDataDictionary() {
        Map<String, List<Metadata>> dataDictionary = new HashMap<>();

        try (Stream<Path> paths = Files.walk(Paths.get("database"))) {
            List<Path> pathList = paths.collect(Collectors.toList());
            pathList.remove(0);

            String dbName = null;
            for (Path path: pathList) {
                if (Files.isDirectory(path)) {
                    dbName = path.getFileName().toString();
                    dataDictionary.put(dbName, new ArrayList<>());
                }

                else if (Files.isRegularFile(path) && path.getFileName().toString().endsWith("_metadata.json")) {
                    Metadata metadata = loadMetadata(path.toString());
                    dataDictionary.get(dbName).add(metadata);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataDictionary;
    }

    public ArrayList<String> getTablesFromDatabase(String databaseName){
        ArrayList<String> teamList = new ArrayList<>();
        Map<String, List<Metadata>> dataDictionary = createDataDictionary();
        List<Metadata> metadataList = dataDictionary.getOrDefault(databaseName, null);
        if (metadataList.size() > 0) {
            for (Metadata m: metadataList) {
                teamList.add(m.getTableName());
            }
        }
        return teamList;
    }

    public List<Metadata> getMetadataListByDatabase(String dbName) {
        Map<String, List<Metadata>> dataDictionary = createDataDictionary();
        return dataDictionary.getOrDefault(dbName, null);
    }

    public Metadata getMetadataByDatabaseAndTable(String dbName, String tableName) {
        Metadata metadata = null;
        Map<String, List<Metadata>> dataDictionary = createDataDictionary();
        List<Metadata> metadataList = dataDictionary.getOrDefault(dbName, null);

        if (metadataList.size() > 0) {
            for (Metadata m: metadataList) {
                if (m.getTableName().equalsIgnoreCase(tableName)) {
                    metadata = m;
                    break;
                }
            }
        }
        return metadata;
    }

    public void createDatabase(String databaseName,String userName) throws IOException {
        try {
            Files.createDirectories(Paths.get("database/"+ databaseName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTableMetadata(String databaseName, String tableName,Metadata metadata){

        Gson gson2 = new GsonBuilder()
                .setPrettyPrinting().create();

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("database/"+databaseName+"/"+tableName+"_metadata.json");
            gson2.toJson(metadata, fileWriter);

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

    public boolean databaseExists(String databaseName){
        Map<String, List<Metadata>> dataDictionary = createDataDictionary();
        List<Metadata> metadataList = dataDictionary.getOrDefault(databaseName, null);
        if(metadataList == null){
            return false;
        }else{
            return true;
        }
    }

    public boolean tableExists(String databaseName,String tableName){
        boolean exists = false;
        Map<String, List<Metadata>> dataDictionary = createDataDictionary();
        List<Metadata> metadataList = dataDictionary.getOrDefault(databaseName, null);
        if (metadataList.size() > 0) {
            for (Metadata m: metadataList) {
                if (m.getTableName().equalsIgnoreCase(tableName)) {
                    exists = true;
                }
            }
        }else {
            exists =false;
        }
        return exists;
    }

    public boolean columnExists(String databaseName, String tableName,String column){
        boolean exists = false;
        Map<String, List<Metadata>> dataDictionary = createDataDictionary();
        List<Metadata> metadataList = dataDictionary.getOrDefault(databaseName, null);
        if (metadataList.size() > 0) {
            for (Metadata m: metadataList) {
                if (m.getTableName().equalsIgnoreCase(tableName)) {
                    if(m.getColumns().containsKey(column)){
                        exists = true;
                    }
                }
            }
        }
        return exists;
    }
}
