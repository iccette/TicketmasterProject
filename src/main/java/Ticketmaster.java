import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Ticketmaster {
    private static final Scanner input = new Scanner(System.in);
    private static final Random random = new Random();
    private static final Path SHOW_FOLDER = Paths.get("shows");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- TICKETMASTER MENU ---");
            System.out.println("1. Create/manage a show");
            System.out.println("2. Sell a ticket");
            System.out.println("3. Print a show report");
            System.out.println("4. Exit");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    createShow();
                    break;
                case 2:
                    sellTicket();
                    break;
                case 3:
                    printReport();
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Please enter 1, 2, 3, or 4.");
            }
        }
    }

    private static void createShow() {
        String showName = readShowName();
        Path file = showFile(showName);
        if (Files.exists(file)) {
            System.out.println("That show already exists. It was not overwritten.");
            return;
        }

        int amount = readInt("How many tickets? ");
        if (amount <= 0) {
            System.out.println("The ticket count must be greater than zero.");
            return;
        }

        ArrayList<Ticket> tickets = new ArrayList<>();
        for (int number = 1; number <= amount; number++) {
            tickets.add(new Ticket(number));
        }

        if (saveTickets(showName, tickets)) {
            System.out.println("Created " + showName + " with " + amount + " tickets.");
        }
    }

    private static void sellTicket() {
        String showName = readShowName();
        ArrayList<Ticket> tickets = loadTickets(showName);
        if (tickets == null) {
            return;
        }

        while (true) {
            int number = readInt("Enter a ticket number (0 to cancel): ");
            if (number == 0) {
                return;
            }
            if (number < 1 || number > tickets.size()) {
                System.out.println("That ticket number does not exist.");
                continue;
            }

            Ticket ticket = tickets.get(number - 1);
            if (ticket.isSold()) {
                System.out.println("That seat is sold. Please choose another.");
                continue;
            }

            String purchaser = readNonEmpty("Enter purchaser's name: ");
            int code = 100000 + random.nextInt(900000);
            ticket.sellTo(purchaser, code);

            if (saveTickets(showName, tickets)) {
                System.out.println("Sold ticket #" + number + " to " + purchaser);
                System.out.println("Confirmation code: " + code);
            }
            return;
        }
    }

    private static void printReport() {
        String showName = readShowName();
        ArrayList<Ticket> tickets = loadTickets(showName);
        if (tickets == null) {
            return;
        }

        System.out.println("\nReport for: " + showName);
        System.out.printf("%-12s %-24s %-18s%n", "Ticket #", "Purchaser", "Confirmation Code");
        for (Ticket ticket : tickets) {
            String name = ticket.isSold() ? ticket.getPurchaserName() : "AVAILABLE";
            System.out.printf("%-12d %-24s %-18d%n",
                    ticket.getTicketNumber(), name, ticket.getConfirmationCode());
        }
    }

    private static String readShowName() {
        return readNonEmpty("Enter show name: ");
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = input.nextLine().trim();
            if (!answer.isEmpty()) {
                return answer;
            }
            System.out.println("This cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static Path showFile(String showName) {
        String safeName = showName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return SHOW_FOLDER.resolve(safeName + ".txt");
    }

    private static boolean saveTickets(String showName, ArrayList<Ticket> tickets) {
        List<String> lines = new ArrayList<>();
        for (Ticket ticket : tickets) {
            // A tab separates each field in the saved file.
            lines.add(ticket.getTicketNumber() + "\t"
                    + ticket.getPurchaserName().replace('\t', ' ') + "\t"
                    + ticket.getConfirmationCode());
        }
        try {
            Files.createDirectories(SHOW_FOLDER);
            Files.write(showFile(showName), lines);
            return true;
        } catch (IOException exception) {
            System.out.println("Could not save tickets: " + exception.getMessage());
            return false;
        }
    }

    private static ArrayList<Ticket> loadTickets(String showName) {
        Path file = showFile(showName);
        if (!Files.exists(file)) {
            System.out.println("Show not found. Please create it first.");
            return null;
        }

        ArrayList<Ticket> tickets = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(file)) {
                String[] fields = line.split("\t", -1);
                if (fields.length != 3) {
                    throw new IllegalArgumentException("Incorrect ticket format");
                }
                int number = Integer.parseInt(fields[0]);
                String purchaser = fields[1];
                int code = Integer.parseInt(fields[2]);
                tickets.add(new Ticket(number, purchaser, code));
            }
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("Could not read show: " + exception.getMessage());
            return null;
        }
        return tickets;
    }
}
