package org.lonewolf2110.models;

import org.lonewolf2110.enums.Weekday;

import java.util.ArrayList;
import java.util.List;

public class SheetData {
    private DateRange dateRange;
    private ClassWeek classWeek;

    public List<SubjectPeriod> getSubjectPeriodList() {
        return this.classWeek.getSubjectPeriodList();
    }

    public List<SubjectPeriod> getSubjectPeriodList(Weekday weekday) {
        List<SubjectPeriod> subjectPeriodList = new ArrayList<>();

        for (SubjectPeriod subjectPeriod : this.classWeek.getSubjectPeriodList()) {
            if (subjectPeriod.getWeekday().getValue() == weekday.getValue()) {
                subjectPeriodList.add(subjectPeriod);
            }
        }

        return subjectPeriodList;
    }

    public SheetData(DateRange dateRange, ClassWeek classWeek) {
        this.dateRange = dateRange;
        this.classWeek = classWeek;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public boolean isEmpty() {
        return classWeek.getSubjectPeriodList().isEmpty();
    }
}
