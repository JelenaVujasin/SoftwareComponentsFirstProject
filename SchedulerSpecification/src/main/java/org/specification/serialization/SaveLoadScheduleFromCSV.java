package org.specification.serialization;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.specification.classes.ConfigMapping;
import org.specification.classes.DayMapping;
import org.specification.classes.Place;
import org.specification.classes.TimeSlot;
import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;
/**
 * Class for loading and saving schedule from and to CSV file
 */
public class SaveLoadScheduleFromCSV {
    private final ScheduleSpecificationWrapper schedule;
    public SaveLoadScheduleFromCSV(ScheduleSpecificationWrapper schedule) {
        this.schedule = schedule;
    }

    public void loadData(String filePath, String configPath,boolean recurring,String configDays) throws IOException {
        loadApache(filePath, configPath,recurring,configDays);
    }

    public void exportData(String path) throws IOException{
        writeData(path);
    }

    private void loadApache(String filePath, String configPath,boolean recurring,String configDays) throws IOException {
        List<ConfigMapping> columnMappings = readConfig(configPath);
        List<DayMapping> dayMappings = readConfigDays(configDays);
        Map<String,String> days = new HashMap<>();
        Map<Integer, String> mappings = new HashMap<>();
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal());
        }

        for(DayMapping dayMapping : dayMappings) {
            days.put(dayMapping.getOriginalDay(), dayMapping.getDayOfWeek());
        }

        FileReader fileReader = new FileReader(filePath);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1),Locale.ENGLISH);

        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendPattern(mappings.get(-1))
                .optionalStart()
                .appendPattern(mappings.get(-2))
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        DateTimeFormatter fmt1 = new DateTimeFormatterBuilder()
                .appendPattern(mappings.get(-2))
                .optionalStart()
                .appendPattern("HH:00")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();


        for (CSVRecord record : parser) {
            TimeSlot appointment = new TimeSlot();

            for (ConfigMapping entry : columnMappings) {
                int columnIndex = entry.getIndex();


                if(columnIndex == -1 || columnIndex == -2) continue;

                String columnName = entry.getCustom();

                switch (mappings.get(columnIndex)) {
                    case "place":
                        appointment.setPlace(new Place(record.get(columnIndex)));
                        //schedule.getPlaces().addAll()
                        break;
                    case "startDate":
                            LocalDateTime startDate = LocalDateTime.parse(record.get(columnIndex), fmt);
                            appointment.setStartDate(LocalDate.from(startDate));
                            appointment.setEndDate(LocalDate.from(startDate));
                            try {
                                if(appointment.getStartTime()==null)
                                    appointment.setStartTime(LocalTime.from(startDate));
                            }catch (DateTimeParseException e){
                                continue;
                            }
                        break;
                    case "endDate":
                        LocalDateTime endDate = LocalDateTime.parse(record.get(columnIndex),fmt);
                        appointment.setEndDate(LocalDate.from(endDate));
                        try {
                            appointment.setEndTime(LocalTime.from(endDate));
                        }catch (DateTimeParseException e){
                            continue;
                        }
                        break;
                    case "startTime":
                        try {
                            if (record.get(columnIndex).contains("-")) {
                                String[] vremena = record.get(columnIndex).split("-");
                                appointment.setStartTime(LocalTime.parse(vremena[0], fmt1));
                                String novi = (vremena[1].concat(":00"));
                                appointment.setEndTime(LocalTime.parse(novi, fmt1));

                            } else {
                                LocalTime startTime = LocalTime.parse(record.get(columnIndex));
                                appointment.setStartTime(startTime);

                            }
                        }catch (Exception e){
                            continue;
                        }
                        break;
                    case "endTime":
                        LocalTime endTime = LocalTime.parse(record.get(columnIndex));
                        appointment.setEndTime(endTime);
                        break;
                    case "additional":
                        appointment.getAdditional().put(columnName, record.get(columnIndex));
                        break;
                    case "dayOfTheWeek":
                        String tab = record.get(columnIndex);
                        tab = tab.replaceAll("[ \\t\\n\\x0B\\f\\r\\u00A0\\u2028\\u2029]+","");
                        switch (days.get(tab)){
                            case "MONDAY":
                                appointment.setDayOfWeek(DayOfWeek.MONDAY);
                                break;
                            case "TUESDAY":
                                appointment.setDayOfWeek(DayOfWeek.TUESDAY);
                                break;
                            case "WEDNESDAY" :
                                appointment.setDayOfWeek(DayOfWeek.WEDNESDAY);
                                break;
                            case "THURSDAY":
                                appointment.setDayOfWeek(DayOfWeek.THURSDAY);
                                break;
                            case "FRIDAY":
                                appointment.setDayOfWeek(DayOfWeek.FRIDAY);
                                break;

                        }
                        break;
                    case "name":
                        appointment.setName(record.get(columnIndex));
                }
            }
            if(recurring){
                List<TimeSlot> recurringSlots = generateTimeSlots(appointment.getName(), appointment.getDayOfWeek(),schedule.getStartScheduleDate(),schedule.getEndScheduleDate(),appointment.getStartTime(),appointment.getEndTime(),appointment.getPlace(),appointment.getAdditional());
                for(TimeSlot timeSlot: recurringSlots){
                    try {
                        schedule.addTimeSlot(timeSlot);
                    }catch (Exception e){
                        System.out.println("Overlaping time slot");
                    }
                }
                System.out.println(schedule.getAppointments());
            }else {

                System.out.println(appointment);
                try {
                    schedule.addTimeSlot(appointment);
                }catch (Exception e){
                    System.out.println("Overlaping time slot");
                }

            }

        }
    }


    private static List<ConfigMapping>  readConfig(String filePath) throws FileNotFoundException {
        List<ConfigMapping> mappings = new ArrayList<>();

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);


        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ", 3);

            mappings.add(new ConfigMapping(Integer.valueOf(splitLine[0]), splitLine[1], splitLine[2]));
        }

        scanner.close();

        return mappings;
    }

    private static List<DayMapping>  readConfigDays(String filePathDays) throws FileNotFoundException {

        List<DayMapping> dayMappings = new ArrayList<>();

        File file1 = new File(filePathDays);
        Scanner scanner1 = new Scanner(file1);


        while(scanner1.hasNextLine()){
            String line1 = scanner1.nextLine();
            String[] split1 = line1.split(" ",2);
            dayMappings.add(new DayMapping(split1[0],split1[1]));
        }
        scanner1.close();

        return dayMappings;
    }





    private void writeData(String path) throws IOException {
        // Create a FileWriter and CSVPrinter
        FileWriter fileWriter = new FileWriter(path);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

        for (TimeSlot appointment : schedule.getAppointments() ) {
            csvPrinter.printRecord(
                    appointment.getName(),
                    appointment.getStartDate(),
                    appointment.getEndDate(),
                    appointment.getStartTime(),
                    appointment.getEndTime(),
                    appointment.getDayOfWeek(),
                    appointment.getPlace().getName()

            );
        }

        csvPrinter.close();
        fileWriter.close();
    }


    private List<TimeSlot> generateTimeSlots (String name, DayOfWeek dayOfTheWeek, LocalDate startPeriod, LocalDate
            endPeriod, LocalTime startTime, LocalTime endTime, Place place, Map < String, String > additional){
        // Find first date that matches the day
        LocalDate startDateTemp = findFirstDateForDay(startPeriod, dayOfTheWeek);
        // Create one time slot weekly until the end date
        List<TimeSlot> recurringTimeSlots = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        while (startDateTemp.isBefore(endPeriod)) {
            // Add time slot to the list
            TimeSlot timeSlot = new TimeSlot(
                    uuid,
                    name,
                    dayOfTheWeek,
                    startTime,
                    endTime,
                    startDateTemp,
                    startDateTemp,
                    place
            );

            timeSlot.setAdditional(additional);
            recurringTimeSlots.add(timeSlot);
            startDateTemp = startDateTemp.plusDays(7);
        }

        return recurringTimeSlots;
    }

    private LocalDate findFirstDateForDay(LocalDate date, DayOfWeek day){
        LocalDate startDateTemp = date;
        while (startDateTemp.getDayOfWeek() != day) {
            startDateTemp = startDateTemp.plusDays(1);
        }
        return startDateTemp;
    }

}
