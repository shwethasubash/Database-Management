package DBMS;

import DBMS.attributetype.AttributeType;
import DBMS.erd.ERDiagramMaker;
import DBMS.metadata.Metadata;
import DBMS.metadata.MetadataManager;
import DBMS.utils.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser implements ISqlParser {
	private final static Logger logger = Logger.getLogger(SqlParser.class.getName());
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SqlParser.class);
	MetadataManager metadataManager;

	SqlParser(){
		metadataManager = new MetadataManager();
	}

    
	public void validateQuery(String query) throws IOException {
		logger.info("Invoked validateQuery method");
		log.info("Query " + query + "from User " + DBMS.getInstance().getUsername());
		if(query.toLowerCase().contains("select")){
			selectQuery(query);
		}else if(query.toLowerCase().contains("create database")){
			createDBQuery(query);
		}else if(query.toLowerCase().contains("update")){
			updateQuery(query);
		}else if(query.toLowerCase().contains("delete")){
			deleteQuery(query);
		}else if(query.toLowerCase().contains("create table")){
			createQuery(query);
		}else if(query.toLowerCase().contains("insert")){
			insertQuery(query);
		}else if(query.toLowerCase().contains("use")){
			useDatabaseQuery(query);
		}else if(query.toLowerCase().contains("grant")){
			grantQuery(query);
		}else if(query.toLowerCase().contains("commit;")){
			commit();
		}else if(query.toLowerCase().contains("rollback;")){
			rollback();
		}else if(query.toLowerCase().contains("generate")){
			erd(query);
		}else if(query.toLowerCase().contains("dump")){
			generatedump(query);
		}else{
			System.out.println("Please enter valid command");
		}
	}

	private void generatedump(String query) throws IOException {
		String[] str = query.split(" ");
		String databaseName = str[1].replace(";","");

		DataDump dataDump = new DataDump();
		dataDump.createDatabaseDump(databaseName);
	}

	private void erd(String query) {
		String[] str = query.split(" ");
		String databaseName = str[2].replace(";","");

        ERDiagramMaker erDiagramMaker = new ERDiagramMaker();
        erDiagramMaker.createERDiagram(databaseName);
	}

	private void commit() {
		SemanticController semanticController = new SemanticController();
		semanticController.commit();
	}

	private void rollback() {
		SemanticController semanticController = new SemanticController();
		semanticController.rollback();
	}

	void selectQuery(String query) throws IOException {
		Map<String,String> selectFields = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(Constants.SELECT_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if (matchFound) {
			selectFields.put("selectColumn", matcher.group(1));
			selectFields.put("table", matcher.group(2));
			selectFields.put("whereColumn", matcher.group(3));
			selectFields.put("whereValue", matcher.group(4).replace("\"", "").replace("\'", ""));
			SemanticController semanticController = new SemanticController();
			semanticController.selectTable(selectFields);
		}else{
			System.out.println("Invalid statement");
		}

		//call to semantic parser
		//if valid, func call to execute the query, pass the map as input 
		
	}
	
	void createDBQuery(String query) throws IOException {
		Map<String,String> createDBFields = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(Constants.CREATE_DB_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if (matchFound) {
			createDBFields.put("database", matcher.group(1));
			SemanticController semanticController = new SemanticController();
			semanticController.createDatabase(createDBFields.get("database"));
		}else{
			System.out.println("Invalid statement");
		}

		//call to semantic parser
		//if valid, func call to execute the query, pass the map as input 
	}
	
	void updateQuery(String query) throws IOException {
		Map<String,String> updateFields = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(Constants.UPDATE_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if (matchFound) {
			updateFields.put("table", matcher.group(1));
			updateFields.put("column", matcher.group(2));
			updateFields.put("value", matcher.group(3).replace("\"", "").replace("\'", ""));
			updateFields.put("whereColumn", matcher.group(5));
			updateFields.put("whereValue", matcher.group(6).replace("\"", "").replace("\'", ""));
			SemanticController semanticController = new SemanticController();
			semanticController.updateTable(updateFields);
		}else{
			System.out.println("Invalid statement");
		}


		//call to semantic parser
		//if valid, func call to execute the query, pass the map as input 
	}
	
	void deleteQuery(String query) throws IOException {
		Map<String,String> deleteFields = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(Constants.DELETE_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if (matchFound) {
			deleteFields.put("table", matcher.group(1));
			deleteFields.put("whereColumn", matcher.group(2));
			deleteFields.put("whereValue", matcher.group(3).replace("\"", "").replace("\'", ""));
			SemanticController semanticController = new SemanticController();
			semanticController.deleteTable(deleteFields);
		}else{
			System.out.println("Invalid statement");
		}


		
		//call to semantic parser
		//if valid, func call to execute the query, pass the map as input 
		
	}
	
	void insertQuery(String query) throws IOException {
		Map<String,String[]> insertFields = new HashMap<String, String[]>();
		Pattern pattern = Pattern.compile(Constants.INSERT_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if(matchFound) {
			String[] table = {matcher.group(1)};
			insertFields.put("table", table);
			insertFields.put("columns", matcher.group(2).split(","));
			String values[]=matcher.group(3).split(",");
			for(int i=0;i<values.length;i++)
			{
				values[i]=values[i].replace("\"", "").replace("\'", "");
			}
			insertFields.put("values", values);
			SemanticController semanticController = new SemanticController();
			semanticController.insert(insertFields);
		}else{
			System.out.println("Invalid statement");
		}
		//call to semantic parser
		//if valid, func call to execute the query, pass the map as input 

	}

	void createQuery(String query) throws IOException {
		Metadata metadata = new Metadata();
		Map<String,String[]> createTableFields = new HashMap<String, String[]>();
		Map<String,AttributeType> columns = new HashMap<String, AttributeType>();
		Pattern pattern = Pattern.compile(Constants.CREATE_TABLE_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		
		if(matchFound) {
			String fields = matcher.group(2);
			fields=fields.substring(0, fields.length()-1);
		
			String[] column = fields.split(",");
			String[] seperateFields;
			AttributeType attribute;
			for(String i : column) {
				seperateFields =i.split(" ");
				attribute = AttributeType.valueOf(seperateFields[1].toUpperCase());
				columns.put(seperateFields[0], attribute);
			}
			
			String[] primaryKeys = matcher.group(4).split(",");
			List<String> primary = Arrays.asList(primaryKeys);
			metadata.setTableName(matcher.group(1));
			metadata.setColumns(columns);
			metadata.setPrimaryKeys(primary);
			SemanticController semanticController = new SemanticController();
			semanticController.createTable(metadata);
		}else{
			System.out.println("Invalid statement");
		}


	}
	
	void useDatabaseQuery(String query) throws FileNotFoundException {
		Pattern pattern = Pattern.compile(Constants.USE_DATABASE, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if(matchFound) {
			String database = matcher.group(1);
			if(!metadataManager.databaseExists(database)){
				System.out.println("database " + database +" is not present. Please retry");
				return;
			}
			UserControl userControl = new UserControl();
			if(!userControl.doesUserHaveAccessToDB(DBMS.getInstance().getUsername(), database)){
				System.out.println("User doesn't have access to DB. please retry.");
				return;
			}
			DBMS.getInstance().setActiveDatabase(database);
		}else{
			System.out.println("Invalid statement");
		}
	}
	
	void grantQuery(String query) throws IOException {
		Map<String,String> grantFields = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(Constants.GRANT_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(query);
		boolean matchFound = matcher.find();
		if(matchFound) {
			grantFields.put("database", matcher.group(1));
			grantFields.put("user", matcher.group(2).replace("\"", "").replace("\'", ""));
			SemanticController semanticController = new SemanticController();
			semanticController.grantAccess(grantFields.get("database"),grantFields.get("user"));
		}else{
			System.out.println("Invalid statement");
		}
	}
	
}
