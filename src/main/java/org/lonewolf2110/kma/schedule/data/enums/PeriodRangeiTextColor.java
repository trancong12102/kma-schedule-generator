package org.lonewolf2110.kma.schedule.data.enums;

import com.itextpdf.kernel.colors.DeviceRgb;
import org.lonewolf2110.kma.schedule.data.range.PeriodRange;

public enum PeriodRangeiTextColor {
    CP1(1, new DeviceRgb(22, 160, 133)),
    CP4(4, new DeviceRgb(41, 128, 185)),
    CP7(7, new DeviceRgb(241, 196, 15)),
    CP10(10, new DeviceRgb(230, 126, 34)),
    CP13(13, new DeviceRgb(192, 57, 43)),
    ;

    DeviceRgb color;
    private int startValue;

    PeriodRangeiTextColor(int i, DeviceRgb deviceRgb) {
        this.startValue = i;
        this.color = deviceRgb;
    }

    public static PeriodRangeiTextColor getTextColor(PeriodRange periodRange) {
        for (PeriodRangeiTextColor textColor : PeriodRangeiTextColor.values()) {
            if (textColor.getStartValue() == periodRange.getStart().getValue()) {
                return textColor;
            }
        }

        return null;
    }

    public int getStartValue() {
        return startValue;
    }

    public DeviceRgb getColor() {
        return color;
    }
}
