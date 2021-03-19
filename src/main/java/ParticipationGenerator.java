import java.sql.*;

public class ParticipationGenerator {
    public static void generate(Integer movieId, Integer personId, String role) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO participation (movieId, personId, role) VALUES");

        if (movieId <= 0) {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT id FROM movie ORDER BY RANDOM() LIMIT 1;");
            resultSet.next();
            movieId = resultSet.getInt(1);
        }
        if (personId <= 0) {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT id FROM person ORDER BY RANDOM() LIMIT 1;");
            resultSet.next();
            personId = resultSet.getInt(1);
        }
        if (role.isBlank()) {
            String[] roles = {"actor", "producer", "operator"};
            role = roles[(int) (Math.random() * 3)];
        }

        sb.append(String.format(" ('%d', '%d', '%s')  ON CONFLICT DO NOTHING;", movieId, personId, role));

        Statement st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }
}
