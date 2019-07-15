package org.lonewolf2110.kma.schedule.enums;

public enum ClassPeriod {
    CP1(1),
    CP2(2),
    CP3(3),
    CP4(4),
    CP5(5),
    CP6(6),
    CP7(7),
    CP8(8),
    CP9(9),
    CP10(10),
    CP11(11),
    CP12(12),
    CP13(13),
    CP14(14),
    CP15(15),
    CP16(16);

    int value;

    ClassPeriod(int value) {
        this.value = value;
    }

    public static ClassPeriod getPeriod(int value) {

        for (ClassPeriod classPeriod : ClassPeriod.values()) {

            if (classPeriod.getValue() == value) {
                return classPeriod;
            }

        }

        return null;
    }

    public int getValue() {
        return value;
    }
}
