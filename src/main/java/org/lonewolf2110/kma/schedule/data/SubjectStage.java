package org.lonewolf2110.kma.schedule.data;

import org.lonewolf2110.kma.schedule.data.enums.ClassPeriod;
import org.lonewolf2110.kma.schedule.data.enums.Weekday;
import org.lonewolf2110.kma.schedule.data.range.DateRange;
import org.lonewolf2110.kma.schedule.data.range.PeriodRange;

public class SubjectStage implements Comparable<SubjectStage> {
    private String subject, classroom;
    private Weekday weekday;
    private PeriodRange periodRange;
    private DateRange dateRange;

    public SubjectStage(String subject, String classroom, Weekday weekday, PeriodRange periodRange, DateRange dateRange) {
        this.subject = subject;
        this.classroom = classroom;
        this.weekday = weekday;
        this.periodRange = periodRange;
        this.dateRange = dateRange;
    }

    public String getSubject() {
        return subject;
    }

    public String getClassroom() {
        return classroom;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public PeriodRange getPeriodRange() {
        return periodRange;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    @Override
    public int compareTo(SubjectStage another) {
        int wdVal1 = this.getWeekday().getValue();
        int wdVal2 = another.getWeekday().getValue();

        if (wdVal1 != wdVal2) {
            return Integer.compare(wdVal1, wdVal2);
        }

        ClassPeriod cp1 = this.getPeriodRange().getStart();
        ClassPeriod cp2 = another.getPeriodRange().getStart();

        return Integer.compare(cp1.getValue(), cp2.getValue());
    }
}
