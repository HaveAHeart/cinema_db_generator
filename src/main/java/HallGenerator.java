import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

public class HallGenerator {
    public static void generate(int amount) throws SQLException, FileNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO hall (hallname) VALUES");
        for (int i = 0; i < amount; i++) {
            String name = UtilityClass.choose(new File("src/main/resources/hall_name.txt"), true);
            sb.append(String.format("\n ('%s'),", name));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }

    public static void generateAdvanced(int amount) throws SQLException, FileNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final String sql = "SELECT hallname FROM hall ORDER BY id DESC LIMIT 1";

        for (int i = 0; i < amount; i++) {
            HallGenerator.generate(1);
            String hallname = UtilityClass.getString(db, sql);
            RowGenerator.generate(hallname, (int) (Math.random() * 15 + 5));
        }

        db.close();
    }
}
