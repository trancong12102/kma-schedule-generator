package org.lonewolf2110.kma.schedule.data.enums;

public enum FileType {
    EXCEL("table/excel", "xlsx"),
    PDF("list/pdf", "pdf"),
    PLAIN_TEXT("text/plain", "txt");

    private String cType;
    private String fileExtension;

    FileType(String cType, String fileExtension) {
        this.cType = cType;
        this.fileExtension = fileExtension;
    }

    public static String getContentType(String cType) {
        FileType type = getFileType(cType);

        assert type != null;
        switch (type) {
            case EXCEL:
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case PDF:
                return "application/pdf";
        }

        return "text/plain";
    }

    public static FileType getFileType(String mimeType) {
        for (FileType type : FileType.values()) {
            if (type.getcType().equals(mimeType)) {
                return type;
            }
        }

        return null;
    }

    public String getcType() {
        return cType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

}
