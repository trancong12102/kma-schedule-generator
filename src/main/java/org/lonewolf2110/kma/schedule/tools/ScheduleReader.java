package org.lonewolf2110.kma.schedule.tools;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.lonewolf2110.kma.schedule.enums.Weekday;
import org.lonewolf2110.kma.schedule.models.*;
import org.lonewolf2110.kma.schedule.utils.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ScheduleReader implements IReader, AutoCloseable {
    private static final int WEEKDAY_COL_NUMBER = 0;
    private static final int SUBJECT_NAME_COL_NUMBER = 4;
    private static final int CLASS_PERIOD_RANGE_COL_NUMBER = 8;
    private static final int CLASSROOM_COL_NUMBER = 9;
    private static final int DATE_RANGE_COL_NUMBER = 10;

    private Workbook workbook;
    private List<SubjectStage> subjectStageList;
    private DateRange termRange;
    private POIFSFileSystem pfs;

    public ScheduleReader() {
        this.subjectStageList = new ArrayList<>();
        termRange = new DateRange(LocalDate.of(
                3000, 1, 1),
                LocalDate.of(2000, 1, 1)
        );

    }

    @Override
    public List<SheetData> parseWorkbookData() {
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

    @Override
    public void read(InputStream inputStream) throws IOException {
        pfs = new POIFSFileSystem(inputStream);
        this.workbook = new HSSFWorkbook(pfs);
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

            for (SubjectStage SubjectStage : subjectStageList) {
                DateRange periodRange = SubjectStage.getDateRange();
                LocalDate SubjectStageStart = DateUtils.getStartDayOfWeek(periodRange.getStart());
                LocalDate SubjectStageEnd = DateUtils.getEndDayOfWeek(periodRange.getEnd());

                if (DateUtils.isBetween(
                        new DateRange(SubjectStageStart, SubjectStageEnd),
                        new DateRange(weekStartDate, weekEndDate)
                )) {
                    classWeek.add(SubjectStage);
                }
            }

            classWeek.sort();
            classWeekList.add(classWeek);
        }

        return classWeekList;
    }

    private void readPeriodList() {
        Sheet sheet = workbook.getSheetAt(0);

        for (int rowNum = 10; sheet.getRow(rowNum) != null; rowNum++) {
            Row row = sheet.getRow(rowNum);
            Cell weekdayCell = row.getCell(WEEKDAY_COL_NUMBER);

            if (weekdayCell == null) {
                return;
            }

            String weekdayString = weekdayCell.getStringCellValue().trim();

            if (weekdayString.isEmpty()) {
                return;
            }

            Weekday weekday = Weekday.getWeekday(Integer.parseInt(weekdayString));
            String subject = row.getCell(SUBJECT_NAME_COL_NUMBER).getStringCellValue().trim();
            String classroom = row.getCell(CLASSROOM_COL_NUMBER).getStringCellValue().trim();

            String periodRangeString = row.getCell(CLASS_PERIOD_RANGE_COL_NUMBER).getStringCellValue().trim();
            PeriodRange periodRange = new PeriodRange(periodRangeString);

            String dateRangeString = row.getCell(DATE_RANGE_COL_NUMBER).getStringCellValue().trim();
            DateRange dateRange = DateUtils.parseDateRange(dateRangeString);

            SubjectStage SubjectStage = new SubjectStage(subject, classroom, weekday, periodRange, dateRange);
            subjectStageList.add(SubjectStage);

            if (dateRange.getStart().isBefore(termRange.getStart())) {
                termRange.setStart(dateRange.getStart());
            }

            if (dateRange.getEnd().isAfter(termRange.getEnd())) {
                termRange.setEnd(dateRange.getEnd());
            }
        }

        termRange.setStart(DateUtils.getStartDayOfWeek(termRange.getStart()));
        termRange.setEnd(DateUtils.getEndDayOfWeek(termRange.getEnd()));
    }

    @Override
    public void close() throws Exception {
        workbook.close();
        pfs.close();
    }
}
