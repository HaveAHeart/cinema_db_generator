import java.io.FileNotFoundException;
import java.sql.*;

public class TicketGenerator {
    public static void generate(Integer movieSessionId, Integer rowNumber, Integer seat) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");
        Statement st;
        String sql;
        ResultSet rs;

        if (movieSessionId <= 0) {
            sql = "SELECT id FROM moviesession ORDER BY RANDOM() LIMIT 1";
            st = db.createStatement();
            rs = st.executeQuery(sql);
            rs.next();
            movieSessionId = rs.getInt(1);
        }

        sql = String.format("SELECT hallid FROM moviesession WHERE id = %d", movieSessionId);
        st = db.createStatement();
        rs = st.executeQuery(sql);
        rs.next();
        int hallId = rs.getInt(1);

        if (rowNumber <= 0) {
            sql = String.format("SELECT rowNumber FROM row WHERE hallid = '%s' ORDER BY RANDOM() LIMIT 1", hallId);
            st = db.createStatement();
            rs = st.executeQuery(sql);
            rs.next();
            rowNumber = rs.getInt(1);
        }

        sql = String.format("SELECT id FROM row WHERE hallid = '%s' AND rownumber = '%s'", hallId, rowNumber);
        st = db.createStatement();
        rs = st.executeQuery(sql);
        rs.next();
        int rowId = rs.getInt(1);

        if (seat <= 0) {
            sql = String.format("SELECT amountSeats FROM row WHERE hallId = '%d' AND rowNumber = '%d'", hallId, rowNumber);
            st = db.createStatement();
            rs = st.executeQuery(sql);
            rs.next();
            int maxSeat = rs.getInt(1);
            seat = (int) (1 + Math.random() * (maxSeat - 1));
        }

        sql = "SELECT id FROM cashbox ORDER BY RANDOM() LIMIT 1";
        st = db.createStatement();
        rs = st.executeQuery(sql);
        rs.next();
        int cashboxId = rs.getInt(1);

        sql = String.format("SELECT sessiontime FROM moviesession WHERE id = '%d'", movieSessionId);
        st = db.createStatement();
        rs = st.executeQuery(sql);
        rs.next();
        String sessionTime = rs.getString(1);
        String soldTime = String.format("SELECT TIMESTAMP '%s' - '1 week'::interval * random()", sessionTime);


        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ticket (sessionid, cashboxid, rowid, seat, soldtime) VALUES");
        sb.append(String.format(" ('%d', '%d', '%d', '%d', (%s))", movieSessionId, cashboxId, rowId, seat, soldTime));
        sb.append(" ON CONFLICT DO NOTHING;");

        st = db.createStatement();
        //System.out.println(sb.toString());
        st.executeUpdate(sb.toString());
    }

    public static void generateAdvanced(int amountTickets, int amountSessions) throws FileNotFoundException, SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Connection db = DriverManager.getConnection(url, "intellijIdea", "1234");

        MovieSessionGenerator.generateAdvanced(amountSessions);

        String sql = String.format("SELECT id FROM moviesession ORDER BY id DESC LIMIT '%d'", amountSessions);
        Statement st = db.createStatement();
        ResultSet rs = st.executeQuery(sql);

        int movieSessionId;
        for (int i = 0; i < amountSessions; i++) {
            rs.next();
            movieSessionId = rs.getInt(1);
            for (int j = 0; j < amountTickets; j++) {
                TicketGenerator.generate(movieSessionId, 0, 0);
            }
        }

    }
}
