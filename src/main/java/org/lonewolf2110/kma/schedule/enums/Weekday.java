package org.lonewolf2110.kma.schedule.enums;

public enum Weekday {
    MONDAY(2, "Thứ hai"),
    TUESDAY(3, "Thứ ba"),
    WEDNESDAY(4, "Thứ tư"),
    THURSDAY(5, "Thứ năm"),
    FRIDAY(6, "Thứ sáu"),
    SATURDAY(7, "Thứ bảy"),
    SUNDAY(1, "Chủ nhật");

    private int value;
    private String text;

    Weekday(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static Weekday getWeekday(int value) {

        for (Weekday weekday : Weekday.values()) {

            if (weekday.getValue() == value) {
                return weekday;
            }

        }

        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}
