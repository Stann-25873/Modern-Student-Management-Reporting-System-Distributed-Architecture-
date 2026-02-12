package com.client.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.server.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class StudentReportExporter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // 1. PDF EXPORT (Landscape orientation for better data visibility)
    public static void exportToPDF(List<Student> students, String filePath) throws Exception {
        Document document = new Document(PageSize.A4.rotate()); 
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("STUDENT REGISTRATION SYSTEM - ENROLLMENT REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Report Date: " + DATE_FORMAT.format(new Date())));
        document.add(Chunk.NEWLINE);

        // 8 Columns: ID, UniqueID, Names, Email, Mode, Date, Dept, Program
        PdfPTable table = new PdfPTable(new float[]{1, 2, 3, 4, 2, 2, 3, 3}); 
        table.setWidthPercentage(100);

        String[] headers = {"ID", "Unique ID", "Full Name", "Email", "Mode", "Enrollment", "Department", "Program"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (Student s : students) {
            table.addCell(String.valueOf(s.getId()));
            table.addCell(s.getStudentUniqueId() != null ? s.getStudentUniqueId() : "");
            table.addCell(s.getFirstName() + " " + s.getLastName());
            table.addCell(s.getEmail());
            table.addCell(s.getStudyMode());
            table.addCell(s.getEnrollmentDate() != null ? DATE_FORMAT.format(s.getEnrollmentDate()) : "");
            table.addCell(s.getDepartment() != null ? s.getDepartment().getName() : "N/A");
            table.addCell(s.getProgram() != null ? s.getProgram().getName() : "N/A");
        }

        document.add(table);
        document.close();
    }

    // 2. EXCEL EXPORT
    public static void exportToExcel(List<Student> students, String filePath) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Enrolled Students");
            
            String[] headers = {"ID", "Student Unique ID", "First Name", "Last Name", "Email", "Study Mode", "Enrollment Date", "Department", "Program"};
            
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getStudentUniqueId());
                row.createCell(2).setCellValue(s.getFirstName());
                row.createCell(3).setCellValue(s.getLastName());
                row.createCell(4).setCellValue(s.getEmail());
                row.createCell(5).setCellValue(s.getStudyMode());
                row.createCell(6).setCellValue(s.getEnrollmentDate() != null ? DATE_FORMAT.format(s.getEnrollmentDate()) : "");
                row.createCell(7).setCellValue(s.getDepartment() != null ? s.getDepartment().getName() : "N/A");
                row.createCell(8).setCellValue(s.getProgram() != null ? s.getProgram().getName() : "N/A");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    // 3. CSV EXPORT (Standard Java IO with buffering)
    public static void exportToCSV(List<Student> students, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            // Write CSV Header
            writer.println("ID,Unique ID,FirstName,LastName,Email,StudyMode,EnrollmentDate,Department,Program");

            // Write Data rows with quote-encapsulation for text fields
            for (Student s : students) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        s.getId(),
                        s.getStudentUniqueId() != null ? s.getStudentUniqueId() : "",
                        s.getFirstName(),
                        s.getLastName(),
                        s.getEmail(),
                        s.getStudyMode(),
                        s.getEnrollmentDate() != null ? DATE_FORMAT.format(s.getEnrollmentDate()) : "",
                        s.getDepartment() != null ? s.getDepartment().getName() : "N/A",
                        s.getProgram() != null ? s.getProgram().getName() : "N/A"
                );
            }
        }
    }
}