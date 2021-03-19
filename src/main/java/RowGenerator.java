import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;

public class RowGenerator {
    public static void generate(String hallName, Integer amount) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");
        Statement st;
        String sql;
        ResultSet rs;

        if (hallName.isBlank()) {
            sql = "SELECT id FROM hall ORDER BY RANDOM() LIMIT 1";
        } else {
            sql = String.format("SELECT id FROM hall WHERE hallname = '%s';", hallName);
        }

        st = db.createStatement();
        rs = st.executeQuery(sql);
        rs.next();
        int hallId = rs.getInt(1);

        sql = String.format("SELECT COUNT(*) FROM row " +
                "WHERE hallId = %d;", hallId);
        st = db.createStatement();
        rs = st.executeQuery(sql);
        rs.next();
        int currRow = rs.getInt(1);
        int amountSeats = (int) (Math.random() * 50 + 1);

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO row (hallId, rowNumber, amountSeats) VALUES");

        for (int i = 1; i <= amount; i++) {
            sb.append(String.format(" ('%d', '%d', '%d'),", hallId, currRow + i, amountSeats));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON CONFLICT DO NOTHING;");
        st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }
}
