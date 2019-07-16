package org.lonewolf2110.kma.schedule.utils;

import org.lonewolf2110.kma.schedule.data.range.DateRange;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
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

    public static LocalDate parseDate(String dateString) {
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateString, dtf);
    }

    public static DateRange parseDateRange(String line) {
        String datePattern = "\\d{2}[/]\\d{2}[/]\\d{4}";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(line);
        List<LocalDate> dateList = new ArrayList<>();

        while (matcher.find()) {
            String str = line.substring(matcher.start(), matcher.end());
            dateList.add(parseDate(str));
        }

        return new DateRange(dateList.get(0), dateList.get(1));
    }
}
