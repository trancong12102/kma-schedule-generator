package org.lonewolf2110.models;

import org.lonewolf2110.utils.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateRange {
    private LocalDate start, end;

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public DateRange(String dateRangeString) {
        List<String> dateStringList = StringUtils.split(dateRangeString, "-");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.start = LocalDate.parse(dateStringList.get(0), dtf);
        this.end = LocalDate.parse(dateStringList.get(1), dtf);
    }
}
