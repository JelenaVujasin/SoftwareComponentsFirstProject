package raf.testapp;

import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

public class CommandHandler {
    public static void showTimeSlots(ScheduleSpecificationWrapper scheduleSpecificationWrapper) {
        System.out.println(scheduleSpecificationWrapper.getAppointments());
    }

}
