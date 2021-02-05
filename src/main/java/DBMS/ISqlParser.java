package DBMS;

import java.io.IOException;

public interface ISqlParser {
	void validateQuery(String query) throws IOException;
}
