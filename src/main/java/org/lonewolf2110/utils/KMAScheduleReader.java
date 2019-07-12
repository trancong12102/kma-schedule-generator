package org.lonewolf2110.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.lonewolf2110.enums.Weekday;
import org.lonewolf2110.models.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class KMAScheduleReader implements AutoCloseable {
    private Workbook workbook;
    private List<SubjectPeriod> subjectPeriodList;
    private DateRange termRange;
    private POIFSFileSystem pfs;
    public KMAScheduleReader() {
        this.subjectPeriodList = new ArrayList<>();
        termRange = new DateRange(LocalDate.of(
                3000, 1, 1),
                LocalDate.of(2000, 1, 1)
        );

    }

    public List<SubjectPeriod> getSubjectPeriodList() {
        return subjectPeriodList;
    }

    private List<ClassWeek> getClassWeekList() {
        List<ClassWeek> classWeekList = new ArrayList<>();
        LocalDate termStart = termRange.getStart();
        LocalDate termEnd = termRange.getEnd();
        int weekCount = Math.toIntExact(ChronoUnit.WEEKS.between(termStart, termEnd) + 1);

        for (int i = 0; i < weekCount; i++) {
            LocalDate weekStartDate = termStart.plusDays(i * 7);
            LocalDate weekEndDate = weekStartDate.plusDays(6);
            ClassWeek classWeek = new ClassWeek();

            for (SubjectPeriod subjectPeriod : subjectPeriodList) {
                DateRange periodRange = subjectPeriod.getDateRange();
                LocalDate subjectPeriodStart = LocalDateUtils.getStartDayOfWeek(periodRange.getStart());
                LocalDate subjectPeriodEnd = LocalDateUtils.getEndDayOfWeek(periodRange.getEnd());

                if (LocalDateUtils.isBetween(
                        new DateRange(subjectPeriodStart, subjectPeriodEnd),
                        new DateRange(weekStartDate, weekEndDate)
                )) {
                    classWeek.add(subjectPeriod);
                }
            }

            classWeek.sort();
            classWeekList.add(classWeek);
        }

        return classWeekList;
    }

    public List<SheetData> getWorkbookData() {
        this.readPeriodList();
        List<ClassWeek> classWeekList = this.getClassWeekList();

        List<SheetData> workbookData = new ArrayList<>();
        classWeekList.add(new ClassWeek());

        for (int week = 0; week < classWeekList.size() - 1; week++) {
            int startWeek = week;

            while (classWeekList.get(week).isEqual(classWeekList.get(week + 1))) {
                week++;
            }

            LocalDate startDate = termRange.getStart().plusDays(startWeek * 7);
            LocalDate endDate = termRange.getStart().plusDays(week * 7 + 6);

            SheetData sheetData = new SheetData(new DateRange(startDate, endDate), classWeekList.get(startWeek));

            if (sheetData.isEmpty()) {
                continue;
            }

            workbookData.add(sheetData);
        }

        return workbookData;
    }

    private void readPeriodList() {
        Sheet sheet = workbook.getSheetAt(0);

        for (int rowNum = 10; sheet.getRow(rowNum) != null; rowNum++) {
            Row row = sheet.getRow(rowNum);
            Cell weekdayCell = row.getCell(0);

            if (weekdayCell == null) {
                return;
            }

            String weekdayString = weekdayCell.getStringCellValue().trim();

            if (weekdayString.isEmpty()) {
                return;
            }

            Weekday weekday = Weekday.getWeekday(Integer.parseInt(weekdayString));
            String subject = row.getCell(4).getStringCellValue().trim();
            String classroom = row.getCell(9).getStringCellValue().trim();

            String classPeriodRangeString = row.getCell(8).getStringCellValue().trim();
            ClassPeriodRange classPeriodRange = new ClassPeriodRange(classPeriodRangeString);

            String dateRangeString = row.getCell(10).getStringCellValue().trim();
            DateRange dateRange = new DateRange(dateRangeString);

            SubjectPeriod subjectPeriod = new SubjectPeriod(subject, classroom, weekday, classPeriodRange, dateRange);
            subjectPeriodList.add(subjectPeriod);

            if (dateRange.getStart().isBefore(termRange.getStart())) {
                termRange.setStart(dateRange.getStart());
            }

            if (dateRange.getEnd().isAfter(termRange.getEnd())) {
                termRange.setEnd(dateRange.getEnd());
            }
        }

        termRange.setStart(LocalDateUtils.getStartDayOfWeek(termRange.getStart()));
        termRange.setEnd(LocalDateUtils.getEndDayOfWeek(termRange.getEnd()));
    }

    public void read(InputStream inputStream) throws IOException {
        pfs = new POIFSFileSystem(inputStream);
        this.workbook = new HSSFWorkbook(pfs);
    }

    @Override
    public void close() throws Exception {
        workbook.close();
        pfs.close();
    }
}
