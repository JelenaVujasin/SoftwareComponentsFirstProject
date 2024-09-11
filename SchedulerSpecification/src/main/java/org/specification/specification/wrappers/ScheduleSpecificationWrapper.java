package org.specification.specification.wrappers;

import lombok.Getter;
import lombok.Setter;
import org.specification.classes.FileFormat;
import org.specification.classes.Place;
import org.specification.classes.TimeSlot;
import org.specification.exceptions.PlaceAlreadyExistsException;
import org.specification.exceptions.TimeSlotOccupiedException;
import org.specification.serialization.SaveSchedulePDF;
import org.specification.specification.interfaces.ScheduleSpecification;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Getter
@Setter
public abstract class ScheduleSpecificationWrapper implements ScheduleSpecification {

    /**
     * List of all appointments in the schedule
     */
    private List<TimeSlot> appointments;
    /**
     * List of all excluded days
     */
    private List<LocalDate> excludedDays;
    /**
     * List of all places in the schedule
     */
    private List<Place> places;
    /**
     * Map of the additional data
     */
    private Map<String,String> helpAdd;
    /**
     * Starting and ending date of the schedule
     */
    private LocalDate startScheduleDate;
    /**
     * Starting and ending date of the schedule
     */
    private LocalDate endScheduleDate;

    /**
     * Initializes the schedule.
     */
    @Override
    public void initialize() {
        appointments = new ArrayList<>();
        excludedDays = new ArrayList<>();
        places = new ArrayList<>();
        helpAdd = new HashMap<>();
    }

    // Place operations

    /**
     * Adds a new place to the schedule
     * @param place Place that will be added to the places of the schedule
     * @throws PlaceAlreadyExistsException Exception thrown if the place already exists
     */
    @Override
    public void addPlace(Place place) throws PlaceAlreadyExistsException{
        for(Place p : places){
            if(p.equals(place)) {
                throw new PlaceAlreadyExistsException();
            }
        }
        places.add(place);
    }

    /**
     * Removes a place from the schedule
     * @param place Place to be removed
     */
    @Override
    public void deletePlace(Place place){
        places.remove(place);
    }

    /**
     * Removes a time slot from the schedule
     * @param timeSlot TimeSlot to be removed
     */
    @Override
    public void deleteTimeSlot(TimeSlot timeSlot){
        appointments.remove(timeSlot);
    }

    /**
     * Adds TimeSlot to the schedule
     * @param timeSlot TimeSlot object added to the schedule
     * @throws TimeSlotOccupiedException Exception thrown if the time slot is occupied
     */
    @Override
    public void addTimeSlot(TimeSlot timeSlot) throws TimeSlotOccupiedException{

    }

    /**
     * Creates a new TimeSlot for specific date and adds it to the schedule
     * @param date          Date of the appointment
     * @param startTime     Starting time of the appointment
     * @param endTime       Ending time of the appointment
     * @param place         Place of the appointment
     * @param additional    Map of the additional data
     * @throws TimeSlotOccupiedException Exception thrown if the time slot is occupied
     */
    @Override
    public void addTimeSlot(String name, LocalDate date, LocalTime startTime, LocalTime endTime, Place place, Map<String, String> additional) throws TimeSlotOccupiedException {}

    /**
     * Deletes a TimeSlot from the schedule
     * @param timeSlot TimeSlot to be deleted
     * @param all     Boolean value that determines if all TimeSlots with the same name should be deleted
     */
    @Override
    public void deleteTimeSlot(TimeSlot timeSlot, boolean all) {}

    /**
     * Adds a new recurring TimeSlot to the schedule
     * @param name Name of the TimeSlot
     * @param dayOfTheWeek Day of the week when the TimeSlot will be held
     * @param startPeriod  Starting date of the TimeSlot
     * @param endPeriod   Ending date of the TimeSlot
     * @param startTime Starting time of the TimeSlot
     * @param endTime Ending time of the TimeSlot
     * @param place Place of the TimeSlot
     * @param additional Map of the additional data
     * @throws TimeSlotOccupiedException Exception thrown if the time slot is occupied
     */
    @Override
    public void addRecuringTimeSlot(String name, DayOfWeek dayOfTheWeek, LocalDate startPeriod, LocalDate endPeriod, LocalTime startTime, LocalTime endTime, Place place, Map<String, String> additional) throws TimeSlotOccupiedException {}

    /**
     * Moves the TimeSlot to the new time
     * @param timeSlot TimeSlot to be moved
     * @param startTime     New starting time
     * @param endTime      New ending time
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalTime startTime, LocalTime endTime){}

    /**
     * Moves the TimeSlot to the new time
     * @param timeSlot TimeSlot to be moved
     * @param startTime    New starting time
     * @param endTime    New ending time
     * @param all    Boolean value that determines if all TimeSlots (if recurring) with the same name should be moved
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalTime startTime, LocalTime endTime, boolean all){}

    /**
     * Moves the TimeSlot to the new place
     * @param timeSlot TimeSlot to be moved
     * @param place New place
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, Place place){}

    /**
     * Moves the TimeSlot to the new place
     * @param timeSlot TimeSlot to be moved
     * @param place New place
     * @param all Boolean value that determines if all TimeSlots (if recurring) with the same name should be moved
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, Place place, boolean all){}

    /**
     * Moves the TimeSlot to the new date
     * @param timeSlot TimeSlot to be moved
     * @param date New date
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate date){}
    /**
     * Moves the TimeSlot to the new date
     * @param timeSlot TimeSlot to be moved
     * @param date New date
     * @param all Boolean value that determines if all TimeSlots (if recurring) with the same name should be moved
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate date, boolean all){}
    /**
     * Moves the TimeSlot to the new date
     * @param timeSlot TimeSlot to be moved
     * @param startDate New starting date
     * @param endDate New ending date
     * @param day Day of the week when the TimeSlot will be held
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate startDate, LocalDate endDate, DayOfWeek day){}
    /**
     * Moves the TimeSlot to the new date
     * @param timeSlot TimeSlot to be moved
     * @param startDate New starting date
     * @param endDate New ending date
     * @param day Day of the week when the TimeSlot will be held
     * @param all Boolean value that determines if all TimeSlots (if recurring) with the same name should be moved
     */
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate startDate, LocalDate endDate, DayOfWeek day, boolean all){}

    /**
     * Loads the schedule from the file. The file must be in the format specified in the config file.
     * @param path Path to the file
     * @param configPath Path to the config file
     * @param recurring Boolean value that determines if the schedule is recurring
     * @param daysPath Path to the file with excluded days
     * @throws IOException Exception thrown if the file is not found
     */
    @Override
    public void loadSchedule(String path, String configPath, boolean recurring, String daysPath) throws IOException{}

    /**
     * Saves the schedule to the file. The file will be in the format specified with the fileFormat parameter.
     * @param path Path to the file
     * @param fileFormat Format of the file
     * @throws IOException Exception thrown if saving to the file fails
     */
    @Override
    public void saveSchedule(String path, FileFormat fileFormat) throws IOException {
        if(fileFormat.equals(FileFormat.PDF)){
            SaveSchedulePDF.createTimeSlotTablePDF(appointments, path);
        }

    }

    /**
     * Adds a new excluded day
     * @param date Date to be excluded from the schedule
     */
    @Override
    public void addExcludedDay(LocalDate date){
        excludedDays.add(date);
    }

    /**
     * Checks if the time slot is available
     * @param timeSlot TimeSlot to be checked
     * @return Boolean value that determines if the time slot is available
     */
    @Override
    public boolean isTimeSlotAvailable(TimeSlot timeSlot){
      return false;
    };
}
