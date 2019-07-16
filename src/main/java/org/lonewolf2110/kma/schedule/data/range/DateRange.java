package org.lonewolf2110.kma.schedule.data.range;

import org.lonewolf2110.kma.schedule.utils.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateRange {
    private LocalDate start, end;

    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public DateRange(String dateRangeString) {
        List<String> dateStringList = StringUtils.split(dateRangeString, "-");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.start = LocalDate.parse(dateStringList.get(0), dtf);
        this.end = LocalDate.parse(dateStringList.get(1), dtf);
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }
}
