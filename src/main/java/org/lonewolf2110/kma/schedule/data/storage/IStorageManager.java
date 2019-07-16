package org.lonewolf2110.kma.schedule.data.storage;

import com.google.api.services.drive.model.File;
import org.lonewolf2110.kma.schedule.data.enums.FileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IStorageManager {
    void delete(String id) throws IOException;

    void uploadFile(InputStream inputStream, String parentId, String filename, FileType fileType) throws IOException;

    List<File> searchFolder(String folder) throws IOException;

    File makeFolder(String folder) throws IOException;
}
