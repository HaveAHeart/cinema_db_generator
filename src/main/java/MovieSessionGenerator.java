import org.postgresql.util.PSQLException;

import java.io.FileNotFoundException;
import java.sql.*;

public class MovieSessionGenerator {
    public static void generate(String movieName, String hallName, int amount) throws SQLException {
        final Connection db = UtilityClass.connectToDB();
        String sql;


        if (movieName.isBlank()) {
            sql = "SELECT id, releaseDate FROM movie ORDER BY RANDOM() LIMIT 1;";
        } else {
            sql = String.format("SELECT id, releaseDate FROM movie WHERE name = '%s';", movieName);
        }
        Statement st = db.createStatement();
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        int movieId = rs.getInt(1);
        String movieDate = rs.getString(2);


        if (hallName.isBlank()) {
            sql = "SELECT id FROM hall ORDER BY RANDOM() LIMIT 1";
        } else {
            sql = String.format("SELECT id FROM hall WHERE hallname = '%s';", hallName);
        }
        int hallId = UtilityClass.getInt(db, sql);


        String sessionTime = String.format("(SELECT DATE '%s' + ('1 month'::interval) + ('1 year'::interval * random()))", movieDate);
        Integer sessionPrice = (int) (250 + Math.random() * 250);

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO moviesession (movieid, hallid, sessiontime, sessionprice) VALUES");
        for (int i = 0; i < amount; i++) {
            sb.append(String.format(" ('%d', '%d', %s, '%d'),", movieId, hallId, sessionTime, sessionPrice));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON CONFLICT DO NOTHING;");
        try {
            UtilityClass.makeUpdate(db, sb.toString());
        }
        catch (PSQLException e) {
            System.out.println("time intersection found");
        }

        db.close();
    }

    public static void generateAdvanced(int amount) throws SQLException, FileNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        HallGenerator.generateAdvanced(1);
        MovieGenerator.generateAdvanced(1);

        String sql = "SELECT hallname FROM hall ORDER BY id DESC LIMIT 1";
        final String hallName = UtilityClass.getString(db, sql);

        sql = "SELECT name FROM movie ORDER BY id DESC LIMIT 1";
        final String movieName = UtilityClass.getString(db, sql);
        MovieSessionGenerator.generate(movieName, hallName, amount);

        db.close();
    }
}
