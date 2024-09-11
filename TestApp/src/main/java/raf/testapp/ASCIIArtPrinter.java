package raf.testapp;

public class ASCIIArtPrinter {
    public static void printAsciiArt(){
        System.out.println("   _____      _              _       _       _____ _      _____ ");
        System.out.println("  / ____|    | |            | |     | |     / ____| |    |_   _|");
        System.out.println(" | (___   ___| |__   ___  __| |_   _| | ___| |    | |      | |  ");
        System.out.println("  \\___ \\ / __| '_ \\ / _ \\/ _` | | | | |/ _ \\ |    | |      | |  ");
        System.out.println("  ____) | (__| | | |  __/ (_| | |_| | |  __/ |____| |____ _| |_ ");
        System.out.println(" |_____/ \\___|_| |_|\\___|\\__,_|\\__,_|_|\\___|\\_____|______|_____|");
        System.out.println("                                                                ");
    }

    public static void printAvailableCommands() {
        System.out.println("Available commands:");
        System.out.println("loadschedule -c config.json -s schedule.json");
        System.out.println("saveschedule -c config.json -s schedule.json -f json");
        System.out.println("showtimeslots");
        System.out.println("filterbydate -d 2020-01-01");
        System.out.println("filterbydateperiod -sd 2020-01-01 -ed 2020-01-02 -d monday");
        System.out.println("filterbytime -st 08:00 -et 12:00");
        System.out.println("filterbyplace -p place1");
        System.out.println("filterbyadditionalplace -a \"key1:value1,key2:value2\"");
        System.out.println("filterbyadditionaltimeslot -a \"key1:value1,key2:value2\"");
        System.out.println("addtimeslot -n name -d 2020-01-01 -st 08:00 -et 12:00 -p place1 -a \"key1:value1,key2:value2\"");
        System.out.println("addplace -n place1 -c 10 -pc 10 -pr true -sb true -a \"key1:value1,key2:value2\"");
        System.out.println("exit");
    }
}
