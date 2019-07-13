package org.lonewolf2110.utils.interfaces;

import org.lonewolf2110.models.SheetData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IKMAScheduleReader {
    List<SheetData> getWorkbookData();

    void read(InputStream inputStream) throws IOException;
}
