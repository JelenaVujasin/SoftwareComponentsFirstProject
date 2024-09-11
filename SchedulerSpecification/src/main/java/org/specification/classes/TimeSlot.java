package org.specification.classes;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
/**
 * Class representing a time slot in the schedule
 */
public class TimeSlot {
        /**
         * Name of the time slot
         */
        private String name;
        /**
         * Day of the week of the time slot
         */
        private DayOfWeek dayOfWeek;
        /**
         * Starting time of the time slot
         */
        private LocalTime startTime;
        /**
         * Ending time of the time slot
         */
        private LocalTime endTime;
        /**
         * Starting date of the time slot
         */
        private LocalDate startDate;
        /**
         * Ending date of the time slot
         */
        private LocalDate endDate;
        /**
         * Place of the time slot
         */
        private Place place;
        /**
         * Group id of the time slot (Used for identifying connected recurring time slots)
         */
        private UUID groupId;
        /**
         * Map of the additional data
         */
        private Map<String, String> additional;

        public TimeSlot() {
                additional = new HashMap<>();
        }

        public TimeSlot(UUID groupId , String name, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate, Place place) {
                this.groupId = groupId;
                this.name = name;
                this.dayOfWeek = dayOfWeek;
                this.startTime = startTime;
                this.endTime = endTime;
                this.startDate = startDate;
                this.endDate = endDate;
                this.place = place;
        }

        public TimeSlot(String name, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate, Place place) {
                this.name = name;
                this.startTime = startTime;
                this.endTime = endTime;
                this.startDate = startDate;
                this.endDate = endDate;
                this.place = place;
        }


        /**
         *  Checks if the time slot is recurring
         * @return True if the time slot is recurring
         */
        public boolean isRecurring(){
                return groupId != null;
        }

        @Override
        public String toString() {
                return "\nName: " + this.getName() + "\nstartTime: " + this.getStartTime() + "\nendTime: " + this.getEndTime()
                        + "\nstartDate: " + this.getStartDate() + "\nendDate: " + getEndDate() + "\nPlace: " + this.getPlace()
                        + "\nAdditional: " + this.getAdditional();
        }


}
