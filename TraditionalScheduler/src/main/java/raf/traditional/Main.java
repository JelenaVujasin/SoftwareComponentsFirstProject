package raf.traditional;

import org.specification.classes.FileFormat;
import org.specification.classes.Place;
import org.specification.classes.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        TraditionalScheduler schedule = new TraditionalScheduler();
        schedule.initialize();
        schedule.setStartScheduleDate(LocalDate.of(2023,10,1));
        schedule.setEndScheduleDate(LocalDate.of(2024,1,15));

        Map<String ,String > add = new HashMap<>();
        add.put("computer", "50");
        add.put("smartboard", "true");

        Place place1 = new Place();
        place1.setName("RAF1");
        place1.setCapacity(50);
        place1.setAdditional(add);
        boolean recurring;

        Place place2 = new Place();
        place2.setName("RAF2");
        place2.setCapacity(10);
        try {
//            schedule.addPlace(place1);
//            schedule.addTimeSlot("SK vezbe", LocalDate.now(), LocalTime.of(1, 0), LocalTime.of(1,5), place1, null);
//            schedule.addTimeSlot("SK vezbe", LocalDate.of(2023, 11, 5), LocalTime.of(0, 55), LocalTime.of(1, 5), place1, null);
//            schedule.addRecuringTimeSlot("KTG vezbe", DayOfWeek.MONDAY, LocalDate.now().minusDays(0), LocalDate.now().plusDays(30), LocalTime.NOON, LocalTime.MIDNIGHT, place2, null);
//            ScheduleFilter scheduleFilter = new ScheduleFilter();
//            //scheduleFilter.setDate(LocalDate.of(2023, 11, 5));
//            //System.out.println("ZA BRISANJE " + scheduleFilter.filter(schedule));
//            //schedule.deleteTimeSlot(scheduleFilter.filter(schedule).getFirst(), true);
//            TimeSlot timeSlot = new TimeSlot("KV",LocalTime.of(0,0),LocalTime.of(0,45),LocalDate.of(2023,11,5),LocalDate.of(2023,11,5),place1);
//            schedule.addTimeSlot(timeSlot);
//            //schedule.moveTimeSlot(timeSlot,LocalTime.of(1,0),LocalTime.of(1,45));
//            System.out.println("Da li je vas raspoed ponavljajuci?Ako jeste unestite DA, ako nije unesite NE");



            schedule.loadSchedule("C:\\Users\\JASIN\\OneDrive\\Desktop\\pretty_formatted_json.json","SchedulerSpecification/config.txt",true,"C:\\Users\\JASIN\\OneDrive\\Desktop\\configdays.txt");
            schedule.saveSchedule("C:\\Users\\JASIN\\OneDrive\\Desktop\\raspored.txt",FileFormat.JSON);
            //schedule.saveSchedule("C:\\Users\\JASIN\\OneDrive\\Desktop\\Proba.txt", FileFormat.CSV);
            //System.out.println("PRESEK");
//            for(TimeSlot ts : schedule.getAppointments()){
//                System.out.println(ts);
//            }

//            for(TimeSlot ts : scheduleFilter.filter(schedule)){
//                System.out.println(ts);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}