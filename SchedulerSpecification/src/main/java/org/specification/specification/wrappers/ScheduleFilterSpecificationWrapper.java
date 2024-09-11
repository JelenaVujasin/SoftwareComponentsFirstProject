package org.specification.specification.wrappers;

import org.specification.classes.Place;
import org.specification.classes.TimeSlot;
import org.specification.specification.interfaces.ScheduleFilterSpecification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
/**
 * ScheduleFilterBuilder class is used to build a filter for the schedule.
 * It defaults to showing all taken time slots in the schedule.
 * If the same method is called twice with different parameters during the building, the latter call will override the first one.
 */
public abstract class ScheduleFilterSpecificationWrapper implements ScheduleFilterSpecification {
    /**
     * Schedule object to filter
     */
    protected ScheduleSpecificationWrapper schedule;
    /**
     * Set of filtering runnables for time slots
     */
    protected Set<Runnable> filteringRunnablesTimeSlot;
    /**
     * Set of filtering runnables for places
     */
    protected Set<Runnable> filteringRunnablesPlaces;
    /**
     * List of filtered time slots
     */
    protected List<TimeSlot> filteredTimeSlots;
    /**
     * List of filtered places
     */
    protected List<Place> filteredPlaces;
    /**
     * Boolean value that determines if the filter should show available or unavailable time slots
     */
    protected boolean showAvailable = false;
    /**
     * Filtering parameter
     */
    protected LocalDate startDate = null;
    /**
     * Filtering parameter
     */
    protected LocalDate endDate = null;
    /**
     * Filtering parameter
     */
    protected DayOfWeek dayOfWeek = null;
    /**
     * Filtering parameter
     */
    protected LocalTime startTime = null;
    /**
     * Filtering parameter
     */
    protected LocalTime endTime = null;
    /**
     * Filtering parameter
     */
    protected Place place = null;
    /**
     * Filtering parameter
     */
    protected int minCapacity = -1;
    /**
     * Filtering parameter
     */
    protected List<String> placeAdditionalKeys;
    /**
     * Filtering parameter
     */
    protected Map<String, String> placeAdditionals;
    /**
     * Filtering parameter
     */
    protected List<String> timeSlotAdditionalKeys;
    /**
     * Filtering parameter
     */
    protected Map<String, String> timeSlotAdditionals;
    /**
     * Starting date for the available time slots showing
     */
    protected LocalDate startDateAvailable;
    /**
     * Ending date for the available time slots showing
     */
    protected LocalDate endDateAvailable;

    public ScheduleFilterSpecificationWrapper(){
        placeAdditionalKeys = new ArrayList<>();
        placeAdditionals = new HashMap<>();
        timeSlotAdditionalKeys = new ArrayList<>();
        timeSlotAdditionals = new HashMap<>();
        filteringRunnablesTimeSlot = new HashSet<>();
        filteringRunnablesPlaces = new HashSet<>();
        filteredTimeSlots = new ArrayList<>();
        filteredPlaces = new ArrayList<>();
    }

    /**
     * Starts the filtering of the provided schedule and returns the list of filtered TimeSlots
     * @param scheduleSpecificationWrapper Schedule object to filter
     * @return TimeSlots from the filtered schedule
     */
    public List<TimeSlot> filterTimeSlots(ScheduleSpecificationWrapper scheduleSpecificationWrapper){
        this.schedule = scheduleSpecificationWrapper;
        this.filteredTimeSlots.addAll(scheduleSpecificationWrapper.getAppointments());
        this.filteringRunnablesTimeSlot.forEach(Runnable::run);
        if(showAvailable){
            return findAvailableTimeSlots(startDateAvailable, endDateAvailable);
        }else{
            return filteredTimeSlots;
        }
    }

    /**
     * Start the filtering of the places in the provided schedule.
     * @param scheduleSpecificationWrapper Schedule object to filter the places from
     * @return Places from the filtered schedule
     */
    @Override
    public List<Place> filterPlaces(ScheduleSpecificationWrapper scheduleSpecificationWrapper){
        this.schedule = scheduleSpecificationWrapper;
        this.filteredPlaces.addAll(scheduleSpecificationWrapper.getPlaces());
        this.filteringRunnablesPlaces.forEach(Runnable::run);
        return filteredPlaces;
    }

    /**
     * Shows all the available time slots in the schedule
     */
    @Override
    public ScheduleFilterSpecification showAvailable(LocalDate startDate, LocalDate endPeriod){
        this.startDateAvailable = startDate;
        this.endDateAvailable = endPeriod;
        this.showAvailable = true;
        return this;
    }

    /**
     * Shows all the unavailable time slots in the schedule
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification showUnavailable(){
        this.showAvailable = false;
        return this;
    }

    /**
     * Resets the filter - removes all filtering conditions
     */
    @Override
    public void reset(){
        filteringRunnablesPlaces.clear();
        filteringRunnablesTimeSlot.clear();
    }

    /**
     * Filter the schedule for timeSlots on specific date
     * @param date date for schedule filtering
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification setDate (LocalDate date){
        startDate = date;
        endDate = date;
        dayOfWeek = date.getDayOfWeek();
        return this;
    }

    /**
     * Filter the schedule for timeSlots in specified period and day of the week
     * @param startDate  starting date of the filtering period
     * @param endDate    ending date of the filtering period
     * @param dayOfWeek  day of the week
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification setPeriod(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek){
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    /**
     * Filter the schedule for the time slots that are available for the specified period of the time
     * @param startTime starting time of the time period
     * @param endTime   ending time of the time period
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification setTime(LocalTime startTime, LocalTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        filteringRunnablesTimeSlot.add(setTimeRunnableTimeSlot);
        filteringRunnablesPlaces.add(setTimeRunnablePlace);
        return this;
    }

    /**
     * Filter the schedule for the time slots that are available for the specified period of the time
     * @param startTime starting time of the time period
     * @param minutes   duration in minutes from the starting time
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification setTime(LocalTime startTime, int minutes){
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(minutes);
        filteringRunnablesTimeSlot.add(setTimeRunnableTimeSlot);
        filteringRunnablesPlaces.add(setTimeRunnablePlace);
        return this;
    }

    protected final Runnable setTimeRunnableTimeSlot = () -> {
        filteredTimeSlots.removeIf( timeSlot -> {
           return (timeSlot.getEndTime().isBefore(this.startTime) || timeSlot.getStartTime().isAfter(this.endTime));
        });
    };

    protected final Runnable setTimeRunnablePlace = () -> {
        filteredPlaces.removeIf(place -> {
            for(TimeSlot timeSlot : schedule.getAppointments()){
                if(timeSlot.getPlace().equals(place) && isOverlappingTime(timeSlot.getStartTime(), timeSlot.getEndTime(), startTime, endTime))
                    return false;
            }
            return true;
        });
    };

    /**
     * Filter time slots by Place
     * @param place place object used for filtering
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification setPlace(Place place){
        this.place = place;
        filteringRunnablesTimeSlot.add(setPlaceRunnableTimeSlot);
        filteringRunnablesPlaces.add(setPlaceRunnablePlace);
        return this;
    }

    protected final Runnable setPlaceRunnableTimeSlot = () -> {
        filteredTimeSlots.removeIf(timeSlot -> !timeSlot.getPlace().equals(this.place));
    };

    protected final Runnable setPlaceRunnablePlace = () -> {
        filteredPlaces.removeIf(place1 -> !this.place.equals(place1));
    };

    /**
     * Returns the time slots with same or greater capacity
     * @param minCapacity minimum capacity
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification setPlaceMinCapacity(int minCapacity){
        this.minCapacity = minCapacity;
        filteringRunnablesTimeSlot.add(setPlaceMinCapacityRunnableTimeSlot);
        filteringRunnablesPlaces.add(setPlaceMinCapacityRunnablePlace);
        return this;
    }

    protected Runnable setPlaceMinCapacityRunnableTimeSlot = () -> {
        filteredTimeSlots.removeIf(timeSlot -> timeSlot.getPlace().getCapacity() < this.minCapacity);
    };

    protected Runnable setPlaceMinCapacityRunnablePlace = () -> {
        filteredPlaces.removeIf(place -> place.getCapacity() < this.minCapacity);
    };

    /**
     * Return time slots where their place's have a specified key-value pair. It will only return the
     * time slots that have place with all the specified entries
     * @param key key part of the key-value
     * @param value value part of the key-value
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification addPlaceAdditional(String key, String value){
        this.placeAdditionals.put(key, value);
        filteringRunnablesTimeSlot.add(addPlaceAdditionalRunnableTimeSlot);
        filteringRunnablesPlaces.add(addPlaceAdditionalRunnablePlace);
        return this;
    }

    protected Runnable addPlaceAdditionalRunnableTimeSlot = () -> {
        filteredTimeSlots.removeIf(timeSlot -> !timeSlot.getPlace().getAdditional().entrySet().containsAll(this.placeAdditionals.entrySet()));
    };

    protected Runnable addPlaceAdditionalRunnablePlace = () -> {
        filteredPlaces.removeIf(place -> !place.getAdditional().entrySet().containsAll(this.placeAdditionals.entrySet()));
    };

    /**
     * Return time slots where their place has a specified additional
     * @param additionalKey specified additional
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification addPlaceHasAdditional(String additionalKey){
        this.placeAdditionalKeys.add(additionalKey);
        filteringRunnablesTimeSlot.add(addPlaceHasAdditionalRunnableTimeSlot);
        filteringRunnablesPlaces.add(addPlaceAdditionalRunnablePlace);

        return this;
    }

    protected Runnable addPlaceHasAdditionalRunnableTimeSlot = () ->{
        filteredTimeSlots.removeIf(timeSlot -> {
            for(String s : this.placeAdditionalKeys){
                if(!timeSlot.getPlace().getAdditional().containsKey(s))
                    return true;
            }
            return false;
        });
    };

    protected Runnable addPlaceHasAdditionalRunnablePlace = () ->{
        filteredPlaces.removeIf(place1 -> {
            for(String s : this.placeAdditionalKeys){
                if(!place1.getAdditional().containsKey(s))
                    return true;
            }
            return false;
        });
    };

    /**
     * Returns time slots which have all the added filtering additionals
     * @param key key part of the additional
     * @param value value part of the additional
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification addTimeSlotAdditional(String key, String value){
        this.timeSlotAdditionals.put(key, value);
        filteringRunnablesTimeSlot.add(addTimeSlotAdditionalRunnableTimeSlot);

        return this;
    }

    protected Runnable addTimeSlotAdditionalRunnableTimeSlot = () -> {
        filteredTimeSlots.removeIf(timeSlot -> !timeSlot.getAdditional().entrySet().containsAll(this.timeSlotAdditionals.entrySet()));
    };

    /**
     * Return time slots that have specific additional
     * @param additionalKey specified additional
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification addTimeSlotHasAdditional(String additionalKey){
        this.timeSlotAdditionalKeys.add(additionalKey);
        filteringRunnablesTimeSlot.add(addTimeHasAdditionalRunnable);

        return this;
    }

    protected Runnable addTimeHasAdditionalRunnable = () ->{
        filteredTimeSlots.removeIf(timeSlot -> {
            for(String s : this.timeSlotAdditionalKeys){
                if(!timeSlot.getAdditional().containsKey(s))
                    return true;
            }
            return false;
        });
    };

    /**
     * Add a custom filtering runnable to the ScheduleFilter.
     * @param runnable runnable to add for custom filtering
     * @return ScheduleFilterSpecification object
     */
    @Override
    public ScheduleFilterSpecification addCustomFilter(Runnable runnable){
        filteringRunnablesTimeSlot.add(runnable);
        return this;
    }

    protected boolean isOverlappingDate(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        // Check for overlap
        return !startDate1.isAfter(endDate2) && !startDate2.isAfter(endDate1);
    }

    protected boolean isOverlappingTime(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        // Return true if startTime1 is before endTime2 and startTime2 is before endTime1
        return startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1);
    }

    protected LocalDate findFirstDateForDay(LocalDate date, DayOfWeek day){
        LocalDate startDateTemp = date;
        while (startDateTemp.getDayOfWeek() != day) {
            startDateTemp = startDateTemp.plusDays(1);
        }
        return startDateTemp;
    }

    public List<TimeSlot> findAvailableTimeSlots(LocalDate startDate, LocalDate endDate) {
        List<TimeSlot> availableSlots = new ArrayList<>();

        for (Place place : schedule.getPlaces()) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // Declare final variables for use in the lambda expression
                final LocalDate finalDate = date;
                final Place finalPlace = place;

                List<TimeSlot> dailySlots = schedule.getAppointments().stream()
                        .filter(slot -> slot.getStartDate().equals(finalDate) && slot.getPlace().equals(finalPlace))
                        .sorted(Comparator.comparing(TimeSlot::getStartTime))
                        .toList();

                LocalTime currentTime = LocalTime.MIDNIGHT;


                for (TimeSlot slot : dailySlots) {
                    if (currentTime.isBefore(slot.getStartTime())) {
                        TimeSlot availableSlot = new TimeSlot();
                        availableSlot.setStartDate(date);
                        availableSlot.setStartTime(currentTime);
                        availableSlot.setEndTime(slot.getStartTime());
                        availableSlot.setPlace(place);
                        availableSlots.add(availableSlot);
                    }
                    currentTime = slot.getEndTime();
                }

                if (currentTime.isBefore(LocalTime.MAX)) {
                    TimeSlot availableSlot = new TimeSlot();
                    availableSlot.setStartDate(date);
                    availableSlot.setStartTime(currentTime);
                    availableSlot.setEndTime(LocalTime.MAX);
                    availableSlot.setPlace(place);
                    availableSlots.add(availableSlot);
                }
            }
        }

        return availableSlots;
    }

}
