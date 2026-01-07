package com.railway.util;

import com.railway.model.Ticket;
import com.railway.model.Train;
import java.io.*;
import java.time.format.DateTimeFormatter;

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
        pdf.append("/MediaBox [0 0 400 600]\n"); // Small ticket size
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
        
        content.append("BT\n");
        content.append("/F1 14 Tf\n");
        content.append("50 550 Td\n");
        content.append("(TAMIL NADU RAILWAY) Tj\n");
        content.append("0 -20 Td\n");
        content.append("/F2 10 Tf\n");
        content.append("(Electronic Ticket) Tj\n");
        
        content.append("0 -30 Td\n");
        content.append("/F1 10 Tf\n");
        content.append("(PNR: TN").append(String.format("%010d", ticket.getTicketId())).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Train: ").append(train.getTrainId()).append(" / ").append(train.getTrainName()).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(From: ").append(train.getSource()).append(" To: ").append(train.getDestination()).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Departure: ").append(train.getDepartureTime()).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Arrival: ").append(train.getArrivalTime()).append(") Tj\n");
        
        content.append("0 -30 Td\n");
        content.append("(Passenger: ").append(ticket.getPassengerName()).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Seat: ").append(ticket.getSeatNumber()).append(" | Class: GENERAL) Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Phone: ").append(ticket.getPassengerPhone()).append(") Tj\n");
        
        content.append("0 -30 Td\n");
        content.append("(Fare: Rs. ").append(String.format("%.2f", ticket.getFare())).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Date: ").append(ticket.getBookingTime().toLocalDate().toString()).append(") Tj\n");
        
        content.append("0 -20 Td\n");
        content.append("(Status: ").append(ticket.getStatus()).append(") Tj\n");
        
        content.append("0 -40 Td\n");
        content.append("/F2 8 Tf\n");
        content.append("(Carry valid ID proof during journey) Tj\n");
        
        content.append("0 -15 Td\n");
        content.append("(Report 30 min before departure) Tj\n");
        
        content.append("0 -30 Td\n");
        content.append("/F1 10 Tf\n");
        content.append("(*** HAPPY JOURNEY ***) Tj\n");
        
        content.append("ET\n");
        
        return content.toString();
    }
}