package DBMS.erd;

import DBMS.metadata.Metadata;
import DBMS.metadata.MetadataManager;
import DBMS.metadata.ReferenceDefinition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ERDiagramMaker {

    public ERDiagramMaker() {
        createERDiagramDirectory();
    }

    private void createERDiagramDirectory() {
        try {
            Files.createDirectories(Paths.get("erDiagram"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createERDiagram(String databaseName) {
        MetadataManager metadataManager = new MetadataManager();
        if (metadataManager.databaseExists(databaseName)) {
            Map<String, List<Metadata>> dataDictionary = metadataManager.createDataDictionary();
            List<Metadata> metadataList = dataDictionary.get(databaseName);

            List<String> erDiagramLines = new ArrayList<>();
            erDiagramLines.add("TABLES");
            for (Metadata m: metadataList) {
                StringBuilder line = new StringBuilder();
                line.append(m.getTableName()).append(" (");
                for (String column: m.getColumnNames()) {
                    if (m.isColumnAPrimaryKey(column)) {
                        line.append("*");
                    }
                    line.append(column).append(", ");
                }
                line.deleteCharAt(line.length() - 1);
                line.setCharAt(line.length() - 1, ')');

                if (m.doesForeignKeyRelationshipExist()) {
                    line.append(" references");
                    for (ReferenceDefinition rd : m.getForeignKeys()) {
                        line.append(" ").append(rd.getReferencedTableName()).append(",");
                    }
                    line.setCharAt(line.length() - 1, ' ');
                }
                erDiagramLines.add(line.toString());
            }

            erDiagramLines.add("");
            erDiagramLines.add("FOREIGN KEY RELATIONSHIPS");
            for (Metadata m: metadataList) {
                if (m.doesForeignKeyRelationshipExist()) {
                    for (ReferenceDefinition rd: m.getForeignKeys()) {
                        for (Map.Entry<String, String> entry: rd.getColumnRelation().entrySet()) {
                            StringBuilder line = new StringBuilder();
                            line.append(m.getTableName())
                                .append(" (")
                                .append(entry.getKey())
                                .append(") -> ")
                                .append(rd.getReferencedTableName())
                                .append(" (")
                                .append(entry.getValue())
                                .append(")");
                            erDiagramLines.add(line.toString());
                        }
                    }
                }
            }

            writeToFile(databaseName, erDiagramLines);
        }
        else {
            System.out.println("The provided database name does not exist");
        }
    }

    private void writeToFile(String filename, List<String> fileContent) {
        try {
            Files.write(Paths.get("erDiagram/" + filename + "_erd.txt"), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
