package org.lonewolf2110.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassWeek {
    private List<SubjectPeriod> subjectPeriodList;

    public boolean isEqual(ClassWeek another) {
        List<SubjectPeriod> s1 = this.subjectPeriodList;
        List<SubjectPeriod> s2 = another.getSubjectPeriodList();

        if (s1.size() != s2.size()) {
            return false;
        }

        for (int i = 0; i < s1.size(); i++) {
            if (s1.get(i) != s2.get(i)) {
                return false;
            }
        }

        return true;
    }

    public void sort() {
        Collections.sort(this.subjectPeriodList);
    }

    public void add(SubjectPeriod subjectPeriod) {
        this.subjectPeriodList.add(subjectPeriod);
    }

    public List<SubjectPeriod> getSubjectPeriodList() {
        return subjectPeriodList;
    }

    public ClassWeek() {
        this.subjectPeriodList = new ArrayList<>();
    }
}
