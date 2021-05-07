public class Ticket {
    final int sessionId;
    final int cashboxId;
    final int rowId;
    final int seat;
    final String soldtime;

    public Ticket(int sessionId, int cashboxId, int rowId, int seat, String soldtime) {
        this.sessionId = sessionId;
        this.cashboxId = cashboxId;
        this.rowId = rowId;
        this.seat = seat;
        this.soldtime = soldtime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        return (this.sessionId == other.sessionId &&
            this.cashboxId == other.cashboxId &&
            this.rowId == other.rowId &&
            this.seat == other.seat &&
            this.soldtime.equals(other.soldtime));
    }
}
