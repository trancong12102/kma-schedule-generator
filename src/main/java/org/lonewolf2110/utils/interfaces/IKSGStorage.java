package org.lonewolf2110.utils;

import com.google.api.services.drive.model.File;
import org.lonewolf2110.enums.FileType;

import java.io.IOException;
import java.util.List;

public interface IKSGStorage {
    void delete(String id) throws IOException;

    void uploadFile(java.io.File filePath, String parentId, String filename, FileType fileType) throws IOException;

    List<File> searchFolder(String folder) throws IOException;

    File makeFolder(String folder) throws IOException;
}
