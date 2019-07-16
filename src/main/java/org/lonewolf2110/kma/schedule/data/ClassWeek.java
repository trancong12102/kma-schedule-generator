package org.lonewolf2110.kma.schedule.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassWeek {
    private List<SubjectStage> subjectStageList;

    public ClassWeek() {
        this.subjectStageList = new ArrayList<>();
    }

    public boolean isEqual(ClassWeek another) {
        List<SubjectStage> s1 = this.subjectStageList;
        List<SubjectStage> s2 = another.getSubjectStageList();

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
        Collections.sort(this.subjectStageList);
    }

    public void add(SubjectStage subjectStage) {
        this.subjectStageList.add(subjectStage);
    }

    public List<SubjectStage> getSubjectStageList() {
        return subjectStageList;
    }
}
