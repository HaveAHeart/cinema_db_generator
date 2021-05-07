import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class MovieGenerator {
    public static void generate(int amount) throws SQLException, FileNotFoundException, ClassNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final String separator = ";";
        final String moviesPath = "src/main/resources/movies.txt";
        final StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO movie (name, releaseDate, duration) VALUES");
        for (int i = 0; i < amount; i++) {
            String[] movieData = UtilityClass.choose(new File(moviesPath), false).split(separator);
            sb.append(String.format("\n ('%s', '%s-01-01', cast('%s minutes'::interval as time)),", movieData[0], movieData[5], movieData[4]));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }

    public static void generateAdvanced(int amount) throws SQLException, FileNotFoundException, ClassNotFoundException {
        final Connection db = UtilityClass.connectToDB();

        MovieGenerator.generate(amount);
        PersonGenerator.generate(amount*4, "100 years");

        String sql = String.format("SELECT id FROM movie ORDER BY id DESC LIMIT '%d'", amount);
        Statement st = db.createStatement();
        ResultSet rsMovies = st.executeQuery(sql);

        sql = String.format("SELECT id FROM person ORDER BY id DESC LIMIT '%d'", amount * 4);
        st = db.createStatement();
        ResultSet rsPersons = st.executeQuery(sql);

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO participation (movieId, personId, role) VALUES");

        int movieId = 0;
        int personId = 0;
        for (int i = 0; i < amount * 4; i++) {
            if (i % 4 == 0) {
                rsMovies.next();
                movieId = rsMovies.getInt(1);

                rsPersons.next();
                personId = rsPersons.getInt(1);
                sb.append(String.format(" ('%d', '%d', 'producer'),", movieId, personId));
            }
            else if (i % 4 == 1) {
                rsPersons.next();
                personId = rsPersons.getInt(1);
                sb.append(String.format(" ('%d', '%d', 'operator'),", movieId, personId));
            }
            else {
                rsPersons.next();
                personId = rsPersons.getInt(1);
                sb.append(String.format(" ('%d', '%d', 'actor'),", movieId, personId));
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }
}
