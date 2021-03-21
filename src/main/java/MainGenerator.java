import java.io.FileNotFoundException;
import java.sql.*;

public class MainGenerator {
    public static void main(String[] args) throws FileNotFoundException, SQLException {
        //CashboxGenerator.generate(5);
        //PersonGenerator.generate(5, "100 years");
        //HallGenerator.generate(5);
        //MovieGenerator.generate(2);

        //default partition generator - set movieid/person < 0 or/and role = "" to be chosen at random
        //ParticipationGenerator.generate(-1, -1, "");

        //RowGenerator.generate("массовый", 3);
        MovieSessionGenerator.generate("", "", 3);
        //TicketGenerator.generate(-1, -1, -1);

        //generate a movie with 4 persons participating as producer, operator and two actors
        //MovieSessionGenerator.generateAdvanced(5);

        //generate amountSessions of movieSessions for a single movie with amountTickets of tickets sold for each one
        //TicketGenerator.generateAdvanced(20, 3);
    }
}
