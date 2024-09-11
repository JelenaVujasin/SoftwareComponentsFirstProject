package raf.testapp;

import org.apache.commons.cli.*;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Options options = defineOptions();
        CommandLineParser parser = new DefaultParser();

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();

            // Exit condition
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {
                String[] arguments = input.split(" ");
                CommandLine cmd = parser.parse(options, arguments);
                processCommand(cmd);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Exiting program...");
    }

    private static Options defineOptions() {
        Options options = new Options();

        // Define commands here
        Option greet = Option.builder("g")
                .longOpt("greet")
                .hasArg()
                .desc("Greet a user")
                .build();

        Option add = Option.builder("a")
                .longOpt("add")
                .numberOfArgs(2)
                .desc("Add two numbers")
                .build();

        options.addOption(greet);
        options.addOption(add);

        return options;
    }

    private static void processCommand(CommandLine cmd) {
        if (cmd.hasOption("greet")) {
            System.out.println("Hello, " + cmd.getOptionValue("greet") + "!");
        } else if (cmd.hasOption("add")) {
            String[] numbers = cmd.getOptionValues("add");
            int num1 = Integer.parseInt(numbers[0]);
            int num2 = Integer.parseInt(numbers[1]);
            System.out.println("The sum is: " + (num1 + num2));
        } else {
//            HelpFormatter formatter = new HelpFormatter();
//            formatter.printHelp("utility-name", options);
        }
    }
}
