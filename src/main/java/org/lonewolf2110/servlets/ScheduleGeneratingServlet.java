package org.lonewolf2110.servlets;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lonewolf2110.enums.FileType;
import org.lonewolf2110.enums.ResponseStatus;
import org.lonewolf2110.models.RequestEntity;
import org.lonewolf2110.models.ResponseEntity;
import org.lonewolf2110.models.SheetData;
import org.lonewolf2110.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class ScheduleGeneratingServlet extends HttpServlet {
    private static final String TEMP_DIRECTORY_PATH = "gtemp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String body = IOUtils.toString(request.getReader());

        Gson gson = new Gson();
        RequestEntity req = gson.fromJson(body, RequestEntity.class);

        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || password == null) {
            // BAD REQUEST
            response.sendError(400);
            return;
        }

        InputStream inputStream;
        String semester;

        try (KMAClient client = new KMAClient()) {
            ResponseStatus status = client.getScheduleAsStream(username, password);

            if (status == ResponseStatus.UNAUTHORIZED) {
                // UNAUTHORIZED
                response.sendError(401);
                return;
            }

            if (status == ResponseStatus.INTERNAL_SERVER_ERROR) {
                // KMA Server Error
                response.sendError(503);
                return;
            }

            inputStream = client.getInputStream();
            semester = client.getSemester();
        } catch (Exception e) {
            //Server Error
            response.sendError(500);
            return;
        }

        List<SheetData> sheetDataList;

        try (KMAScheduleReader reader = new KMAScheduleReader()) {
            reader.read(inputStream);
            sheetDataList = reader.getWorkbookData();
        } catch (Exception e) {
            //Server Error
            response.sendError(500);
            return;
        } finally {
            inputStream.close();
        }

        String tempPath = getServletContext().getRealPath("") + TEMP_DIRECTORY_PATH;
        File tempDir = new File(tempPath);

        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                // Internal Server Error
                response.sendError(500);
                return;
            }
        }


        KSGStorage storage;
        String parentId;
        String parrentWebViewLink;
        try {
            storage = new KSGStorage();
            com.google.api.services.drive.model.File parent = storage.makeFolder(username);
            parentId = parent.getId();
            parrentWebViewLink = parent.getWebViewLink();
        } catch (Exception e) {
            e.printStackTrace();
            // Internal server error
            response.sendError(500);
            return;
        }

        KMAScheduleGenerator generator = new KMAScheduleGenerator(sheetDataList);

        for (FileType fileType : FileType.values()) {
            long now = LocalDateUtils.now().toNanos();
            String tempFilename = String.format("%s.%s", now, fileType.getFileExtension());
            File tempFile = new File(tempDir, tempFilename);

            if (tempFile.exists()) {
                try {
                    FileUtils.forceDelete(tempFile);
                } catch (Exception e) {
                    // Internal server error
                    response.sendError(500);
                    return;
                }
            }

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                generator.setOutputStream(outputStream);
                generator.generate(fileType);
            } catch (Exception e) {
                response.sendError(500);
                return;
            }

            String gFileName = String.format("HK%s.%s", semester, fileType.getFileExtension());
            try {
                storage.uploadFile(tempFile, parentId, gFileName, fileType);
            } catch (Exception e) {
                e.printStackTrace();
                // Internal Server Error
                response.sendError(500);
                return;
            }

            try {
                FileUtils.forceDelete(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ResponseEntity responseEntity = new ResponseEntity(parrentWebViewLink);
        String responseString = gson.toJson(responseEntity);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.print(responseString);
        out.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");

        if (username == null) {
            // Bad request
            response.sendError(400);
            return;
        }

        KSGStorage storage;
        try {
            storage = new KSGStorage();
            List<com.google.api.services.drive.model.File> folderList = storage.searchFolder(username);

            if (folderList.size() == 0) {
                response.sendError(404);
            } else {
                com.google.api.services.drive.model.File folder = folderList.get(0);
                ResponseEntity responseEntity = new ResponseEntity(folder.getWebViewLink());
                String responseString = new Gson().toJson(responseEntity);
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                out.print(responseString);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Internal Server Error
            response.sendError(500);
        }
    }
}
