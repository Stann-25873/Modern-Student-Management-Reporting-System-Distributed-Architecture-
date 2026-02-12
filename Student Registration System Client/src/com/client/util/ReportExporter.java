package com.client.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.server.model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Date;

public class ReportExporter {

    // 1. PDF EXPORT (using iText)
    public static void exportToPDF(List<User> users, String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Title Section
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("STUDENT REGISTRATION SYSTEM - USER REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        document.add(new Paragraph("Generated on: " + new Date().toString()));
        document.add(Chunk.NEWLINE);

        // Table Setup: 5 columns
        PdfPTable table = new PdfPTable(new float[]{1, 3, 4, 3, 2}); 
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"ID", "Username", "Email", "Full Name", "Role"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Data Rows - simplified from the complex stream map
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getUsername());
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName() + " " + user.getLastName());
            table.addCell(user.getUserRole());
        }

        document.add(table);
        document.close();
    }

    // 2. EXCEL EXPORT (using Apache POI)
    public static void exportToExcel(List<User> users, String filePath) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("System Users");
            String[] headers = {"User ID", "Username", "Email", "First Name", "Last Name", "System Role"};
            
            // Header Style
            Row headerRow = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            style.setFont(headerFont);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
            }

            // Data Rows
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getFirstName());
                row.createCell(4).setCellValue(user.getLastName());
                row.createCell(5).setCellValue(user.getUserRole());
            }

            // Column Auto-sizing
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    // 3. CSV EXPORT (Standard Java IO)
    public static void exportToCSV(List<User> users, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            // Write CSV Header
            writer.println("ID,Username,Email,First Name,Last Name,Role");

            // Write Data rows
            for (User user : users) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUserRole()
                );
            }
        }
    }
}