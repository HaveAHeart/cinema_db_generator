import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class TicketGenerator {
    public static ArrayList<Ticket> generateTicketLab4(int amount, int movieSessionId, int rowNumber, int seat) throws SQLException, ClassNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();
        Statement st;
        Ticket ticket;
        String sql;
        ResultSet rs;
        int resRowNumber = 0;
        int resMovieSessionId = 0;
        int resSeat = 0;
        ArrayList<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            if (movieSessionId <= 0) {
                sql = "SELECT id FROM moviesession ORDER BY RANDOM() LIMIT 1";
                resMovieSessionId = UtilityClass.getInt(db, sql);
            }
            else resMovieSessionId = movieSessionId;

            sql = String.format("SELECT hallid FROM moviesession WHERE id = %d", resMovieSessionId);
            int hallId = UtilityClass.getInt(db, sql);

            if (rowNumber <= 0) {
                sql = String.format("SELECT rowNumber FROM row WHERE hallid = '%s' ORDER BY RANDOM() LIMIT 1", hallId);
                resRowNumber = UtilityClass.getInt(db, sql);
                //System.out.println(resRowNumber);
            }
            else resRowNumber = rowNumber;

            sql = String.format("SELECT id FROM row WHERE hallid = '%s' AND rownumber = '%s'", hallId, resRowNumber);
            int rowId = UtilityClass.getInt(db, sql);

            if (seat <= 0) {
                sql = String.format("SELECT amountSeats FROM row WHERE hallId = '%d' AND rowNumber = '%d'", hallId, resRowNumber);
                int maxSeat = UtilityClass.getInt(db, sql);
                resSeat = (int) (1 + Math.random() * (maxSeat - 1));
            }
            else resSeat = seat;

            sql = "SELECT id FROM cashbox ORDER BY RANDOM() LIMIT 1";
            int cashboxId = UtilityClass.getInt(db, sql);

            sql = String.format("SELECT sessiontime FROM moviesession WHERE id = '%d'", movieSessionId);
            String sessionTime = UtilityClass.getString(db, sql);
            String soldTime = String.format("SELECT TIMESTAMP '%s' - '1 week'::interval * random()", sessionTime);
            ticket = new Ticket(resMovieSessionId, cashboxId, rowId, resSeat, soldTime);
            if (tickets.contains(ticket)) continue;
            tickets.add(ticket);
        }
        db.close();

        HashSet<Ticket> ticketsWithoutDup = new HashSet<>(tickets);
        tickets = new ArrayList<>(ticketsWithoutDup);

        return tickets;
    }

    public static void generate(Integer movieSessionId, Integer rowNumber, Integer seat) throws SQLException, ClassNotFoundException {
        final Connection db = UtilityClass.connectToDB();
        final StringBuilder sb = new StringBuilder();
        Statement st;
        String sql;
        ResultSet rs;

        Ticket ticket = generateTicketLab4(1, movieSessionId, rowNumber, seat).get(0);

        sb.append("INSERT INTO ticket (sessionid, cashboxid, rowid, seat, soldtime) VALUES");
        sb.append(String.format(" ('%d', '%d', '%d', '%d', (%s))",
                ticket.sessionId,
                ticket.cashboxId,
                ticket.rowId,
                ticket.seat,
                ticket.soldtime));
        sb.append(" ON CONFLICT DO NOTHING;");

        UtilityClass.makeUpdate(db, sb.toString());
        db.close();
    }

    public static void generateAdvanced(int amountTickets, int amountSessions) throws FileNotFoundException, SQLException, ClassNotFoundException {
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
