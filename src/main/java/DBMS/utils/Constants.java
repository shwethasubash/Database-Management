package DBMS.utils;

public class Constants {
	
	//Regular expression for all basic SQL queries
	public static String QUERY="create table table_name(name INT,age INT,primary key(name));";
	public static String SELECT_REGEX = "^SELECT\\s([\\w\\*\\)\\(\\,\\s]+)\\sFROM\\s([\\w]+)\\sWHERE\\s(\\w+)\\s*\\=\\s*([\"\\w]+);$";
	public static String CREATE_DB_REGEX = "^create\\s+database\\s+(\\w+);$";
	public static String UPDATE_REGEX = "^UPDATE\\s([\\w]+)\\sSET\\s(\\w+)\\s*\\=\\s*([\\\"\\w]+)(\\s*WHERE\\s(\\w+)\\s*\\=\\s*([\"\\w]+))*;$";
	public static String DELETE_REGEX = "^DELETE\\sFROM\\s(\\w+)\\sWHERE\\s(\\w+)\\s*\\=\\s*([\"\\w]+);$";
	public static String INSERT_REGEX = "^INSERT\\sINTO\\s(\\w+)\\s*\\(*([\\s\\w\\,]*)\\)*\\sVALUES\\s*\\(*([\\w\\'\"\\,\\s]+)\\)*;$";
	public static String CREATE_TABLE_REGEX = "^CREATE\\sTABLE\\s(\\w+)\\s*\\((.*)(PRIMARY\\s+KEY\\s*\\(\\s*(\\w+)\\s*\\))\\s*\\);$";
	public static String USE_DATABASE = "^USE\\s+(\\w+);$";
	public static String GRANT_REGEX = "^GRANT\\s+([\\w\\_\\-]+)\\s+TO\\s+([\\w+\"\\']+)\\s*;$";
}