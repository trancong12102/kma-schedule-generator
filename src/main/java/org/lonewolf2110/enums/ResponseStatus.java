package org.lonewolf2110.enums;

public enum ResponseStatus {
    SUCCESS(200),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500);

    int code;

    public int getCode() {
        return code;
    }

    public static ResponseStatus getResponseStatus(int code) {
        for (ResponseStatus responseStatus : ResponseStatus.values()){
            if (responseStatus.getCode() == code) {
                return responseStatus;
            }
        }

        return null;
    }

    ResponseStatus(int code) {
        this.code = code;
    }
}
