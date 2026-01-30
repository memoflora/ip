import java.util.Scanner;

public class Flora {
    private static final String line = "    ____________________________________________________________";
    private static final String indent = "     ";
    private static final String greeting = indent + "Hi there! Flora here.\n" + indent + "Ask me anything!";
    private static final String farewell = indent + "Talk to you later—bye!";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println(line);
        System.out.println(greeting);

        while (true) {
            System.out.println(line);
            System.out.println();

            input = sc.nextLine();
            System.out.println(line);

            if (input.equalsIgnoreCase("bye")) {
                System.out.println(farewell);
                break;
            }

            System.out.println(indent + input);
        }

        System.out.println(line);
    }
}
