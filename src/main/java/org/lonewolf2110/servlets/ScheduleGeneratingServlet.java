package org.lonewolf2110.servlets;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lonewolf2110.enums.FileType;
import org.lonewolf2110.models.RequestEntity;
import org.lonewolf2110.models.ResponseEntity;
import org.lonewolf2110.models.SheetData;
import org.lonewolf2110.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class ScheduleGeneratingServlet extends HttpServlet {
    private static final String TEMP_DIRECTORY_PATH = "gtemp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //---------------------------------------------------------------------------------
        // Parse json from request
        String body = IOUtils.toString(request.getReader());

        Gson gson = new Gson();
        RequestEntity req = gson.fromJson(body, RequestEntity.class);

        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || password == null) {
            // BAD REQUEST
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //----------------------------------------------------------------------------------
        // Get schedule excel file from KMA server

        Instant start = Instant.now();

        KMASoupClient client = new KMASoupClient();
        int kStatus = client.login(username, password);

        if (kStatus != HttpServletResponse.SC_OK) {
            response.sendError(kStatus);
            return;
        }

        kStatus = client.getScheduleAsStream();

        if (kStatus != HttpServletResponse.SC_OK) {
            response.sendError(kStatus);
            return;
        }

        InputStream inputStream = client.getInputStream();
        String semester = StringUtils.reverseSemester(client.getSemester());

        /*
        // Using HtmlUnit instead of JSoup
        InputStream inputStream;
        String semester;

        try (KMAUnitClient client = new KMAUnitClient()) {
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
        }*/

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Get excel file from KMA server in " + duration.toMillis() + " milliseconds");

        //-----------------------------------------------------------------------------
        // Get workbook data from downloaded excel file

        start = Instant.now();

        List<SheetData> workbookData;

        try (KMAScheduleReader reader = new KMAScheduleReader()) {
            reader.read(inputStream);
            workbookData = reader.getWorkbookData();
        } catch (Exception e) {
            //Server Error
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } finally {
            inputStream.close();
        }

        if (workbookData.isEmpty()) {
            response.setStatus(204);
            return;
        }

        System.out.println("Get data list [OK]");

        end = Instant.now();
        duration = Duration.between(start, end);
        System.out.println("Get schedule data list in " + duration.toMillis() + " milliseconds");

        //------------------------------------------------------------------------------
        // Temp path

        String tempPath = getServletContext().getRealPath("") + TEMP_DIRECTORY_PATH;
        File tempDir = new File(tempPath);

        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                // Internal Server Error
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        //----------------------------------------------------------------------------------
        // Create folder on Google drive and get webview link

        start = Instant.now();

        KSGStorage storage;
        String parentId;
        String parentWebViewLink;
        try {
            storage = new KSGStorage();
            com.google.api.services.drive.model.File parent = storage.makeFolder(username);
            parentId = parent.getId();
            parentWebViewLink = parent.getWebViewLink();
        } catch (Exception e) {
            e.printStackTrace();
            // Internal server error
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        System.out.println("Get parent webview link [OK]");

        end = Instant.now();
        duration = Duration.between(start, end);
        System.out.println("Get parent webview link in " + duration.toMillis() + " milliseconds");

        //-------------------------------------------------------------------------------------
        // Generate schedule file and upload to Google drive

        KMAScheduleGenerator generator = new KMAScheduleGenerator(workbookData);

        for (FileType fileType : FileType.values()) {
            long now = LocalDateUtils.now().toNanos();
            String tempFilename = String.format("%s.%s", now, fileType.getFileExtension());
            File tempFile = new File(tempDir, tempFilename);

            if (tempFile.exists()) {
                try {
                    FileUtils.forceDelete(tempFile);
                } catch (Exception e) {
                    // Internal server error
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }

            start = Instant.now();

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                generator.setOutputStream(outputStream);
                generator.generate(fileType);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            end = Instant.now();
            duration = Duration.between(start, end);
            System.out.println("Generated file " + tempFilename + " in " + duration.toMillis() + " milliseconds");

            start = Instant.now();

            String gFileName = String.format("HK%s.%s", semester, fileType.getFileExtension());
            try {
                storage.uploadFile(tempFile, parentId, gFileName, fileType);
            } catch (Exception e) {
                e.printStackTrace();
                // Internal Server Error
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            end = Instant.now();
            duration = Duration.between(start, end);
            System.out.println("Uploaded file " + tempFilename + " in " + duration.toMillis() + " milliseconds");

            try {
                FileUtils.forceDelete(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Generate and upload file [OK]");

        //-------------------------------------------------------------------------
        // Send json response to client

        ResponseEntity responseEntity = new ResponseEntity(parentWebViewLink);
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        KSGStorage storage;
        try {
            storage = new KSGStorage();
            List<com.google.api.services.drive.model.File> folderList = storage.searchFolder(username);

            if (folderList.size() == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
