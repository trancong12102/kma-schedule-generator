package org.lonewolf2110.utils;

import org.lonewolf2110.models.DateRange;

import java.time.*;

public class LocalDateUtils {
    public static LocalDate getStartDayOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

    public static LocalDate getEndDayOfWeek(LocalDate date) {
        return date.plusDays(DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue());
    }

    public static boolean isBetween(DateRange outer, DateRange inner) {
        LocalDate outerStartDate = outer.getStart();
        LocalDate outerEndDate = outer.getEnd();
        LocalDate innerStartDate = inner.getStart();
        LocalDate innerEndDate = inner.getEnd();
        return (outerStartDate.isBefore(innerStartDate) || outerStartDate.isEqual(innerStartDate))
                && (outerEndDate.isAfter(innerEndDate) || outerEndDate.isEqual(innerEndDate));
    }

    public static Duration now() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime todayStart = now.toLocalDate().atStartOfDay(zoneId);
        return Duration.between(todayStart, now);
    }
}
