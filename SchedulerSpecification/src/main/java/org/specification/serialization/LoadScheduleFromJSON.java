package org.specification.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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
 * Class for loading and exporting schedule from and to JSON
 */
public class LoadScheduleFromJSON {
        private final ScheduleSpecificationWrapper schedule;


        public LoadScheduleFromJSON(ScheduleSpecificationWrapper schedule) {
                this.schedule = schedule;
        }

        public void loadDataJSON(String filePath, String configPath, boolean recurring, String configDays) throws FileNotFoundException {
            loadJson(filePath, configPath,recurring,configDays);
        }

        public void exportDataJSON(String path) throws IOException{
                writeData(path);
        }


        private void loadJson(String filePath,String configPath,boolean recurring,String configDays) throws FileNotFoundException {
                List<ConfigMapping> columnMappings = readConfig(configPath);
                List<DayMapping> dayMappings = readConfigDays(configDays);
                Map<String,String> days = new HashMap<>();
                Map<String, String> mappings = new HashMap<>();
                for(ConfigMapping configMapping : columnMappings) {
                        mappings.put(configMapping.getCustom(), configMapping.getOriginal());
                }

                for(DayMapping dayMapping : dayMappings) {
                        days.put(dayMapping.getOriginalDay(), dayMapping.getDayOfWeek());
                }
                FileReader fileReader = new FileReader(filePath);
                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(fileReader, JsonArray.class);

                DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                        .appendPattern(mappings.get("Datum"))
                        .optionalStart()
                        .appendPattern(mappings.get("Vreme"))
                        .optionalEnd()
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .toFormatter();

                DateTimeFormatter fmt1 = new DateTimeFormatterBuilder()
                        .appendPattern(mappings.get("Vreme"))
                        .optionalStart()
                        .appendPattern("HH:00")
                        .optionalEnd()
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .toFormatter();

                for (JsonElement element : jsonArray) {
                        TimeSlot appointment = new TimeSlot();
                       JsonObject jsonObject = element.getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                                String key = entry.getKey();
                                JsonElement value = entry.getValue();
                                switch (mappings.get(key)) {
                                        case "place":
                                                appointment.setPlace(new Place(value.getAsString()));
                                                //schedule.getPlaces().addAll()
                                                break;
                                        case "startDate":
                                                LocalDateTime startDate = LocalDateTime.parse(value.getAsString(), fmt);
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
                                                LocalDateTime endDate = LocalDateTime.parse(value.getAsString(),fmt);
                                                appointment.setEndDate(LocalDate.from(endDate));
                                                try {
                                                        appointment.setEndTime(LocalTime.from(endDate));
                                                }catch (DateTimeParseException e){
                                                        continue;
                                                }
                                                break;
                                        case "startTime":
                                                try {
                                                        if (value.getAsString().contains("-")) {
                                                                String[] vremena = value.getAsString().split("-");
                                                                appointment.setStartTime(LocalTime.parse(vremena[0], fmt1));
                                                                String novi = (vremena[1].concat(":00"));
                                                                appointment.setEndTime(LocalTime.parse(novi, fmt1));

                                                        } else {
                                                                LocalTime startTime = LocalTime.parse(value.getAsString());
                                                                appointment.setStartTime(startTime);

                                                        }
                                                }catch (Exception e){
                                                        continue;
                                                }
                                                break;
                                        case "endTime":
                                                LocalTime endTime = LocalTime.parse(value.getAsString());
                                                appointment.setEndTime(endTime);
                                                break;
                                        case "additional":
                                                appointment.getAdditional().put(value.getAsString(), value.getAsString());
                                                break;
                                        case "dayOfTheWeek":
                                                String tab = value.getAsString();
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
                                                appointment.setName(value.getAsString());
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

        private void writeData(String path) throws IOException {
                // Create a FileWriter and CSVPrinter
                FileWriter fileWriter = new FileWriter(path);
                Gson gson = new GsonBuilder()
                        .registerTypeAdapterFactory(new LocalTimeTypeAdapterFactory())
                        .registerTypeAdapterFactory(new LocalDateTypeAdapterFactory())
                        .setPrettyPrinting()
                        .create();

                for (TimeSlot appointment : schedule.getAppointments() ) {
                        String jsonData = gson.toJson(appointment);
                        fileWriter.write(jsonData);
                }

                fileWriter.close();
        }



        private static List<ConfigMapping> readConfig(String filePath) throws FileNotFoundException {
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


        private List<TimeSlot> generateTimeSlots (String name, DayOfWeek dayOfTheWeek, LocalDate startPeriod, LocalDate
                endPeriod, LocalTime startTime, LocalTime endTime, Place place, Map< String, String > additional){
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

        static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                        out.value(value.toString());
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                        return LocalDate.parse(in.nextString());
                }
        }

        // TypeAdapterFactory for LocalDateTypeAdapter
        static class LocalDateTypeAdapterFactory implements TypeAdapterFactory {
                @Override
                @SuppressWarnings("unchecked")
                public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                        return (type.getRawType() == LocalDate.class)
                                ? (TypeAdapter<T>) new LocalDateTypeAdapter()
                                : null;
                }
        }


        static class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {

                @Override
                public LocalTime read(JsonReader jsonReader) throws IOException {
                        return LocalTime.parse(jsonReader.nextString());
                }

                @Override
                public void write(JsonWriter jsonWriter, LocalTime localTime) throws IOException {
                        jsonWriter.value(localTime.toString());
                }
        }

        // TypeAdapterFactory for LocalTimeTypeAdapter
        static class LocalTimeTypeAdapterFactory implements TypeAdapterFactory {

                @Override
                public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                        return (typeToken.getRawType() == LocalTime.class)
                                ? (TypeAdapter<T>) new LocalTimeTypeAdapter()
                                : null;
                }
        }


}
