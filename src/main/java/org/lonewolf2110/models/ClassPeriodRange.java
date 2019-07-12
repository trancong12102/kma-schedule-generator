package org.lonewolf2110.models;

import org.lonewolf2110.enums.ClassPeriod;
import org.lonewolf2110.utils.StringUtils;

import java.util.List;

public class ClassPeriodRange {
    private ClassPeriod start, end;

    public ClassPeriod getStart() {
        return start;
    }

    public ClassPeriod getEnd() {
        return end;
    }

    public ClassPeriodRange(ClassPeriod start, ClassPeriod end) {
        this.start = start;
        this.end = end;
    }

    public ClassPeriodRange(String periodRangeString) {
        List<String> periodStringList = StringUtils.split(periodRangeString, ",");
        int startPeriodInt = Integer.parseInt(periodStringList.get(0));
        int endPeriodInt = Integer.parseInt(periodStringList.get(periodStringList.size() - 1));
        this.start = ClassPeriod.getPeriod(startPeriodInt);
        this.end = ClassPeriod.getPeriod(endPeriodInt);
    }
}
