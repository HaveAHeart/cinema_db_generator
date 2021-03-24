import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;


public class PersonGenerator {
    public static void generate(int amount, String period) throws SQLException, FileNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final String age = String.format("cast((now()) - ('%s'::interval * random()) - '20 years'::interval as date)", period);
        final StringBuilder sb = new StringBuilder();
        final String separator = " ";

        sb.append("INSERT INTO person (name, surname, dateOfBirth) VALUES");

        for (int i = 0; i < amount; i++) {
            String[] nameSurname = UtilityClass.choose(new File("src/main/resources/full_name.txt"), true).split(separator);
            sb.append(String.format("\n ('%s', '%s', %s),", nameSurname[0], nameSurname[1], age));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }
}
