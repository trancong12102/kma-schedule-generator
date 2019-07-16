package org.lonewolf2110.kma.schedule.data.range;

import org.lonewolf2110.kma.schedule.data.enums.ClassPeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeriodRange {
    private ClassPeriod start, end;

    public PeriodRange(ClassPeriod start, ClassPeriod end) {
        this.start = start;
        this.end = end;
    }

    public PeriodRange(int startVal, int endVal) {
        this.start = ClassPeriod.getPeriod(startVal);
        this.end = ClassPeriod.getPeriod(endVal);
    }

    public PeriodRange(String periodRangeString) {
        String periodRegex = "\\d{1,2}";
        Pattern periodPattern = Pattern.compile(periodRegex);
        Matcher matcher = periodPattern.matcher(periodRangeString);
        List<Integer> periodList = new ArrayList<>();

        while (matcher.find()) {
            String str = periodRangeString.substring(matcher.start(), matcher.end());
            periodList.add(Integer.parseInt(str));
        }

        int startPeriodInt = periodList.get(0);
        int endPeriodInt = periodList.get(periodList.size() - 1);

        this.start = ClassPeriod.getPeriod(startPeriodInt);
        this.end = ClassPeriod.getPeriod(endPeriodInt);
    }

    public ClassPeriod getStart() {
        return start;
    }

    public ClassPeriod getEnd() {
        return end;
    }
}
