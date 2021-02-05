package DBMS;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {

        try {
            SqlParser sqlParser = new SqlParser();
            Transaction transaction = new Transaction();
            String username = ReadInput.readInput("Please enter username");
            String password = ReadInput.readInput("Please enter password");

//        DataDump dataDump = new DataDump();
//        dataDump.createDatabaseDump("TestDB");

            DBMS dbms = DBMS.getInstance();
            User user = new User(username, password);
            if (!UserControl.userExists(user.getUsername())) {
                username = ReadInput.readInput("Please enter username");
                password = ReadInput.readInput("Please enter password");
                user.setUsername(username);
            }
            dbms.setUsername(username);
            transaction.generateTransactionId(username);
            dbms.setTransactionId(transaction.getId());
            String query = ReadInput.readInput("Enter Query");
            do {
                sqlParser.validateQuery(query);
                query = ReadInput.readInput("Enter Query");
            } while (!query.equals("quit"));
        }catch (Exception e){
            System.out.println("Exception occurred "+ e);
        }finally {
            SemanticController semanticController = new SemanticController();
            semanticController.rollback();
        }
    }
}
