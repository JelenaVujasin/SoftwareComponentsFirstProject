package raf.traditional;

import org.specification.classes.FileFormat;
import org.specification.classes.Place;
import org.specification.classes.TimeSlot;
import org.specification.exceptions.PlaceAlreadyExistsException;
import org.specification.exceptions.TimeSlotOccupiedException;
import org.specification.serialization.LoadScheduleFromJSON;
import org.specification.serialization.SaveLoadScheduleFromCSV;
import org.specification.serialization.SaveSchedulePDF;
import org.specification.specification.ScheduleManager;
import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TraditionalScheduler extends ScheduleSpecificationWrapper {

    static {
        ScheduleManager.setScheduler(new TraditionalScheduler());
        ScheduleManager.setFilter(new ScheduleFilter());
    }

    public TraditionalScheduler() {
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void addPlace(Place place) throws PlaceAlreadyExistsException {
        super.addPlace(place);
    }

    @Override
    public void deletePlace(Place place) {
        super.deletePlace(place);
    }

    @Override
    public void deleteTimeSlot(TimeSlot timeSlot, boolean all) {
        if(timeSlot.getGroupId() == null) {
            getAppointments().remove(timeSlot);
            return;
        }
        if(all){
            getAppointments().removeIf(timeSlot1 -> {
                if(timeSlot1.getGroupId() == null)return false;
                if(timeSlot1.getGroupId().equals(timeSlot.getGroupId()))return true;
                return false;
            });
        }else{
            getAppointments().remove(timeSlot);
        }
    }

    @Override
    public void addTimeSlot(TimeSlot timeSlot) throws TimeSlotOccupiedException {
        if(!this.getPlaces().contains(timeSlot.getPlace())){
            throw new IllegalArgumentException("Place doesn't exist in the schedule");
        }
        if(this.getExcludedDays().contains(timeSlot.getStartDate())){
            throw new IllegalArgumentException("Date is excluded");
        }
        if(!checkIfOverlap(timeSlot)){
            getAppointments().add(timeSlot);
        }else{
            throw new TimeSlotOccupiedException();
        }
    }

    @Override
    public void addTimeSlot(String name,LocalDate date, LocalTime startTime, LocalTime endTime, Place place, Map<String, String> additional) throws TimeSlotOccupiedException {
        if(!this.getPlaces().contains(place)){
            throw new IllegalArgumentException("Place doesn't exist in the schedule");
        }
        if(date.isBefore(this.getStartScheduleDate()) || date.isAfter(this.getEndScheduleDate())){
            throw new IllegalArgumentException("Date is not in the schedule period");
        }
        if(this.getExcludedDays().contains(date)){
            throw new IllegalArgumentException("Date is excluded");
        }
        TimeSlot ts = new TimeSlot(UUID.randomUUID(), name,date.getDayOfWeek(), startTime, endTime, date, date, place);
        if(!checkIfOverlap(ts)){
            getAppointments().add(ts);
        }else{
            throw new TimeSlotOccupiedException();
        }
    }

    @Override
    public void addRecuringTimeSlot(String name,DayOfWeek dayOfTheWeek, LocalDate startPeriod, LocalDate endPeriod, LocalTime startTime, LocalTime endTime, Place place, Map<String, String> additional) throws TimeSlotOccupiedException {
        if(!getPlaces().contains(place)){
            throw new IllegalArgumentException("Place doesn't exist in the schedule");
        }
        List<TimeSlot> timeSlotList = generateTimeSlots(name, dayOfTheWeek, startPeriod, endPeriod, startTime, endTime, place, additional);
        for(TimeSlot timeSlot : timeSlotList){
            if(checkIfOverlap(timeSlot) || this.getExcludedDays().contains(timeSlot.getStartDate()))
                throw new TimeSlotOccupiedException();
        }
        getAppointments().addAll(timeSlotList);
    }

    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalTime startTime, LocalTime endTime){
        // Method to call the overloaded method
        moveTimeSlot(timeSlot, startTime, endTime, false);
    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalTime startTime, LocalTime endTime, boolean all){
        checkIfCScheduleContainsTimeslot(timeSlot);
        if(!all){
            setNewTime(timeSlot,startTime,endTime,null);
        }else{
            List<TimeSlot> recurringTimeSlots = generateTimeSlots(timeSlot.getName(),timeSlot.getDayOfWeek(),timeSlot.getStartDate(),timeSlot.getEndDate(),timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getPlace(),timeSlot.getAdditional());
            for(TimeSlot ts:recurringTimeSlots){
                setNewTime(ts,startTime,endTime,timeSlot.getGroupId());
            }
        }

    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, Place place){
        // Method to call the overloaded method
        moveTimeSlot(timeSlot, place, false);
    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, Place place, boolean all){
        checkIfCScheduleContainsTimeslot(timeSlot);
        if(!all){
            setNewPlace(timeSlot,place,null);
        }else{
            List<TimeSlot> recurringTimeSlots = generateTimeSlots(timeSlot.getName(),timeSlot.getDayOfWeek(),timeSlot.getStartDate(),timeSlot.getEndDate(),timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getPlace(),timeSlot.getAdditional());
            for(TimeSlot ts:recurringTimeSlots){
                setNewPlace(ts,place,ts.getGroupId());
            }
        }

    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate date){
        // Method to call the overloaded method
        moveTimeSlot(timeSlot, date, false);
    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate date, boolean all){
        checkIfCScheduleContainsTimeslot(timeSlot);

        if(timeSlot.isRecurring() && all)
            throw new IllegalArgumentException("Can't move recurring TimeSlot to specific date");
        setNewDate(timeSlot,date,null);


    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate startDate, LocalDate endDate, DayOfWeek day){
        // Method to call the overloaded method
        moveTimeSlot(timeSlot, startDate, endDate, day, false);
    }
    @Override
    public void moveTimeSlot(TimeSlot timeSlot, LocalDate startDate, LocalDate endDate, DayOfWeek day, boolean all){
        checkIfCScheduleContainsTimeslot(timeSlot);

        if(!timeSlot.isRecurring())
            throw new IllegalArgumentException("Can't move non-recurring TimeSlot to date period");
        if(!all)
            throw new RuntimeException("Can't move one recurring date for period");

        setNewRecurringDate(timeSlot,startDate,endDate,day,timeSlot.getGroupId());
    }

    @Override
    public void loadSchedule(String path,String configPath,boolean recurring,String configDays) throws IOException {
        SaveLoadScheduleFromCSV load = new SaveLoadScheduleFromCSV(this);
        load.loadData(path,configPath,recurring,configDays);
    }

    @Override
    public void saveSchedule(String path, FileFormat fileFormat) throws IOException {
        if(fileFormat.equals(FileFormat.CSV)){
            SaveLoadScheduleFromCSV save = new SaveLoadScheduleFromCSV(this);
            save.exportData(path);
        }else if(fileFormat.equals(FileFormat.JSON)){
            LoadScheduleFromJSON save = new LoadScheduleFromJSON(this);
            save.exportDataJSON(path);
        }
        if(fileFormat.equals(FileFormat.PDF)){
            SaveSchedulePDF.createTimeSlotTablePDF(getAppointments(), path);
        }
    }

    @Override
    public boolean isTimeSlotAvailable(TimeSlot timeSlot){
        return !checkIfOverlap(timeSlot);
    }

    private void checkIfCScheduleContainsTimeslot(TimeSlot timeSlot){
        if(!getAppointments().contains(timeSlot))
            throw new IllegalArgumentException("Provided timeSlot doesn't exist in the schedule");
    }

    private LocalDate findFirstDateForDay(LocalDate date, DayOfWeek day){
        LocalDate startDateTemp = date;
        while (startDateTemp.getDayOfWeek() != day) {
            startDateTemp = startDateTemp.plusDays(1);
        }
        return startDateTemp;
    }

    private boolean checkIfOverlap(TimeSlot timeSlot){

        for(TimeSlot ts: getAppointments()){
            if(!ts.getStartDate().equals(timeSlot.getStartDate()) || !ts.getEndDate().equals(timeSlot.getEndDate()))continue;
            if(!ts.getPlace().equals(timeSlot.getPlace()))continue;
            if(!timeSlot.getEndTime().isBefore(ts.getStartTime()) && !timeSlot.getStartTime().isAfter(ts.getEndTime())){

                return true;
            }
        }
        return false;
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

    private void setNewDate(TimeSlot timeSlot, LocalDate date,UUID groupID){
        getAppointments().remove(timeSlot);
        TimeSlot ts = new TimeSlot(groupID,timeSlot.getName(), date.getDayOfWeek(),timeSlot.getStartTime(),timeSlot.getEndTime(),date,date,timeSlot.getPlace());
        if(!checkIfOverlap(ts)){
            timeSlot.setStartDate(date);
            timeSlot.setEndDate(date);
            timeSlot.setDayOfWeek(date.getDayOfWeek());
            timeSlot.setGroupId(ts.getGroupId());
        }else{
            getAppointments().add(timeSlot);
            throw new TimeSlotOccupiedException();
        }
    }

    private void setNewRecurringDate(TimeSlot timeSlot, LocalDate startDate,LocalDate endDate,DayOfWeek dayOfWeek,UUID groupID){
        getAppointments().remove(timeSlot);
        TimeSlot ts = new TimeSlot(groupID,timeSlot.getName(), dayOfWeek,timeSlot.getStartTime(),timeSlot.getEndTime(),startDate,endDate,timeSlot.getPlace());
        if(!checkIfOverlap(ts)){
            timeSlot.setStartDate(startDate);
            timeSlot.setEndDate(endDate);
            timeSlot.setDayOfWeek(dayOfWeek);
            timeSlot.setGroupId(ts.getGroupId());
        }else{
            getAppointments().add(timeSlot);
            throw new TimeSlotOccupiedException();
        }
    }



    private void setNewTime(TimeSlot timeSlot,LocalTime startTime, LocalTime endTime,UUID groupID){
        getAppointments().remove(timeSlot);
        TimeSlot ts = new TimeSlot(groupID,timeSlot.getName(), timeSlot.getDayOfWeek(),startTime,endTime,timeSlot.getStartDate(),timeSlot.getEndDate(),timeSlot.getPlace());
        if(!checkIfOverlap(ts)){
            timeSlot.setStartTime(startTime);
            timeSlot.setEndTime(endTime);
            timeSlot.setGroupId(ts.getGroupId());
        }else{
            getAppointments().add(timeSlot);
            throw new TimeSlotOccupiedException();
        }
    }

    private void setNewPlace(TimeSlot timeSlot,Place place,UUID groupID){
        getAppointments().remove(timeSlot);
        TimeSlot ts = new TimeSlot(groupID,timeSlot.getName(), timeSlot.getDayOfWeek(),timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getStartDate(),timeSlot.getEndDate(),place);
        if(!checkIfOverlap(ts)){
            timeSlot.setPlace(place);
        }else{
            getAppointments().add(timeSlot);
            throw new TimeSlotOccupiedException();
        }
    }
}
