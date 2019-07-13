package org.lonewolf2110.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.lonewolf2110.enums.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;

@Deprecated
public class KMAUnitClient implements AutoCloseable {
    private final String KMA_LOGIN_PAGE_URL = "http://115.146.127.72/CMCSoft.IU.Web.Info/Login.aspx";
    private final String KMA_LOGIN_PAGE_TITLE = ".: Đăng nhập :.";
    private final String KMA_HOMEPAGE_TITLE = ".: Hệ thống đăng ký học :.";
    private final String KMA_SCHEDULE_PAGE_URL = "http://115.146.127.72/CMCSoft.IU.Web.Info/Reports/Form/StudentTimeTable.aspx";
    private final String KMA_SCHEDULE_PAGE_TITLE = ".: Thời khóa biểu sinh viên :.";

    private WebClient webClient;
    private HtmlPage page;
    private InputStream inputStream;
    private String semester;

    public KMAUnitClient() {
        this.webClient = new WebClient(BrowserVersion.CHROME);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private ResponseStatus initialize() throws IOException {
        page = webClient.getPage(KMA_LOGIN_PAGE_URL);

        return getResponseStatus(KMA_LOGIN_PAGE_TITLE);
    }

    private ResponseStatus login(String username, String password) throws IOException {
        HtmlForm form = page.getFirstByXPath("//form[@id='Form1']");
        HtmlInput userNameTxtInp = form.getInputByName("txtUserName");
        HtmlInput passWordTxtInp = form.getInputByName("txtPassword");

        userNameTxtInp.setValueAttribute(username);
        passWordTxtInp.setValueAttribute(password);
        passWordTxtInp.focus();

        HtmlSubmitInput button = form.getInputByName("btnSubmit");
        button.focus();

        page = button.click();

        return getResponseStatus(KMA_HOMEPAGE_TITLE, KMA_LOGIN_PAGE_TITLE);
    }

    private ResponseStatus forwardToSchedulePage() throws IOException {
        page = webClient.getPage(KMA_SCHEDULE_PAGE_URL);

        return getResponseStatus(KMA_SCHEDULE_PAGE_TITLE, KMA_LOGIN_PAGE_TITLE);
    }

    private ResponseStatus getScheduleAsStream() {
        HtmlForm form = page.getFirstByXPath("//form[@id='Form1']");
        HtmlSelect select = form.getSelectByName("drpType");
        select.setSelectedIndex(1);

        HtmlSelect semesterSelect = form.getSelectByName("drpSemester");
        this.semester = semesterSelect.getOption(0).asText();

        HtmlInput button = form.getInputByName("btnView");
        try {
            inputStream = button.click().getEnclosingWindow().getEnclosedPage().getWebResponse().getContentAsStream();
        } catch (IOException e) {
            return ResponseStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseStatus.SUCCESS;
    }

    public ResponseStatus getScheduleAsStream(String username, String password) throws IOException {
        ResponseStatus status;

        if ((status = initialize()) != ResponseStatus.SUCCESS) {
            return status;
        }

        System.out.println("[INFO] INITIALIZE SUCCESSFULLY");

        if ((status = login(username, password)) != ResponseStatus.SUCCESS) {
            return status;
        }

        System.out.println("[INFO] LOGIN SUCCESSFULLY");

        if ((status = forwardToSchedulePage()) != ResponseStatus.SUCCESS) {
            return status;
        }

        System.out.println("[INFO] FORWARD TO SCHEDULE PAGE SUCCESSFULLY");

        status = getScheduleAsStream();

        if (status == ResponseStatus.SUCCESS) {
            System.out.println("[INFO] GET SCHEDULE AS STREAM SUCCESSFULLY");
        }

        return status;
    }

    private ResponseStatus getResponseStatus(String SUCCESS_TITLE) {
        String pageTitle = page.getTitleText().trim();

        if (pageTitle.equals(SUCCESS_TITLE)) {
            return ResponseStatus.SUCCESS;
        }

        return ResponseStatus.INTERNAL_SERVER_ERROR;
    }

    private ResponseStatus getResponseStatus(String SUCCESS_TITLE, String UNAUTHORIZED_TITLE) {
        String pageTitle = page.getTitleText().trim();

        if (pageTitle.equals(SUCCESS_TITLE)) {
            return ResponseStatus.SUCCESS;
        }

        if (pageTitle.equals(UNAUTHORIZED_TITLE)) {
            return ResponseStatus.UNAUTHORIZED;
        }

        return ResponseStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public void close() {
        page.cleanUp();
        webClient.close();
    }

    public String getSemester() {
        return semester;
    }
}
