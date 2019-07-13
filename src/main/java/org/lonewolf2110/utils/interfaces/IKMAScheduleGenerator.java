package org.lonewolf2110.utils;

import org.lonewolf2110.enums.FileType;

import java.io.IOException;
import java.io.OutputStream;

public interface IKMAScheduleGenerator {
    void generate(FileType fileType) throws IOException;
    void setOutputStream(OutputStream outputStream);
}
