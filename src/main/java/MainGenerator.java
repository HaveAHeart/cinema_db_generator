import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

public class MainGenerator {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, BrokenBarrierException, InterruptedException {
        //CashboxGenerator.generate(4);
        //PersonGenerator.generate(15, "100 years");
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
        //TicketGenerator.generateAdvanced(13, 50);
        //String str = UtilityClass.choose(new File("src/main/resources/movies.txt"));
        //System.out.println(str);

        /*ArrayList<Ticket> tickets = TicketGenerator.generateTicketLab4(5, 1, 0, 0);
        for (int i = 0; i < 5; i++) {
            Ticket ticket = tickets.get(i);
            System.out.println(String.format(" ('%d', '%d', '%d', '%d', (%s))\n",
                    ticket.sessionId,
                    ticket.cashboxId,
                    ticket.rowId,
                    ticket.seat,
                    ticket.soldtime));
        }*/
        //MovieSessionGenerator.generate("", "отличный", 2);
        lab4.runThreads(1000, 2, 310); //commited
        lab4.runThreads(1000, 4, 310); //repeatable
        lab4.runThreads(1000, 8, 310); //serializable
        lab4.runThreads(2500, 2, 310);
        lab4.runThreads(2500, 4, 310);
        lab4.runThreads(2500, 8, 310);
    }
}
