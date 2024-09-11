package org.specification.specification.interfaces;

import org.specification.classes.Place;
import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleFilterSpecification {
    List<Place> filterPlaces(ScheduleSpecificationWrapper scheduleSpecificationWrapper);

    ScheduleFilterSpecification showAvailable(LocalDate startDate, LocalDate endPeriod);

    ScheduleFilterSpecification showUnavailable();

    void reset();

    ScheduleFilterSpecification setDate(LocalDate date);

    ScheduleFilterSpecification setPeriod(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek);

    ScheduleFilterSpecification setTime(LocalTime startTime, LocalTime endTime);

    ScheduleFilterSpecification setTime(LocalTime startTime, int minutes);

    ScheduleFilterSpecification setPlace(Place place);

    ScheduleFilterSpecification setPlaceMinCapacity(int minCapacity);

    ScheduleFilterSpecification addPlaceAdditional(String key, String value);

    ScheduleFilterSpecification addPlaceHasAdditional(String additionalKey);

    ScheduleFilterSpecification addTimeSlotAdditional(String key, String value);

    ScheduleFilterSpecification addTimeSlotHasAdditional(String additionalKey);

    ScheduleFilterSpecification addCustomFilter(Runnable runnable);
}
