package org.lonewolf2110.kma.schedule.tools;

import org.lonewolf2110.kma.schedule.enums.FileType;

import java.io.IOException;
import java.io.OutputStream;

public interface IGenerator {
    void generate(FileType fileType) throws IOException;

    void setOutputStream(OutputStream outputStream);
}
