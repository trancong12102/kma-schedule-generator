package org.lonewolf2110.kma.schedule.servlets;

import com.google.common.io.ByteSource;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.lonewolf2110.kma.schedule.servlets.enties.RequestEntity;
import org.lonewolf2110.kma.schedule.servlets.enties.ResponseEntity;
import org.lonewolf2110.kma.schedule.data.enums.FileType;
import org.lonewolf2110.kma.schedule.data.SheetData;
import org.lonewolf2110.kma.schedule.generator.ScheduleGenerator;
import org.lonewolf2110.kma.schedule.reader.ScheduleReader;
import org.lonewolf2110.kma.schedule.client.SoupClient;
import org.lonewolf2110.kma.schedule.data.storage.StorageManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class GeneratingServlet extends HttpServlet {

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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //-----------------------------------------------------------------------------
        // Get workbook data from KMA schedule page

        Instant start = Instant.now();

        InputStream inputStream;
        String semester;

        try {
            SoupClient client = new SoupClient();
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

            inputStream = client.getInputStream();
            semester = client.getSemester();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Get excel file from KMA server in " + duration.toMillis() + " milliseconds");

        //----------------------------------------------------------------------------------
        // Parse schedule data from excel file
        start = Instant.now();

        List<SheetData> workbookData;

        try (ScheduleReader reader = new ScheduleReader()) {
            reader.read(inputStream);
            workbookData = reader.getWorkbookData();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            inputStream.close();
            return;
        }

        end = Instant.now();
        duration = Duration.between(start, end);
        System.out.println("Parse data from excel file in " + duration.toMillis() + " milliseconds");
        //----------------------------------------------------------------------------------
        // Create folder on Google drive and get webview link

        start = Instant.now();

        StorageManager storage;
        String parentId;
        String parentWebViewLink;

        try {
            storage = new StorageManager();
            com.google.api.services.drive.model.File parent = storage.makeFolder(username);
            parentId = parent.getId();
            parentWebViewLink = parent.getWebViewLink();
        } catch (Exception e) {
            e.printStackTrace();
            // Internal server error
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        end = Instant.now();
        duration = Duration.between(start, end);
        System.out.println("Get webview link in " + duration.toMillis() + " milliseconds");

        //-------------------------------------------------------------------------------------
        // Generate schedule file and upload to Google drive

        ScheduleGenerator generator = new ScheduleGenerator(workbookData);
        String filenameWithoutExtension = String.format("HK%s", semester);
        storage.deleteFiles(parentId, filenameWithoutExtension);

        for (FileType fileType : FileType.values()) {
            start = Instant.now();

            String gFileName = String.format("%s.%s", filenameWithoutExtension, fileType.getFileExtension());

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                generator.setOutputStream(outputStream);
                generator.generate(fileType);

                inputStream = ByteSource.wrap(outputStream.toByteArray()).openStream();
                storage.uploadFile(inputStream, parentId, gFileName, fileType);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            end = Instant.now();
            duration = Duration.between(start, end);
            System.out.println("Generated and uploaded file " + gFileName + " in " + duration.toMillis() + " milliseconds");
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

        StorageManager storage;

        try {
            storage = new StorageManager();
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
