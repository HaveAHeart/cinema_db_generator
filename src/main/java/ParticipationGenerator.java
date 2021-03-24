import java.sql.*;

public class ParticipationGenerator {
    public static void generate(Integer movieId, Integer personId, String role) throws SQLException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();
        final String[] roles = {"actor", "producer", "operator"};

        sb.append("INSERT INTO participation (movieId, personId, role) VALUES");

        if (movieId <= 0) {
            String sql = "SELECT id FROM movie ORDER BY RANDOM() LIMIT 1;";
            movieId = UtilityClass.getInt(db, sql);
        }
        if (personId <= 0) {
            String sql = "SELECT id FROM person ORDER BY RANDOM() LIMIT 1;";
            personId = UtilityClass.getInt(db, sql);
        }
        if (role.isBlank()) {
            role = roles[(int) (Math.random() * roles.length)];
        }

        sb.append(String.format(" ('%d', '%d', '%s')  ON CONFLICT DO NOTHING;", movieId, personId, role));

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }
}
