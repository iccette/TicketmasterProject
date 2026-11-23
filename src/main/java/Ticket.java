public class Ticket {
    private int ticketNumber;
    private String purchaserName;
    private int confirmationCode;

    public Ticket(int ticketNumber) {
        this.ticketNumber = ticketNumber;
        this.purchaserName = "";
        this.confirmationCode = 0;
    }

    public Ticket(int ticketNumber, String purchaserName, int confirmationCode) {
        this.ticketNumber = ticketNumber;
        this.purchaserName = purchaserName;
        this.confirmationCode = confirmationCode;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public int getConfirmationCode() {
        return confirmationCode;
    }

    public boolean isSold() {
        return !purchaserName.isEmpty();
    }

    public void sellTo(String name, int code) {
        purchaserName = name;
        confirmationCode = code;
    }
}
