package org.lonewolf2110.kma.schedule.reader;

import org.lonewolf2110.kma.schedule.data.SheetData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IReader {
    List<SheetData> getWorkbookData();

    void read(InputStream inputStream) throws IOException;
}
