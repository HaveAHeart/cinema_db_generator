import java.sql.Connection;
import java.sql.SQLException;

public class CashboxGenerator {
    public static void generate(int amount) throws SQLException, ClassNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO cashbox (isAutomated) VALUES");
        for (int i = 0; i < amount; i++) {
            sb.append(String.format(" ('%s'),", (Math.random() < 0.5)));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }
}
