package com.railway.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.railway.model.Ticket;
import com.railway.model.Train;

public class PDFTicketGenerator {
    
    public static boolean generateTicketPDF(Ticket ticket, Train train, String outputPath) {
        try {
            // Create directory if it doesn't exist
            File file = new File(outputPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Generate simple PDF content using basic PDF structure
            String pdfContent = generateSimplePDF(ticket, train);
            
            // Save as PDF file
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(pdfContent.getBytes("ISO-8859-1"));
            }
            
            System.out.println("[INFO] PDF ticket generated: " + outputPath);
            System.out.println("[INFO] File size: " + file.length() + " bytes");
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error generating PDF ticket: " + e.getMessage());
            return false;
        }
    }
    
    private static String generateSimplePDF(Ticket ticket, Train train) {
        StringBuilder pdf = new StringBuilder();
        
        // PDF Header
        pdf.append("%PDF-1.4\n");
        pdf.append("1 0 obj\n");
        pdf.append("<<\n");
        pdf.append("/Type /Catalog\n");
        pdf.append("/Pages 2 0 R\n");
        pdf.append(">>\n");
        pdf.append("endobj\n\n");
        
        // Pages object
        pdf.append("2 0 obj\n");
        pdf.append("<<\n");
        pdf.append("/Type /Pages\n");
        pdf.append("/Kids [3 0 R]\n");
        pdf.append("/Count 1\n");
        pdf.append(">>\n");
        pdf.append("endobj\n\n");
        
        // Page object
        pdf.append("3 0 obj\n");
        pdf.append("<<\n");
        pdf.append("/Type /Page\n");
        pdf.append("/Parent 2 0 R\n");
        pdf.append("/MediaBox [0 0 288 180]\n"); // ATVM ticket size (4" x 2.5" at 72 DPI)
        pdf.append("/Contents 4 0 R\n");
        pdf.append("/Resources <<\n");
        pdf.append("/Font <<\n");
        pdf.append("/F1 5 0 R\n");
        pdf.append("/F2 6 0 R\n");
        pdf.append(">>\n");
        pdf.append(">>\n");
        pdf.append(">>\n");
        pdf.append("endobj\n\n");
        
        // Content stream
        String contentStream = generateContentStream(ticket, train);
        pdf.append("4 0 obj\n");
        pdf.append("<<\n");
        pdf.append("/Length ").append(contentStream.length()).append("\n");
        pdf.append(">>\n");
        pdf.append("stream\n");
        pdf.append(contentStream);
        pdf.append("endstream\n");
        pdf.append("endobj\n\n");
        
        // Font objects
        pdf.append("5 0 obj\n");
        pdf.append("<<\n");
        pdf.append("/Type /Font\n");
        pdf.append("/Subtype /Type1\n");
        pdf.append("/BaseFont /Helvetica-Bold\n");
        pdf.append(">>\n");
        pdf.append("endobj\n\n");
        
        pdf.append("6 0 obj\n");
        pdf.append("<<\n");
        pdf.append("/Type /Font\n");
        pdf.append("/Subtype /Type1\n");
        pdf.append("/BaseFont /Helvetica\n");
        pdf.append(">>\n");
        pdf.append("endobj\n\n");
        
        // Cross-reference table
        pdf.append("xref\n");
        pdf.append("0 7\n");
        pdf.append("0000000000 65535 f \n");
        pdf.append("0000000010 65535 n \n");
        pdf.append("0000000079 65535 n \n");
        pdf.append("0000000173 65535 n \n");
        pdf.append("0000000301 65535 n \n");
        pdf.append("0000000380 65535 n \n");
        pdf.append("0000000484 65535 n \n");
        
        // Trailer
        pdf.append("trailer\n");
        pdf.append("<<\n");
        pdf.append("/Size 7\n");
        pdf.append("/Root 1 0 R\n");
        pdf.append(">>\n");
        pdf.append("startxref\n");
        pdf.append("565\n");
        pdf.append("%%EOF\n");
        
        return pdf.toString();
    }
    
    private static String generateContentStream(Ticket ticket, Train train) {
        StringBuilder content = new StringBuilder();
        
        // Draw modern ticket design
        content.append("q\n"); // Save graphics state
        
        // Main background - Light cream color
        content.append("0.98 0.97 0.94 rg\n"); // Cream background
        content.append("0 0 288 180 re\n");
        content.append("f\n");
        
        // Header section - Dark blue gradient effect
        content.append("0.1 0.2 0.5 rg\n"); // Dark blue
        content.append("0 150 288 30 re\n");
        content.append("f\n");
        
        // Side accent bar - Orange
        content.append("1 0.4 0 rg\n"); // Orange accent
        content.append("0 0 8 180 re\n");
        content.append("f\n");
        
        // Content area background
        content.append("1 1 1 rg\n"); // White
        content.append("8 8 280 142 re\n");
        content.append("f\n");
        
        // Border
        content.append("0.2 0.2 0.2 RG\n"); // Dark gray border
        content.append("1.5 w\n");
        content.append("0 0 288 180 re\n");
        content.append("S\n");
        
        // Decorative lines
        content.append("0.8 0.8 0.8 RG\n"); // Light gray
        content.append("0.5 w\n");
        content.append("15 145 258 0 re\n"); // Under header
        content.append("S\n");
        content.append("15 25 258 0 re\n"); // Above footer
        content.append("S\n");
        
        content.append("Q\n"); // Restore graphics state
        
        content.append("BT\n");
        
        // Header - White text on dark blue
        content.append("1 1 1 rg\n"); // White
        content.append("/F1 11 Tf\n");
        content.append("15 162 Td\n");
        content.append("(TAMIL NADU RAILWAY) Tj\n");
        
        // PNR in header - right aligned
        content.append("120 0 Td\n");
        content.append("/F1 10 Tf\n");
        content.append("(PNR: TN").append(String.format("%010d", ticket.getTicketId())).append(") Tj\n");
        
        // Ticket type
        content.append("-120 -18 Td\n");
        content.append("0.1 0.2 0.5 rg\n"); // Dark blue
        content.append("/F1 9 Tf\n");
        content.append("(E-TICKET) Tj\n");
        
        content.append("180 0 Td\n");
        content.append("/F2 8 Tf\n");
        content.append("(UTS: TN").append(String.format("%06d", ticket.getTicketId())).append(") Tj\n");
        
        // Route - Large and prominent
        content.append("-180 -18 Td\n");
        content.append("0 0 0 rg\n"); // Black
        content.append("/F1 13 Tf\n");
        content.append("(").append(train.getSource().toUpperCase()).append(" → ").append(train.getDestination().toUpperCase()).append(") Tj\n");
        
        // Train details in a box-like format
        content.append("0 -16 Td\n");
        content.append("0.3 0.3 0.3 rg\n"); // Dark gray
        content.append("/F2 7 Tf\n");
        content.append("(TRAIN: ").append(train.getTrainId()).append(" | ").append(train.getTrainName().toUpperCase()).append(") Tj\n");
        
        // Passenger section
        content.append("0 -14 Td\n");
        content.append("0 0 0 rg\n"); // Black
        content.append("/F1 8 Tf\n");
        content.append("(PASSENGER: ").append(ticket.getPassengerName().toUpperCase()).append(") Tj\n");
        
        // Seat and class in columns
        content.append("0 -12 Td\n");
        content.append("/F2 7 Tf\n");
        content.append("(SEAT: ").append(ticket.getSeatNumber()).append(") Tj\n");
        
        content.append("80 0 Td\n");
        content.append("(CLASS: GENERAL) Tj\n");
        
        content.append("80 0 Td\n");
        content.append("(QUOTA: GN) Tj\n");
        
        // Journey details row
        content.append("-160 -14 Td\n");
        content.append("0.8 0 0 rg\n"); // Red for fare
        content.append("/F1 8 Tf\n");
        content.append("(FARE: Rs. ").append(String.format("%.2f", ticket.getFare())).append(") Tj\n");
        
        content.append("0 0 0 rg\n"); // Black
        content.append("70 0 Td\n");
        content.append("(DEP: ").append(train.getDepartureTime()).append(") Tj\n");
        
        content.append("70 0 Td\n");
        content.append("(ARR: ").append(train.getArrivalTime()).append(") Tj\n");
        
        // Date and status row
        content.append("-140 -12 Td\n");
        content.append("/F2 7 Tf\n");
        content.append("(DATE: ").append(ticket.getBookingTime().toLocalDate().toString()).append(") Tj\n");
        
        // Status with background color effect
        if ("BOOKED".equals(ticket.getStatus())) {
            content.append("0 0.7 0 rg\n"); // Green
        } else {
            content.append("0.8 0 0 rg\n"); // Red
        }
        content.append("100 0 Td\n");
        content.append("/F1 8 Tf\n");
        content.append("(STATUS: ").append(ticket.getStatus()).append(") Tj\n");
        
        // Transaction and contact info
        content.append("0 0 0 rg\n"); // Black
        content.append("-100 -12 Td\n");
        content.append("/F2 6 Tf\n");
        content.append("(TXN ID: TN").append(ticket.getBookingTime().toLocalDate().toString().replace("-", "")).append(String.format("%06d", ticket.getTicketId())).append(") Tj\n");
        
        content.append("0 -8 Td\n");
        content.append("(CONTACT: ").append(ticket.getPassengerPhone()).append(" | EMAIL: ").append(ticket.getPassengerEmail()).append(") Tj\n");
        
        // Footer section
        content.append("0.5 0.5 0.5 rg\n"); // Gray
        content.append("0 -16 Td\n");
        content.append("/F2 6 Tf\n");
        content.append("(• Carry valid photo ID • Report 30 min before departure • Keep ticket safe) Tj\n");
        
        // Happy journey - centered and styled
        content.append("0.1 0.4 0.8 rg\n"); // Blue
        content.append("40 -10 Td\n");
        content.append("/F1 8 Tf\n");
        content.append("(★ ★ ★  HAPPY JOURNEY - TAMIL NADU RAILWAY  ★ ★ ★) Tj\n");
        
        content.append("ET\n");
        
        return content.toString();
    }
}