import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class UtilityClass {
    static Connection connectToDB() throws SQLException {
        final String url = "jdbc:postgresql://localhost:5432/postgres";
        return DriverManager.getConnection(url, "intellijIdea", "1234");
    }

    static int getInt(Connection db, String sql) throws SQLException {
        Statement st = db.createStatement();
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        return rs.getInt(1);
    }

    static String getString(Connection db, String sql) throws SQLException {
        Statement st = db.createStatement();
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        return rs.getString(1);
    }

    static void makeUpdate(Connection db, String sql) throws SQLException {
        Statement st = db.createStatement();
        st.executeUpdate(sql);
    }

    static String choose(File filename, boolean isUTF) throws FileNotFoundException {
        //reservoir sampling -
        //chance to override result for 1st string = 100%, 2nd = 50%, 3rd = 33%, etc.
        //As a result, we have a fair choose with the equal possibility chances for every string
        String result = null;
        Random rand = new Random();
        int n = 0;
        if (isUTF) {
            for (Scanner sc = new Scanner(filename); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0) result = line;
            }
        } else {
            for (Scanner sc = new Scanner(filename, "Windows-1251"); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0) result = line;
            }
        }
        return result;
    }
}
