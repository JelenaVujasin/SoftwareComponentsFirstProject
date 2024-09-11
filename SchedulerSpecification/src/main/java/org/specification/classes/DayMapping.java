package org.specification.classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DayMapping {
    /**
     * Name of the day in the CSV file
     */
    private String originalDay;
    /**
     * Real name of the day (as in DayOfWeek)
     */
    private String dayOfWeek;

    public DayMapping(String originalDay, String dayOfWeek) {
        this.originalDay = originalDay;
        this.dayOfWeek = dayOfWeek;
    }
}
