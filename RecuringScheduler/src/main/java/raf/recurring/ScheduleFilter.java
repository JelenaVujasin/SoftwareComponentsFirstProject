package raf.recurring;


import org.specification.classes.Place;
import org.specification.classes.TimeSlot;
import org.specification.specification.interfaces.ScheduleFilterSpecification;
import org.specification.specification.wrappers.ScheduleFilterSpecificationWrapper;
import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleFilter extends ScheduleFilterSpecificationWrapper {

    public ScheduleFilter() {
        super();
    }

    @Override
    public List<TimeSlot> filterTimeSlots(ScheduleSpecificationWrapper scheduleSpecificationWrapper) {
        return super.filterTimeSlots(scheduleSpecificationWrapper);
    }

    @Override
    public List<Place> filterPlaces(ScheduleSpecificationWrapper scheduleSpecificationWrapper) {
        return super.filterPlaces(scheduleSpecificationWrapper);
    }

    @Override
    public ScheduleFilterSpecification showAvailable(LocalDate startDate, LocalDate endPeriod) {
        return super.showAvailable(startDate, endPeriod);
    }


    @Override
    public ScheduleFilterSpecification showUnavailable() {
        return super.showUnavailable();
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public ScheduleFilterSpecification setDate(LocalDate date) {
        return super.setDate(date);
    }

    @Override
    public ScheduleFilterSpecification setPeriod(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek) {
        return super.setPeriod(startDate, endDate, dayOfWeek);
    }

    @Override
    public ScheduleFilterSpecification setTime(LocalTime startTime, LocalTime endTime) {
        return super.setTime(startTime, endTime);
    }

    @Override
    public ScheduleFilterSpecification setTime(LocalTime startTime, int minutes) {
        return super.setTime(startTime, minutes);
    }

    @Override
    public ScheduleFilterSpecification setPlace(Place place) {
        return super.setPlace(place);
    }

    @Override
    public ScheduleFilterSpecification setPlaceMinCapacity(int minCapacity) {
        return super.setPlaceMinCapacity(minCapacity);
    }

    @Override
    public ScheduleFilterSpecification addPlaceAdditional(String key, String value) {
        return super.addPlaceAdditional(key, value);
    }

    @Override
    public ScheduleFilterSpecification addPlaceHasAdditional(String additionalKey) {
        return super.addPlaceHasAdditional(additionalKey);
    }

    @Override
    public ScheduleFilterSpecification addTimeSlotAdditional(String key, String value) {
        return super.addTimeSlotAdditional(key, value);
    }

    @Override
    public ScheduleFilterSpecification addTimeSlotHasAdditional(String additionalKey) {
        return super.addTimeSlotHasAdditional(additionalKey);
    }

    @Override
    public ScheduleFilterSpecification addCustomFilter(Runnable runnable) {
        return super.addCustomFilter(runnable);
    }

    @Override
    public List<TimeSlot> findAvailableTimeSlots(LocalDate startDate, LocalDate endDate) {
        return super.findAvailableTimeSlots(startDate, endDate);
    }
}
