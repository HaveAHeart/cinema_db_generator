import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class PersonGenerator {
    public static void generate(int amount, String period) throws SQLException, FileNotFoundException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

        final String age = String.format("cast((now()) - ('%s'::interval * random()) - '20 years'::interval as date)", period);
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO person (name, surname, dateOfBirth) VALUES");

        for (int i = 0; i < amount; i++) {
            String[] nameSurname = choose(new File("src/main/resources/full_name.txt"));
            sb.append(String.format("\n ('%s', '%s', %s),", nameSurname[0], nameSurname[1], age));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("ON CONFLICT DO NOTHING;");
        Statement st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }

    private static String[] choose(File filename) throws FileNotFoundException {
        //reservoir sampling -
        //chance to override result for 1st string = 100%, 2nd = 50%, 3rd = 33%, etc.
        //As a result, we have a fair choose with the equal possibility chances for every string
        String name = null;
        String surname = null;
        Random rand = new Random();
        int n = 0;
        for(Scanner sc = new Scanner(filename); sc.hasNext(); ) {
            ++n;
            String line = sc.nextLine();
            if(rand.nextInt(n) == 0) {
                name = line.split(" ")[1];
                surname = line.split(" ")[0];
            }
        }
        return new String[]{name, surname};
    }

}
