import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class lab4 {
    static long globalStart;
    public static void runThreads(int amount, int isolationLevel, int sessionId) throws SQLException, ClassNotFoundException, BrokenBarrierException, InterruptedException, IOException {
        CyclicBarrier cbStart = new CyclicBarrier(3);
        CyclicBarrier cbFin = new CyclicBarrier(4);
        Connection connInsert = UtilityClass.connectToDB();
        connInsert.setTransactionIsolation(isolationLevel);
        Connection connUpdate = UtilityClass.connectToDB();
        connUpdate.setTransactionIsolation(isolationLevel);
        Connection connSelect = UtilityClass.connectToDB();
        connSelect.setTransactionIsolation(isolationLevel);


        ArrayList<Ticket> tickets = TicketGenerator.generateTicketLab4(amount, sessionId, 0, 0);

        connInsert.createStatement().execute(String.format("DELETE FROM TICKET WHERE sessionid = %d;", sessionId));



        TransactionThread threadInsert = new TransactionThread(cbStart, cbFin, connInsert, tickets, TransactionType.INSERT);
        TransactionThread threadUpdate = new TransactionThread(cbStart, cbFin, connUpdate, tickets, TransactionType.UPDATE);
        TransactionThread threadSelect = new TransactionThread(cbStart, cbFin, connSelect, tickets, TransactionType.SELECT);

        System.out.println(String.format("threads created, tickets: %d", tickets.size()));

        globalStart = System.nanoTime();

        threadInsert.start();
        threadUpdate.start();
        threadSelect.start();

        cbFin.await();

        System.out.println("threads finished");

        connInsert.close();
        connUpdate.close();
        connSelect.close();

        List<String> execTimeInsert = threadInsert.getExecTime(); //беды с импортом Pair
        List<String> execTimeUpdate = threadUpdate.getExecTime(); //юзаю аналог из стандартной либы
        List<String> execTimeSelect = threadSelect.getExecTime();




        String outPathInsert = String.format("src/transOutput/insert_%d_%d.txt", amount, isolationLevel);
        String outPathUpdate = String.format("src/transOutput/update_%d_%d.txt", amount, isolationLevel);
        String outPathSelect = String.format("src/transOutput/select_%d_%d.txt", amount, isolationLevel);

        UtilityClass.writeInFile(outPathInsert, execTimeInsert);
        UtilityClass.writeInFile(outPathUpdate, execTimeUpdate);
        UtilityClass.writeInFile(outPathSelect, execTimeSelect);
    }

    private static class TransactionThread extends Thread {
        private final CyclicBarrier cbStart;
        private final CyclicBarrier cbFin;
        private final Connection connection;
        private final ArrayList<Ticket> tickets;
        private final TransactionType transactionType;
        private List<String> execTime;

        public TransactionThread(CyclicBarrier cbStart, CyclicBarrier cbFin, Connection connection,
                                 ArrayList<Ticket> tickets, TransactionType transactionType) {
            this.cbStart = cbStart;
            this.cbFin = cbFin;
            this.connection = connection;
            this.tickets = tickets;
            this.transactionType = transactionType;
        }

        public List<String> getExecTime() { return execTime; }

        public void run() {
            try {
                cbStart.await();
                execTime = transaction(tickets, connection, transactionType);

            } catch (InterruptedException | BrokenBarrierException | SQLException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    cbFin.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }

        private static List<String> transaction(ArrayList<Ticket> tickets, Connection connection, TransactionType type) throws SQLException {
            List<String> execTime = new ArrayList<>();
            AtomicBoolean flag = new AtomicBoolean(false);
            Statement st;
            long start = 0;
            long end = 0;

            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                st = connection.createStatement();
                String sql = "";
                switch (type) {
                    case UPDATE -> sql = String.format("UPDATE ticket SET soldtime = soldtime - '1 second'::interval WHERE sessionid = '%d';",
                            ticket.sessionId);
                    case INSERT -> sql = String.format("INSERT INTO ticket (sessionid, cashboxid, rowid, seat, soldtime) " +
                                    "VALUES ('%d', '%d', '%d', '%d', (%s))  ON CONFLICT DO NOTHING;",
                            ticket.sessionId,
                            ticket.cashboxId,
                            ticket.rowId,
                            ticket.seat,
                            ticket.soldtime);
                    case SELECT -> sql = String.format("SELECT * FROM ticket WHERE sessionid = '%d';",
                            ticket.sessionId);
                }
                do {
                    try {
                        start = System.nanoTime();
                        st.execute(sql);
                        end = System.nanoTime();
                        flag.set(false);
                    } catch (SQLException e) {
                        flag.set(true);
                        //System.out.println("serializable intersection");
                    }
                } while (flag.get());

                execTime.add(i, String.format("%d %d", end - globalStart, end - start));
            }
            return execTime;
        }



    }

    private enum TransactionType {
        INSERT, UPDATE, SELECT
    }
}
