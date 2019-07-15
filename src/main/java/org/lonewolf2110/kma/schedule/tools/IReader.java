package org.lonewolf2110.kma.schedule.tools;

import org.lonewolf2110.kma.schedule.models.SheetData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IReader {
    List<SheetData> parseWorkbookData();

    void read(InputStream inputStream) throws IOException;
}
