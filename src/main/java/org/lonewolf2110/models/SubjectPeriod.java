package org.lonewolf2110.models;

import org.lonewolf2110.enums.ClassPeriod;
import org.lonewolf2110.enums.Weekday;

public class SubjectPeriod implements Comparable<SubjectPeriod> {
    private String subject, classroom;
    private Weekday weekday;
    private ClassPeriodRange classPeriodRange;
    private DateRange dateRange;

    public SubjectPeriod(String subject, String classroom, Weekday weekday, ClassPeriodRange classPeriodRange, DateRange dateRange) {
        this.subject = subject;
        this.classroom = classroom;
        this.weekday = weekday;
        this.classPeriodRange = classPeriodRange;
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

    public ClassPeriodRange getClassPeriodRange() {
        return classPeriodRange;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    @Override
    public int compareTo(SubjectPeriod another) {
        int wdVal1 = this.getWeekday().getValue();
        int wdVal2 = another.getWeekday().getValue();

        if (wdVal1 != wdVal2) {
            return Integer.compare(wdVal1, wdVal2);
        }

        ClassPeriod cp1 = this.getClassPeriodRange().getStart();
        ClassPeriod cp2 = another.getClassPeriodRange().getStart();

        return Integer.compare(cp1.getValue(), cp2.getValue());
    }
}
