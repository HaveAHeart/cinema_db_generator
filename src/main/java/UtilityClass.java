import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class UtilityClass {
    static final String winEncoding = "Windows-1251";

    static Connection connectToDB() throws SQLException, ClassNotFoundException {
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
            for (Scanner sc = new Scanner(filename, winEncoding); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0) result = line;
            }
        }
        return result;
    }

    static void writeInFile(String path, List<String> execTime) throws IOException {
            Files.write(Paths.get(path), execTime, StandardCharsets.UTF_8);
    }
}

