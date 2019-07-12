package org.lonewolf2110.utils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.lonewolf2110.enums.FileType;
import org.lonewolf2110.enums.PeriodRangeTextColor;
import org.lonewolf2110.enums.Weekday;
import org.lonewolf2110.models.ClassPeriodRange;
import org.lonewolf2110.models.DateRange;
import org.lonewolf2110.models.SheetData;
import org.lonewolf2110.models.SubjectPeriod;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KMAScheduleGenerator {
    private OutputStream outputStream;
    private List<SheetData> sheetDataList;

    public KMAScheduleGenerator(List<SheetData> sheetDataList, OutputStream outputStream) {
        this.sheetDataList = sheetDataList;
        this.outputStream = outputStream;
    }

    public KMAScheduleGenerator(List<SheetData> sheetDataList) {
        this.sheetDataList = sheetDataList;
    }

    public void generate(FileType fileType) throws IOException {
        switch (fileType) {
            case EXCEL:
                generateExcel();
                break;
            case PDF:
                generatePDF();
                break;
            case PLAIN_TEXT:
                generateText();
                break;
        }
    }

    private void generateText() {
        PrintWriter printWriter = new PrintWriter(outputStream);
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);

        for (SheetData sheetData : sheetDataList) {
            printWriter.println("==============================================");
            DateRange dateRange = sheetData.getDateRange();
            printWriter.println(String.format("Từ %s đến %s", dateRange.getStart().format(dtf), dateRange.getEnd().format(dtf)));

            for (Weekday weekday : Weekday.values()) {
                printWriter.println("---------------------------------------------");
                printWriter.println(weekday.getText());

                for (SubjectPeriod subjectPeriod : sheetData.getSubjectPeriodList(weekday)) {
                    ClassPeriodRange pR = subjectPeriod.getClassPeriodRange();
                    String classroom = subjectPeriod.getClassroom();
                    String subject = subjectPeriod.getSubject();

                    String txt = String.format("(%s - %s) tại %s: %s", pR.getStart().getValue(), pR.getEnd().getValue(), classroom, subject);
                    printWriter.println(txt);
                }
            }
        }
        printWriter.close();
    }

    private void generatePDF() throws IOException {
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        final String FONT_REGULAR = getClass().getClassLoader().getResource("font/Open_Sans/OpenSans-Regular.ttf").getPath();
        final String FONT_BOLD = getClass().getClassLoader().getResource("font/Open_Sans/OpenSans-Bold.ttf").getPath();

        PdfFont fontBold = PdfFontFactory.createFont(FONT_BOLD, PdfEncodings.IDENTITY_H);
        PdfFont fontRegular = PdfFontFactory.createFont(FONT_REGULAR, PdfEncodings.IDENTITY_H);

        DateTimeFormatter dtf;

        for (SheetData sheetData : sheetDataList) {
            DateRange dateRange = sheetData.getDateRange();
            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String sheetTitle = String.format("Từ %s đến %s", dateRange.getStart().format(dtf), dateRange.getEnd().format(dtf));
            document.add(
                    new Paragraph(sheetTitle)
                            .setFont(fontBold)
                            .setFontSize(24f)
            );

            for (Weekday weekday : Weekday.values()) {
                document.add(
                        new Paragraph(weekday.getText())
                                .setFont(fontRegular)
                                .setUnderline()
                );

                for (SubjectPeriod subjectPeriod : sheetData.getSubjectPeriodList(weekday)) {
                    ClassPeriodRange range = subjectPeriod.getClassPeriodRange();
                    String classroom = subjectPeriod.getClassroom() + " ";
                    String subject = subjectPeriod.getSubject();

                    Color textColor = PeriodRangeTextColor.getTextColor(range).getColor();
                    if (textColor == null) {
                        textColor = new DeviceRgb(0, 0, 0);
                    }
                    String rangeText = String.format("(%s - %s): ", range.getStart().getValue(), range.getEnd().getValue());
                    document.add(
                            new Paragraph()
                                    .setFont(fontRegular)
                                    .setMarginLeft(28f)
                                    .add(new Text(rangeText).setBold().setFontColor(textColor))
                                    .add(new Text(classroom).setBold().setUnderline())
                                    .add(new Text(subject))
                    );
                }

            }
        }

        document.close();
    }

    private void generateExcel() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("Template.xlsx").getFile());
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        XSSFCreationHelper factory = workbook.getCreationHelper();

        DateTimeFormatter dtf;

        for (SheetData sheetData : sheetDataList) {
            XSSFSheet sheet = workbook.cloneSheet(0);

            DateRange dateRange = sheetData.getDateRange();
            dtf = DateTimeFormatter.ofPattern("dd-MM");
            String sheetName = String.format("%s đến %s", dateRange.getStart().format(dtf), dateRange.getEnd().format(dtf));
            int idx = workbook.getSheetIndex(sheet);
            workbook.setSheetName(idx, sheetName);

            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String sheetTitle = String.format("Từ %s đến %s", dateRange.getStart().format(dtf), dateRange.getEnd().format(dtf));
            sheet.getRow(1).getCell(1).setCellValue(sheetTitle);

            XSSFDrawing drawing = sheet.createDrawingPatriarch();

            for (SubjectPeriod subjectPeriod : sheetData.getSubjectPeriodList()) {
                ClassPeriodRange classPeriodRange = subjectPeriod.getClassPeriodRange();
                int sRIdx = classPeriodRange.getStart().getValue() + 3;
                int eRIdx = classPeriodRange.getEnd().getValue() + 3;
                int cIdx = subjectPeriod.getWeekday().getValue();

                XSSFCell cell = sheet.getRow(sRIdx).getCell(cIdx);
                String value = String.format("%s tại %s", subjectPeriod.getSubject(), subjectPeriod.getClassroom());
                cell.setCellValue(value);

                XSSFClientAnchor anchor = factory.createClientAnchor();
                anchor.setCol1(cIdx);
                anchor.setCol2(cIdx + 1);
                anchor.setRow1(sRIdx);
                anchor.setRow2(eRIdx + 1);

                XSSFComment comment = drawing.createCellComment(anchor);
                comment.setString(subjectPeriod.getClassroom());

                cell.setCellComment(comment);

                sheet.addMergedRegion(new CellRangeAddress(sRIdx, eRIdx, cIdx, cIdx));
            }
        }

        workbook.removeSheetAt(0);
        workbook.write(outputStream);
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

}
