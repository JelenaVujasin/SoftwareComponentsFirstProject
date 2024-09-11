package org.specification.specification;

import org.specification.specification.wrappers.ScheduleFilterSpecificationWrapper;
import org.specification.specification.wrappers.ScheduleSpecificationWrapper;

public class ScheduleManager {
    private static ScheduleSpecificationWrapper scheduler = null;
    private static ScheduleFilterSpecificationWrapper filter = null;

    public static void setScheduler(ScheduleSpecificationWrapper specificationImplementation){
        scheduler = specificationImplementation;
    }

    public static void setFilter(ScheduleFilterSpecificationWrapper specificationImplementation){
        filter = specificationImplementation;
    }

    public static ScheduleSpecificationWrapper getScheduler(){
        scheduler.initialize();
        return scheduler;
    }

    public static ScheduleFilterSpecificationWrapper getFilter(){
        return filter;
    }
}
