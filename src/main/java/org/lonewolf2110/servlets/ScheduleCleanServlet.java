package org.lonewolf2110.servlets;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class ScheduleCleanServlet extends HttpServlet {
    private static final String TEMP_DIRECTORY_PATH = "gtemp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        File generatedDir = new File(getServletContext().getRealPath(""), TEMP_DIRECTORY_PATH);

        if (generatedDir.exists()) {
            // Delete old file
            try (Stream<Path> paths = Files.walk(generatedDir.toPath())) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
                                FileTime time = attr.creationTime();
                                Instant fileInstant = time.toInstant();
                                Instant now = Instant.now();
                                Duration diff = Duration.between(fileInstant, now);
                                long minutes = Math.abs(diff.toMinutes());

                                out.println(String.format("[INFO] %s %s mins %s", path.getFileName(), minutes, minutes > 5 ? "DELETE" : "KEEP"));

                                if (minutes > 30) {
                                    FileUtils.forceDelete(path.toFile());
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }
}
