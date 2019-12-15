import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        String fileName = "src\\main\\resources\\data-1572M.xml";
        try {
            parseFileSax(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBConnection.printVoterCounts();
        DBConnection.customSelect("Красненко Аникей");
        DBConnection.connectionClose();
    }

    private static void parseFileSax(String fileName) throws Exception {
        DBConnection.getConnection();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler();
        parser.parse(new File(fileName), handler);
        DBConnection.executeBatch();
    }
}