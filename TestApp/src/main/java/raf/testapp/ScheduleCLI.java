package raf.testapp;

import org.specification.classes.FileFormat;
import org.specification.classes.Place;
import org.specification.exceptions.PlaceAlreadyExistsException;
import org.specification.specification.ScheduleManager;
import org.specification.specification.wrappers.ScheduleFilterSpecificationWrapper;
import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScheduleCLI {
    static ScheduleSpecificationWrapper schedule;
    static ScheduleFilterSpecificationWrapper scheduleFilter;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        ASCIIArtPrinter.printAsciiArt();
        System.out.println("Welcome to the Interactive Schedule CLI!");
        System.out.println("For list of available commands type 'help'");

        boolean chosen = false;
        if(args.length != 0) {
            if (args[0].equals("traditional")) {
                initializeSchedule(1);
                chosen = true;
            } else if(args[0].equals("recurring")){
                initializeSchedule(2);
                chosen = true;
            }
        }
        if(!chosen){
            System.out.println("Choose one of the implemented schedulers: 1 or 2");
            int choice = scanner.nextInt();
            initializeSchedule(choice);
        }

        while (!exit) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();

            // Split the input into command and arguments
            String[] parts = input.split("\\s+");
            String command = parts[0];

            switch (command.toLowerCase()) {
                case "loadschedule":
                    // loadschedule -s schedule.json -c config.json -d days.json -r recurring
                    loadSchedule(parts[2], parts[4], parts[6], parts[8]);
                    break;
                case "saveschedule":
                    // saveschedule -s schedule.json -f json
                    saveSchedule(parts[2], parts[4]);
                    break;
                case "showtimeslots":
                    // showtimeslots
                    showTimeSlots();
                    break;
                case "filterbydate":
                    // filterbydate -d 2020-01-01
                    filterByDate(parts[2]);
                    break;
                case "filterbydateperiod":
                    // filterbydate -sd 2020-01-01 -ed 2020-01-02 -d monday
                    filterByDatePeriod(parts[2], parts[4], parts[6]);
                    break;
                case "filterbytime":
                    // filterbytime -st 08:00 -et 12:00
                    filterByTime(parts[2], parts[4]);
                    break;
                case "filterbyplace":
                    // filterbyplace -p place1
                    filterByPlace(parts[2]);
                    break;
                case "filterbyadditionalplace":
                    // filterbyadditionalplace -a "key1:value1,key2:value2"
                    filterByAdditionalPlace(parts[2]);
                    break;
                case "filterbyadditionaltimeslot":
                    // filterbyadditionaltimeslot -a "key1:value1,key2:value2"
                    filterByAdditionalTimeSlot(parts[2]);
                    break;
                case "addtimeslot":
                    // addtimeslot -n name -d 2020-01-01 -st 08:00 -et 12:00 -p place1 -a "key1:value1,key2:value2"
                    addTimeSlot(parts[2], parts[4], parts[6], parts[8], parts[10], parts[12]);
                    break;
                case "addplace":
                    // addplace -n place1 -c 10 -pc 10 -pr true -sb true -a "key1:value1,key2:value2"
                    addPlace(parts[2], parts[4], parts[6], parts[8], parts[10], parts[12]);
                    break;
                case "schedulestartend":
                    // schedulestartend -s 2020-01-01 -e 2030-01-01
                    schedulestartend(parts[2], parts[4]);
                    break;
                case "addplacesfromfile":
                    // addplacesfromfile -p filePath.txt
                    addPlaces(parts[2]);
                    break;
                case "addexcludedday":
                    // addexcludedday -d 2020-01-01
                    addexcludedday(parts[2]);
                    break;
                case "help":
                    // help
                    ASCIIArtPrinter.printAvailableCommands();
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown command. For available commands type 'help'");
            }
        }
        System.out.println("Exiting program...");
        scanner.close();
    }

    private static void initializeSchedule(int choice){
        String nameForClass = null;
        if(choice == 1){
            nameForClass = "raf.traditional.TraditionalScheduler";
        }else{
            nameForClass = "raf.recurring.RecurringScheduler";
        }
        if(nameForClass == null){
            System.out.println("Duvaj ga");
        }
        try {
            Class.forName(nameForClass);
            schedule = ScheduleManager.getScheduler();
            scheduleFilter = ScheduleManager.getFilter();
        } catch (ClassNotFoundException e) {
            System.out.println("Duvaj ga1");
            throw new RuntimeException(e);
        }

    }

    private static LocalTime parseTime(String time) {
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        return LocalTime.of(hour, minute);
    }

    private static LocalDate parseDate(String date) {
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        return LocalDate.of(year, month, day);
    }

    private static void addexcludedday(String dateP){
        LocalDate date = parseDate(dateP);
        schedule.addExcludedDay(date);
    }

    private static void addplacesfromfile(String filePath){
        try{
            File file = new File(filePath);
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] parts = line.split(",");
                String name = parts[0];
                int capacity = Integer.parseInt(parts[1]);
                int computer = Integer.parseInt(parts[2]);
                boolean projector = Boolean.parseBoolean(parts[3]);
                boolean smartBoard = Boolean.parseBoolean(parts[4]);
                Map<String, String> additional = new HashMap<>();
                if (parts[5] != null) {
                    String[] additionalParts = parts[5].split(",");
                    for (String additionalPart : additionalParts) {
                        String[] additionalPartParts = additionalPart.split(":");
                        String key = additionalPartParts[0];
                        String value = additionalPartParts[1];
                        additional.put(key, value);
                    }
                }
                Place place = new Place(name, capacity, computer, projector, smartBoard, additional);
                try{
                    schedule.addPlace(place);
                } catch (PlaceAlreadyExistsException e){
                    System.out.println("Place already exists");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private static void schedulestartend(String startDateP, String endDateP){
        try{
            LocalDate startDate = parseDate(startDateP);
            LocalDate endDate = parseDate(endDateP);
            if(endDate.isBefore(startDate)) {
                System.out.println("End date is before start date");
                return;
            }
            schedule.setStartScheduleDate(startDate);
            schedule.setEndScheduleDate(endDate);
        }catch (Exception e){
            System.out.println("Error setting start and end date");
            e.printStackTrace();
        }
    }

    public static void saveSchedule(String schedulePath, String format) {
        try {
            schedule.saveSchedule(schedulePath, FileFormat.valueOf(format.toUpperCase()));
        } catch (IOException e) {
            System.out.println("Error saving schedule");
        }
    }

    public static void loadSchedule(String schedulePath, String configPath, String daysPath , String recurringP) {
        try {
            schedule.loadSchedule(schedulePath, configPath, Boolean.parseBoolean(recurringP), daysPath);
        } catch (Exception e) {
            System.out.println("Error loading schedule");
            e.printStackTrace();
        }
    }

    public static void filterByAdditionalTimeSlot(String additionalP) {
        Map<String, String> additional = new HashMap<>();
        if (additionalP != null) {
            String[] additionalParts = additionalP.split(",");
            for (String additionalPart : additionalParts) {
                String[] additionalPartParts = additionalPart.split(":");
                String key = additionalPartParts[0];
                String value = additionalPartParts[1];
                scheduleFilter.addTimeSlotAdditional(key, value);
            }
        }
    }

    public static void filterByAdditionalPlace(String additionalP) {
        Map<String, String> additional = new HashMap<>();
        if (additionalP != null) {
            String[] additionalParts = additionalP.split(",");
            for (String additionalPart : additionalParts) {
                String[] additionalPartParts = additionalPart.split(":");
                String key = additionalPartParts[0];
                String value = additionalPartParts[1];
                scheduleFilter.addPlaceAdditional(key, value);
            }
        }
    }

    public static void filterByPlace(String placeName) {
        Place place = null;
        for (Place p : schedule.getPlaces()) {
            if (p.getName().equals(placeName)) {
                place = p;
                break;
            }
        }
        if (place == null) {
            System.out.println("Place not found");
            return;
        }
        scheduleFilter.setPlace(place);
    }

    public static void filterByTime(String startTimeP, String endTimeP) {
        LocalTime startTime = parseTime(startTimeP);
        LocalTime endTime = parseTime(endTimeP);
        scheduleFilter.setTime(startTime, endTime);
    }

    public static void filterByDate(String dateP) {
        LocalDate date = parseDate(dateP);
        scheduleFilter.setDate(date);
    }

    public static void filterByDatePeriod(String startDateP, String endDateP, String dayOfWeekP) {
        LocalDate startDate = parseDate(startDateP);
        LocalDate endDate = parseDate(endDateP);
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekP.toUpperCase());
        scheduleFilter.setPeriod(startDate, endDate, dayOfWeek);
    }

    public static void showTimeSlots() {
        System.out.println(schedule.getAppointments());
    }

    public static void addPlaces(String filePath){

        try{
            File file = new File(filePath);
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String [] parts = sc.nextLine().split(",");
                int capacity = 0;
                if(parts.length >= 2)
                    capacity = Integer.parseInt(parts[1]);

                int computer = 0;
                if(parts.length >= 3)
                    computer = Integer.parseInt(parts[2]);

                boolean projector = false;
                if(parts.length >= 4)
                    projector = Boolean.parseBoolean(parts[3]);

                boolean smartBoard = false;
                if(parts.length >= 5)
                    smartBoard = Boolean.parseBoolean(parts[4]);


                Map<String, String> additional = new HashMap<>();
                if (parts.length >= 6 && parts[5] != null) {
                    String[] additionalParts = parts[5].split(",");
                    for (String additionalPart : additionalParts) {
                        String[] additionalPartParts = additionalPart.split(":");
                        String key = additionalPartParts[0];
                        String value = additionalPartParts[1];
                        additional.put(key, value);
                    }
                }
                Place place = new Place(parts[0], capacity, computer, projector, smartBoard, additional);
                try{
                    schedule.addPlace(place);
                }catch (Exception e){
                    System.out.println("JA");
                    System.out.println("Place already exists");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addPlace(String name, String capacityP, String computerP, String projectorP, String smartBoardP, String additionalP) {
        int capacity = Integer.parseInt(capacityP);
        int computer = Integer.parseInt(computerP);
        boolean projector = Boolean.parseBoolean(projectorP);
        boolean smartBoard = Boolean.parseBoolean(smartBoardP);
        Map<String, String> additional = new HashMap<>();
        if (additionalP != null) {
            String[] additionalParts = additionalP.split(",");
            for (String additionalPart : additionalParts) {
                String[] additionalPartParts = additionalPart.split(":");
                String key = additionalPartParts[0];
                String value = additionalPartParts[1];
                additional.put(key, value);
            }
        }
        Place place = new Place(name, capacity, computer, projector, smartBoard, additional);
        try{
            schedule.addPlace(place);
        }catch (Exception e){
            System.out.println("Place already exists");
        }
    }

    public static void addTimeSlot(String name, String dateP, String startTimeP, String endTimeP, String placeNameP, String additionalP) {
        LocalDate date = parseDate(dateP);
        LocalTime startTime = parseTime(startTimeP);
        LocalTime endTime = parseTime(endTimeP);
        Place place = null;

        // Find place
        for (Place p : schedule.getPlaces()) {
            if (p.getName().equals(placeNameP)) {
                place = p;
                break;
            }
        }
        if (place == null) {
            System.out.println("Place not found");
            return;
        }
        // Parse additional
        Map<String, String> additional = new HashMap<>();
        if (additionalP != null) {
            String[] additionalParts = additionalP.split(",");
            for (String additionalPart : additionalParts) {
                String[] additionalPartParts = additionalPart.split(":");
                String key = additionalPartParts[0];
                String value = additionalPartParts[1];
                additional.put(key, value);
            }
        }
        try{
            schedule.addTimeSlot(name, date, startTime, endTime, place, additional);
        }catch (Exception e){
            System.out.println("Time slot already occupied");
        }
    }
}
