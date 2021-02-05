package DBMS.metadata;

import DBMS.attributetype.AttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metadata {
    private String tableName;
    private Map<String, AttributeType> columns;
    private List<String> primaryKeys;
    private List<ReferenceDefinition> foreignKeys;

    public Metadata() {
        columns = new HashMap<>();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, AttributeType> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, AttributeType> columns) {
        this.columns = columns;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<ReferenceDefinition> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<ReferenceDefinition> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public boolean isColumnAPrimaryKey(String columnName) {
        if (primaryKeys == null || primaryKeys.size() == 0) {
            return false;
        }

        boolean isPrimaryKey =  false;

        for (String key: primaryKeys) {
            if (key.equalsIgnoreCase(columnName)) {
                isPrimaryKey = true;
                break;
            }
        }

        return isPrimaryKey;
    }

    public List<String> getColumnNames() {
        return new ArrayList<>(columns.keySet());
    }

    public boolean doesForeignKeyRelationshipExist() {
        return !(foreignKeys == null || foreignKeys.size() == 0);
    }
}
