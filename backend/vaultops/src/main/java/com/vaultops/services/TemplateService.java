package com.vaultops.services;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;

@Service
public class TemplateService {

    public ByteArrayResource generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Asset Import");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle requiredStyle = createRequiredFieldStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Name*", "Type*", "Location*", "Assignment*", "Serial Number*",
                    "Condition Status*", "Usage Status*", "Assigned To",
                    "Purchase Date", "Purchase Price"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);

                if (headers[i].endsWith("*")) {
                    cell.setCellStyle(requiredStyle);
                } else {
                    cell.setCellStyle(headerStyle);
                }
            }

            addSampleData(sheet, dateStyle);
            addDataValidations(sheet);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            sheet.createFreezePane(0, 1);

            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel template", e);
        }
    }

    private void addSampleData(Sheet sheet, CellStyle dateStyle) {
        Row sample1 = sheet.createRow(1);
        sample1.createCell(0).setCellValue("Dell Laptop XPS 15");
        sample1.createCell(1).setCellValue("Laptop");
        sample1.createCell(2).setCellValue("Office A - Floor 3");
        sample1.createCell(3).setCellValue("ASSIGNED");
        sample1.createCell(4).setCellValue("DELLXPS123456");
        sample1.createCell(5).setCellValue("EXCELLENT");
        sample1.createCell(6).setCellValue("IN_USE");
        sample1.createCell(7).setCellValue("john.doe@company.com");

        Cell date1 = sample1.createCell(8);
        date1.setCellValue(LocalDate.of(2023, 1, 15));
        date1.setCellStyle(dateStyle);

        sample1.createCell(9).setCellValue(1200.00);

        Row sample2 = sheet.createRow(2);
        sample2.createCell(0).setCellValue("HP Monitor 27inch");
        sample2.createCell(1).setCellValue("Monitor");
        sample2.createCell(2).setCellValue("Office B - Floor 2");
        sample2.createCell(3).setCellValue("UNASSIGNED");
        sample2.createCell(4).setCellValue("HPMON789012");
        sample2.createCell(5).setCellValue("GOOD");
        sample2.createCell(6).setCellValue("IN_STORAGE");
        sample2.createCell(7).setCellValue("");

        Cell date2 = sample2.createCell(8);
        date2.setCellValue(LocalDate.of(2022, 6, 10));
        date2.setCellStyle(dateStyle);

        sample2.createCell(9).setCellValue(350.00);
    }

    private void addDataValidations(Sheet sheet) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        String[] assignments = {"ASSIGNED", "UNASSIGNED"};
        CellRangeAddressList assignmentRange = new CellRangeAddressList(3, 1000, 3, 3);
        DataValidationConstraint assignmentConstraint =
                validationHelper.createExplicitListConstraint(assignments);
        DataValidation assignmentValidation =
                validationHelper.createValidation(assignmentConstraint, assignmentRange);
        assignmentValidation.setShowErrorBox(true);
        assignmentValidation.createErrorBox("Invalid Value",
                "Please select a valid assignment status: ASSIGNED or UNASSIGNED");
        sheet.addValidationData(assignmentValidation);

        String[] conditions = {"EXCELLENT", "GOOD", "FAIR", "BAD", "DAMAGED"};
        CellRangeAddressList conditionRange = new CellRangeAddressList(3, 1000, 5, 5);
        DataValidationConstraint conditionConstraint =
                validationHelper.createExplicitListConstraint(conditions);
        DataValidation conditionValidation =
                validationHelper.createValidation(conditionConstraint, conditionRange);
        conditionValidation.setShowErrorBox(true);
        conditionValidation.createErrorBox("Invalid Value",
                "Please select a valid condition status");
        sheet.addValidationData(conditionValidation);

        String[] usageTypes = {"IN_USE", "IN_STORAGE", "IN_REPAIRS"};
        CellRangeAddressList usageRange = new CellRangeAddressList(3, 1000, 6, 6);
        DataValidationConstraint usageConstraint =
                validationHelper.createExplicitListConstraint(usageTypes);
        DataValidation usageValidation =
                validationHelper.createValidation(usageConstraint, usageRange);
        usageValidation.setShowErrorBox(true);
        usageValidation.createErrorBox("Invalid Value",
                "Please select a valid usage status");
        sheet.addValidationData(usageValidation);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createRequiredFieldStyle(Workbook workbook) {
        CellStyle style = createHeaderStyle(workbook);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }

    public ByteArrayResource generateCsvTemplate() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            String[] header = {
                    "Name*", "Type*", "Location*", "Assignment*", "Serial Number*",
                    "Condition Status*", "Usage Status*", "Assigned To",
                    "Purchase Date", "Purchase Price"
            };
            writer.writeNext(header);

            writer.writeNext(new String[]{
                    "Dell Laptop XPS 15",
                    "Laptop",
                    "Office A - Floor 3",
                    "ASSIGNED",
                    "DELLXPS123456",
                    "EXCELLENT",
                    "IN_USE",
                    "john.doe@company.com",
                    "2023-01-15",
                    "1200.00"
            });

            writer.writeNext(new String[]{
                    "HP Monitor 27inch",
                    "Monitor",
                    "Office B - Floor 2",
                    "UNASSIGNED",
                    "HPMON789012",
                    "GOOD",
                    "IN_STORAGE",
                    "",
                    "2022-06-10",
                    "350.00"
            });

            writer.flush();
            return new ByteArrayResource(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV template", e);
        }
    }
}