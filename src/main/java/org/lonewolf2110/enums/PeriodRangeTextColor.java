package org.lonewolf2110.enums;

import com.itextpdf.kernel.colors.DeviceRgb;
import org.lonewolf2110.models.ClassPeriodRange;

public enum PeriodRangeTextColor {
    CP1(1, new DeviceRgb(22, 160, 133)),
    CP4(4, new DeviceRgb(41, 128, 185)),
    CP7(7, new DeviceRgb(241, 196, 15)),
    CP10(10, new DeviceRgb(230, 126, 34)),
    CP13(13, new DeviceRgb(192, 57, 43)),;

    private int startValue;
    DeviceRgb color;

    public static PeriodRangeTextColor getTextColor(ClassPeriodRange periodRange) {
        for (PeriodRangeTextColor textColor : PeriodRangeTextColor.values()) {
            if (textColor.getStartValue() == periodRange.getStart().getValue()) {
                return textColor;
            }
        }

        return null;
    }

    PeriodRangeTextColor(int i, DeviceRgb deviceRgb) {
        this.startValue = i;
        this.color = deviceRgb;
    }

    public int getStartValue() {
        return startValue;
    }

    public DeviceRgb getColor() {
        return color;
    }
}
