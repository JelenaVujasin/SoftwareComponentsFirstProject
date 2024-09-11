package org.specification.specification.interfaces;

import org.specification.classes.FileFormat;
import org.specification.classes.Place;
import org.specification.classes.TimeSlot;
import org.specification.exceptions.PlaceAlreadyExistsException;
import org.specification.exceptions.TimeSlotOccupiedException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public interface ScheduleSpecification {

    void initialize();

    void addPlace(Place place) throws PlaceAlreadyExistsException;

    void deletePlace(Place place);

    // Add time slot
    void deleteTimeSlot(TimeSlot timeSlot, boolean all);

    void deleteTimeSlot(TimeSlot timeSlot);

    void addTimeSlot(TimeSlot timeSlot) throws TimeSlotOccupiedException;

    void addTimeSlot(String name, LocalDate date, LocalTime startTime, LocalTime endTime, Place place, Map<String, String> additional) throws TimeSlotOccupiedException;

    void addRecuringTimeSlot(String name, DayOfWeek dayOfTheWeek, LocalDate startPeriod, LocalDate endPeriod, LocalTime startTime, LocalTime endTime, Place place, Map<String, String> additional) throws TimeSlotOccupiedException;

    void moveTimeSlot(TimeSlot timeSlot, LocalTime startTime, LocalTime endTime);

    void moveTimeSlot(TimeSlot timeSlot, LocalTime startTime, LocalTime endTime, boolean all);

    void moveTimeSlot(TimeSlot timeSlot, Place place);

    void moveTimeSlot(TimeSlot timeSlot, Place place, boolean all);

    void moveTimeSlot(TimeSlot timeSlot, LocalDate date);

    void moveTimeSlot(TimeSlot timeSlot, LocalDate date, boolean all);

    void moveTimeSlot(TimeSlot timeSlot, LocalDate startDate, LocalDate endDate, DayOfWeek day);

    void moveTimeSlot(TimeSlot timeSlot, LocalDate startDate, LocalDate endDate, DayOfWeek day, boolean all);

    void loadSchedule(String path, String configPath, boolean recurring, String daysPath) throws IOException;

    void saveSchedule(String path, FileFormat fileFormat) throws IOException;

    void addExcludedDay(LocalDate date);

    boolean isTimeSlotAvailable(TimeSlot timeSlot);
}
