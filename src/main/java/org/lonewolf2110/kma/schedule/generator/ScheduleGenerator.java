package org.lonewolf2110.kma.schedule.generator;

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
import org.lonewolf2110.kma.schedule.data.enums.FileType;
import org.lonewolf2110.kma.schedule.data.enums.PeriodRangeiTextColor;
import org.lonewolf2110.kma.schedule.data.enums.Weekday;
import org.lonewolf2110.kma.schedule.data.range.DateRange;
import org.lonewolf2110.kma.schedule.data.range.PeriodRange;
import org.lonewolf2110.kma.schedule.data.SubjectStage;
import org.lonewolf2110.kma.schedule.data.SheetData;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class ScheduleGenerator implements IGenerator {
    private static final String FONT_REGULAR_PATH = "font/Open_Sans/OpenSans-Regular.ttf";
    private static final String FONT_BOLD_PATH = "font/Open_Sans/OpenSans-Bold.ttf";

    private OutputStream outputStream;
    private List<SheetData> sheetDataList;

    public ScheduleGenerator(List<SheetData> sheetDataList) {
        this.sheetDataList = sheetDataList;
    }

    @Override
    public void generate(FileType fileType) throws IOException {
        switch (fileType) {
            case EXCEL:
                generateExcel();
                break;
            case PDF:
                generatePDF();
                break;
            case PLAIN_TEXT:
                generatePlainText();
                break;
        }
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Deprecated
    private void generatePlainText() {
        PrintWriter printWriter = new PrintWriter(outputStream);
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);

        for (SheetData sheetData : sheetDataList) {
            printWriter.println("===================================================================");
            DateRange dateRange = sheetData.getDateRange();
            printWriter.println(String.format("Từ %s đến %s", dateRange.getStart().format(dtf), dateRange.getEnd().format(dtf)));

            for (Weekday weekday : Weekday.values()) {
                List<SubjectStage> subjectStageList = sheetData.getSubjectPeriodList(weekday);

                printWriter.println("----------------------------------------------------------------");
                printWriter.println(weekday.getText());

                for (SubjectStage subjectStage : subjectStageList) {
                    PeriodRange pR = subjectStage.getPeriodRange();
                    String classroom = subjectStage.getClassroom();
                    String subject = subjectStage.getSubject();

                    String txt = String.format("(%s - %s) tại %s: %s", pR.getStart().getValue(), pR.getEnd().getValue(), classroom, subject);
                    printWriter.println(txt);
                }
            }
        }

        printWriter.flush();
        printWriter.close();
    }

    @Deprecated
    private void generatePDF() throws IOException {
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        final String FONT_REGULAR = Objects.requireNonNull(getClass().getClassLoader().getResource(FONT_REGULAR_PATH))
                .getPath();
        final String FONT_BOLD = Objects.requireNonNull(getClass().getClassLoader().getResource(FONT_BOLD_PATH))
                .getPath();

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
                List<SubjectStage> subjectStageList = sheetData.getSubjectPeriodList(weekday);

                document.add(
                        new Paragraph(weekday.getText())
                                .setFont(fontRegular)
                                .setUnderline()
                );

                for (SubjectStage subjectStage : subjectStageList) {
                    PeriodRange range = subjectStage.getPeriodRange();
                    String classroom = subjectStage.getClassroom() + " ";
                    String subject = subjectStage.getSubject();

                    Color textColor = Objects.requireNonNull(PeriodRangeiTextColor.getTextColor(range)).getColor();
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
        File file = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("Template.xlsx"))
                        .getFile()
        );
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

            for (SubjectStage subjectStage : sheetData.getSubjectPeriodList()) {
                PeriodRange periodRange = subjectStage.getPeriodRange();
                int sRIdx = periodRange.getStart().getValue() + 3;
                int eRIdx = periodRange.getEnd().getValue() + 3;
                int cIdx = subjectStage.getWeekday().getValue();

                XSSFCell cell = sheet.getRow(sRIdx).getCell(cIdx);
                String value = String.format("%s tại %s", subjectStage.getSubject(), subjectStage.getClassroom());
                cell.setCellValue(value);

                XSSFClientAnchor anchor = factory.createClientAnchor();
                anchor.setCol1(cIdx);
                anchor.setCol2(cIdx + 1);
                anchor.setRow1(sRIdx);
                anchor.setRow2(eRIdx + 1);

                XSSFComment comment = drawing.createCellComment(anchor);
                comment.setString(subjectStage.getClassroom());

                cell.setCellComment(comment);

                sheet.addMergedRegion(new CellRangeAddress(sRIdx, eRIdx, cIdx, cIdx));
            }
        }

        workbook.removeSheetAt(0);
        workbook.write(outputStream);
        workbook.close();
    }

}
