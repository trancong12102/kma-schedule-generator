package org.lonewolf2110.kma.schedule.models;

import org.lonewolf2110.kma.schedule.enums.Weekday;

import java.util.ArrayList;
import java.util.List;

public class SheetData {
    private DateRange dateRange;
    private ClassWeek classWeek;

    public SheetData(DateRange dateRange, ClassWeek classWeek) {
        this.dateRange = dateRange;
        this.classWeek = classWeek;
    }

    public List<SubjectStage> getSubjectPeriodList() {
        return this.classWeek.getSubjectStageList();
    }

    public List<SubjectStage> getSubjectPeriodList(Weekday weekday) {
        List<SubjectStage> subjectStageList = new ArrayList<>();

        for (SubjectStage subjectStage : this.classWeek.getSubjectStageList()) {
            if (subjectStage.getWeekday().getValue() == weekday.getValue()) {
                subjectStageList.add(subjectStage);
            }
        }

        return subjectStageList;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public boolean isEmpty() {
        return classWeek.getSubjectStageList().isEmpty();
    }
}
