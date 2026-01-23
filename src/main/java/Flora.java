import java.util.Scanner;

public class Flora {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("    ____________________________________________________________");
        System.out.println("     Hi there! Flora here.\n     Ask me anything!");

        while (true) {
            System.out.println("    ____________________________________________________________\n");
            input = sc.nextLine();
            System.out.println(input);

            System.out.println("    ____________________________________________________________");
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("     Talk to you later—bye!");
                break;
            }

            System.out.print("     ");
            System.out.println(input);
        }

        System.out.println("    ____________________________________________________________");
    }
}
