package org.lonewolf2110.kma.schedule.client;

import com.google.common.io.ByteSource;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.lonewolf2110.kma.schedule.utils.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class SoupClient implements IClient {
    private static final String LOGIN_PAGE_TITLE = ".: Đăng nhập :.";
    private static final String HOMEPAGE_TITLE = ".: Hệ thống đăng ký học :.";

    private static final String LOGIN_URL = "http://115.146.127.72/CMCSoft.IU.Web.Info/Login.aspx";
    private static final String SCHEDULE_URL = "http://115.146.127.72/CMCSoft.IU.Web.Info/Reports/Form/StudentTimeTable.aspx";

    private Map<String, String> cookies;
    private InputStream inputStream;
    private String semester;

    @Override
    public int login(String username, String password) {
        try {
            Document doc = Jsoup
                    .connect(LOGIN_URL)
                    .get();
            FormElement form = (FormElement) doc.selectFirst("#Form1");
            List<Connection.KeyVal> loginData = form.formData();
            setKeyVal(loginData, "txtUserName", username);
            setKeyVal(loginData, "txtPassword", DigestUtils.md5Hex(password));

            Connection.Response response = Jsoup
                    .connect(LOGIN_URL)
                    .method(Connection.Method.POST)
                    .data(loginData)
                    .execute();
            doc = response.parse();
            String title = doc.title().trim();

            if (title.equals(LOGIN_PAGE_TITLE)) {
                return HttpServletResponse.SC_UNAUTHORIZED;
            }

            if (!title.equals(HOMEPAGE_TITLE)) {
                return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
            }

            this.cookies = response.cookies();
            System.out.println("LOGIN [OK]");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
        }

        return HttpServletResponse.SC_OK;
    }

    @Override
    public int readScheduleAsStream() {
        try {
            Document doc = Jsoup
                    .connect(SCHEDULE_URL)
                    .cookies(cookies)
                    .get();

            FormElement form = (FormElement) doc.selectFirst("#Form1");
            List<Connection.KeyVal> scheduleFormData = form.formData();
            setKeyVal(scheduleFormData, "drpType", "B");
            this.semester = getVal(scheduleFormData, "hidSemester");
            reverseSemester();

            Connection.Response res = Jsoup
                    .connect(SCHEDULE_URL)
                    .data(scheduleFormData)
                    .method(Connection.Method.POST)
                    .cookies(cookies)
                    .ignoreContentType(true)
                    .execute();

            this.inputStream = ByteSource.wrap(res.bodyAsBytes()).openStream();
            System.out.println("GET SCHEDULE FROM KMA SITE [OK]");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
        }

        return HttpServletResponse.SC_OK;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    public String getSemester() {
        return semester;
    }

    private void reverseSemester() {
        List<String> wordList = StringUtils.split(semester, "_");

        if (wordList.size() == 3) {
            semester = wordList.get(2) + "_" + wordList.get(0) + "_" + wordList.get(1);
        }
    }

    private void setKeyVal(List<Connection.KeyVal> formData, String key, String value) {
        for (Connection.KeyVal keyVal : formData) {
            if (keyVal.key().equals(key)) {
                keyVal.value(value);
                break;
            }
        }
    }

    private String getVal(List<Connection.KeyVal> formData, String key) {
        for (Connection.KeyVal keyVal : formData) {
            if (keyVal.key().equals(key)) {
                return keyVal.value();
            }
        }

        return null;
    }

}
