import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CashboxGenerator {
    public static void generate(int amount) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO cashbox (isAutomated) VALUES");

        for (int i = 0; i < amount; i++) {
            sb.append(String.format(" ('%s'),", (Math.random() < 0.5)));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        Statement st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }
}
