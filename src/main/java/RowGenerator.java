import java.sql.Connection;
import java.sql.SQLException;

public class RowGenerator {
    public static void generate(String hallName, Integer amount) throws SQLException, ClassNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();
        String sql;


        if (hallName.isBlank()) {
            sql = "SELECT id FROM hall ORDER BY RANDOM() LIMIT 1";
        } else {
            sql = String.format("SELECT id FROM hall WHERE hallname = '%s';", hallName);
        }
        int hallId = UtilityClass.getInt(db, sql);

        sql = String.format("SELECT COUNT(*) FROM row WHERE hallId = %d;", hallId);
        int currRow = UtilityClass.getInt(db, sql);
        int amountSeats = (int) (Math.random() * 500 + 1);


        sb.append("INSERT INTO row (hallId, rowNumber, amountSeats) VALUES");
        for (int i = 1; i <= amount; i++) {
            sb.append(String.format(" ('%d', '%d', '%d'),", hallId, currRow + i, amountSeats));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }
}
