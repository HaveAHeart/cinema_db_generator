import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class HallGenerator {
    public static void generate(int amount) throws SQLException, FileNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO hall (hallname) VALUES");

        for (int i = 0; i < amount; i++) {
            String name = choose(new File("src/main/resources/hall_name.txt"));
            sb.append(String.format("\n ('%s'),", name));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("ON CONFLICT DO NOTHING;");
        Statement st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }

    public static void generateAdvanced(int amount) throws SQLException, FileNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

        for (int i = 0; i < amount; i++) {
            HallGenerator.generate(1);

            String sql = "SELECT hallname FROM hall ORDER BY id DESC LIMIT 1";
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            String hallname = rs.getString(1);

            RowGenerator.generate(hallname, (int) (Math.random() * 15 + 5));
        }
    }


    private static String choose(File filename) throws FileNotFoundException {
        //reservoir sampling -
        //chance to override result for 1st string = 100%, 2nd = 50%, 3rd = 33%, etc.
        //As a result, we have a fair choose with the equal possibility chances for every string
        String name = null;
        Random rand = new Random();
        int n = 0;
        for(Scanner sc = new Scanner(filename); sc.hasNext(); ) {
            ++n;
            String line = sc.nextLine();
            if(rand.nextInt(n) == 0) name = line;
        }
        return name;
    }
}
