package org.lonewolf2110.kma.schedule.enums;

public enum ResponseStatus {
    SUCCESS(200),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500);

    int code;

    ResponseStatus(int code) {
        this.code = code;
    }

    public static ResponseStatus getResponseStatus(int code) {
        for (ResponseStatus responseStatus : ResponseStatus.values()) {
            if (responseStatus.getCode() == code) {
                return responseStatus;
            }
        }

        return null;
    }

    public int getCode() {
        return code;
    }
}
