import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class MovieGenerator {
    public static void generate(int amount) throws SQLException, FileNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO movie (name, releaseDate, duration) VALUES");

        for (int i = 0; i < amount; i++) {
            String[] movieData = choose(new File("src/main/resources/movies.txt"));
            sb.append(String.format("\n ('%s', '%s-01-01', cast('%s minutes'::interval as time)),", movieData[0], movieData[1], movieData[2]));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" ON CONFLICT DO NOTHING;");

        Statement st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }

    public static void generateAdvanced(int amount) throws SQLException, FileNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

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

        st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }

    private static String[] choose(File filename) throws FileNotFoundException {
        //reservoir sampling -
        //chance to override result for 1st string = 100%, 2nd = 50%, 3rd = 33%, etc.
        //As a result, we have a fair choose with the equal possibility chances for every string
        String name = null;
        String releaseDate = null;
        String duration = null;
        Random rand = new Random();
        int n = 0;
        for(Scanner sc = new Scanner(filename, "Windows-1251"); sc.hasNext(); ) {
            ++n;
            String line = sc.nextLine();
            if(rand.nextInt(n) == 0) {
                name = line.split(";")[0];
                releaseDate = line.split(";")[5];
                duration = line.split(";")[4];
            }
        }
        return new String[]{name, releaseDate, duration};
    }
}
