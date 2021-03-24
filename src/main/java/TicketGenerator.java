import java.io.FileNotFoundException;
import java.sql.*;

public class TicketGenerator {
    public static void generate(Integer movieSessionId, Integer rowNumber, Integer seat) throws SQLException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();
        Statement st;
        String sql;
        ResultSet rs;

        if (movieSessionId <= 0) {
            sql = "SELECT id FROM moviesession ORDER BY RANDOM() LIMIT 1";
            movieSessionId = UtilityClass.getInt(db, sql);
        }

        sql = String.format("SELECT hallid FROM moviesession WHERE id = %d", movieSessionId);
        int hallId = UtilityClass.getInt(db, sql);

        if (rowNumber <= 0) {
            sql = String.format("SELECT rowNumber FROM row WHERE hallid = '%s' ORDER BY RANDOM() LIMIT 1", hallId);
            rowNumber = UtilityClass.getInt(db, sql);
        }

        sql = String.format("SELECT id FROM row WHERE hallid = '%s' AND rownumber = '%s'", hallId, rowNumber);
        int rowId = UtilityClass.getInt(db, sql);

        if (seat <= 0) {
            sql = String.format("SELECT amountSeats FROM row WHERE hallId = '%d' AND rowNumber = '%d'", hallId, rowNumber);
            int maxSeat = UtilityClass.getInt(db, sql);
            seat = (int) (1 + Math.random() * (maxSeat - 1));
        }

        sql = "SELECT id FROM cashbox ORDER BY RANDOM() LIMIT 1";
        int cashboxId = UtilityClass.getInt(db, sql);

        sql = String.format("SELECT sessiontime FROM moviesession WHERE id = '%d'", movieSessionId);
        String sessionTime = UtilityClass.getString(db, sql);
        String soldTime = String.format("SELECT TIMESTAMP '%s' - '1 week'::interval * random()", sessionTime);


        sb.append("INSERT INTO ticket (sessionid, cashboxid, rowid, seat, soldtime) VALUES");
        sb.append(String.format(" ('%d', '%d', '%d', '%d', (%s))", movieSessionId, cashboxId, rowId, seat, soldTime));
        sb.append(" ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }

    public static void generateAdvanced(int amountTickets, int amountSessions) throws FileNotFoundException, SQLException {
        final Connection db = UtilityClass.connectToDB();
        MovieSessionGenerator.generateAdvanced(amountSessions);

        String sql = String.format("SELECT id FROM moviesession ORDER BY id DESC LIMIT '%d'", amountSessions);
        Statement st = db.createStatement();
        ResultSet rs = st.executeQuery(sql);

        int movieSessionId;
        for (int i = 0; i < amountSessions; i++) {
            rs.next();
            movieSessionId = rs.getInt(1);
            for (int j = 0; j < amountTickets; j++) TicketGenerator.generate(movieSessionId, 0, 0);
        }

        db.close();
    }
}
