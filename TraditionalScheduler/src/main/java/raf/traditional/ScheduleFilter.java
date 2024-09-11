package raf.traditional;


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
    public ScheduleFilterSpecification showUnavailable() {
        return super.showUnavailable();
    }

    @Override
    public ScheduleFilterSpecification setDate(LocalDate date) {
        super.setDate(date);
        filteringRunnablesTimeSlot.add(setDateRunnable);
        return this;
    }

    protected final Runnable setDateRunnable = () ->{
        filteredTimeSlots.removeIf(timeSlot -> {
            return !startDate.equals(timeSlot.getStartDate());
        });
    };

    @Override
    public ScheduleFilterSpecification setPeriod(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek) {
        super.setPeriod(startDate, endDate, dayOfWeek);
        filteringRunnablesTimeSlot.add(setPeriodRunnable);
        return this;
    }

    protected final Runnable setPeriodRunnable = () -> {
        filteredTimeSlots.removeIf(timeSlot -> {
            if(timeSlot.getStartDate().equals(startDate)) return false;
            if(timeSlot.getStartDate().equals(endDate)) return false;
            if(isOverlappingDate(timeSlot.getStartDate(), timeSlot.getStartDate(), startDate, endDate))
                return false;
            return true;
        });
    };

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
}
