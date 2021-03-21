import java.io.FileNotFoundException;
import java.sql.*;

public class MainGenerator {
    public static void main(String[] args) throws FileNotFoundException, SQLException {
        //CashboxGenerator.generate(4);
        //PersonGenerator.generate(5, "100 years");
        //HallGenerator.generate(3);
        //HallGenerator.generateAdvanced(2);
        //MovieGenerator.generate(4);
        //MovieGenerator.generateAdvanced(4);

        //default partition generator - set movieid/person < 0 or/and role = "" to be chosen at random
        //ParticipationGenerator.generate(3, 4, "actor");

        //RowGenerator.generate("красный", 3);
        //MovieSessionGenerator.generate("Чужой", "красный", 7);
        //TicketGenerator.generate(5, 2, 6);


        //MovieSessionGenerator.generateAdvanced(2);

        //generate amountSessions of movieSessions for a single movie with amountTickets of tickets sold for each one
        TicketGenerator.generateAdvanced(7, 2);
    }
}
