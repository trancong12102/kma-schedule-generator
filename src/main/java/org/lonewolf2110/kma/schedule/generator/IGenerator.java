package org.lonewolf2110.kma.schedule.generator;

import org.lonewolf2110.kma.schedule.data.enums.FileType;

import java.io.IOException;
import java.io.OutputStream;

public interface IGenerator {
    void generate(FileType fileType) throws IOException;

    void setOutputStream(OutputStream outputStream);
}
