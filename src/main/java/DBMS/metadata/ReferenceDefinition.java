package DBMS.metadata;

import java.util.Map;

public class ReferenceDefinition {
    private String referencedTableName;
    private Map<String, String> columnRelation;

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public Map<String, String> getColumnRelation() {
        return columnRelation;
    }

    public void setColumnRelation(Map<String, String> columnRelation) {
        this.columnRelation = columnRelation;
    }
}
