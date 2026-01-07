package com.railway.util;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.railway.model.Ticket;
import com.railway.model.Train;

public class PDFTicketGenerator {
    
    // ATVM ticket size: 4" x 2.5" at 72 DPI = 288 x 180 points
    private static final float TICKET_WIDTH = 288f;
    private static final float TICKET_HEIGHT = 180f;
    
    // Colors
    private static final Color HEADER_COLOR = new Color(25, 50, 120); // Dark blue
    private static final Color ACCENT_COLOR = new Color(255, 100, 0); // Orange
    private static final Color BACKGROUND_COLOR = new Color(250, 248, 240); // Cream
    private static final Color TEXT_PRIMARY = Color.BLACK;
    private static final Color TEXT_SECONDARY = new Color(80, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(0, 150, 0);
    private static final Color ERROR_COLOR = new Color(200, 0, 0);
    
    public static boolean generateTicketPDF(Ticket ticket, Train train, String outputPath) {
        try {
            // Create directory if it doesn't exist
            File file = new File(outputPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Create PDF document
            try (PDDocument document = new PDDocument()) {
                // Create custom page size for ATVM ticket
                PDRectangle pageSize = new PDRectangle(TICKET_WIDTH, TICKET_HEIGHT);
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                
                // Create content stream
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    drawTicketDesign(contentStream, ticket, train);
                }
                
                // Save the document
                document.save(outputPath);
                
                System.out.println("[INFO] PDF ticket generated: " + outputPath);
                System.out.println("[INFO] File size: " + file.length() + " bytes");
                
                return true;
            }
            
        } catch (IOException e) {
            System.err.println("Error generating PDF ticket: " + e.getMessage());
            return false;
        }
    }
    
    private static void drawTicketDesign(PDPageContentStream contentStream, Ticket ticket, Train train) throws IOException {
        // Fill background
        contentStream.setNonStrokingColor(BACKGROUND_COLOR);
        contentStream.addRect(0, 0, TICKET_WIDTH, TICKET_HEIGHT);
        contentStream.fill();
        
        // Draw header section
        drawHeader(contentStream, ticket);
        
        // Draw main content
        drawMainContent(contentStream, ticket, train);
        
        // Draw footer
        drawFooter(contentStream);
        
        // Draw borders and decorative elements
        drawBorders(contentStream);
    }
    
    private static void drawHeader(PDPageContentStream contentStream, Ticket ticket) throws IOException {
        // Header background
        contentStream.setNonStrokingColor(HEADER_COLOR);
        contentStream.addRect(0, TICKET_HEIGHT - 35, TICKET_WIDTH, 35);
        contentStream.fill();
        
        // Orange accent strip
        contentStream.setNonStrokingColor(ACCENT_COLOR);
        contentStream.addRect(0, TICKET_HEIGHT - 40, TICKET_WIDTH, 5);
        contentStream.fill();
        
        // Header text
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(10, TICKET_HEIGHT - 20);
        contentStream.showText("TAMIL NADU RAILWAY");
        
        // PNR number (right aligned)
        String pnr = "PNR: TN" + String.format("%010d", ticket.getTicketId());
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText(pnr);
        
        // E-TICKET label
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
        contentStream.newLineAtOffset(-150, -12);
        contentStream.showText("E-TICKET");
        
        // UTS number
        String uts = "UTS: TN" + String.format("%06d", ticket.getTicketId());
        contentStream.newLineAtOffset(180, 0);
        contentStream.showText(uts);
        
        contentStream.endText();
    }
    
    private static void drawMainContent(PDPageContentStream contentStream, Ticket ticket, Train train) throws IOException {
        float yPosition = TICKET_HEIGHT - 55;
        
        // Route section - prominent
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_PRIMARY);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(10, yPosition);
        String route = train.getSource().toUpperCase() + " → " + train.getDestination().toUpperCase();
        contentStream.showText(route);
        contentStream.endText();
        
        yPosition -= 20;
        
        // Train details
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.newLineAtOffset(10, yPosition);
        String trainInfo = "TRAIN: " + train.getTrainId() + " | " + train.getTrainName().toUpperCase();
        contentStream.showText(trainInfo);
        contentStream.endText();
        
        yPosition -= 15;
        
        // Passenger name
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_PRIMARY);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
        contentStream.newLineAtOffset(10, yPosition);
        contentStream.showText("PASSENGER: " + ticket.getPassengerName().toUpperCase());
        contentStream.endText();
        
        yPosition -= 15;
        
        // Three column layout: Seat, Class, Quota
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        
        // Column 1: Seat
        contentStream.newLineAtOffset(10, yPosition);
        contentStream.showText("SEAT: " + ticket.getSeatNumber());
        
        // Column 2: Class
        contentStream.newLineAtOffset(70, 0);
        contentStream.showText("CLASS: GENERAL");
        
        // Column 3: Quota
        contentStream.newLineAtOffset(70, 0);
        contentStream.showText("QUOTA: GN");
        
        contentStream.endText();
        
        yPosition -= 15;
        
        // Journey details row
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
        
        // Fare (highlighted in red)
        contentStream.setNonStrokingColor(ERROR_COLOR);
        contentStream.newLineAtOffset(10, yPosition);
        contentStream.showText("FARE: Rs. " + String.format("%.2f", ticket.getFare()));
        
        // Departure time
        contentStream.setNonStrokingColor(TEXT_PRIMARY);
        contentStream.newLineAtOffset(70, 0);
        contentStream.showText("DEP: " + train.getDepartureTime());
        
        // Arrival time
        contentStream.newLineAtOffset(70, 0);
        contentStream.showText("ARR: " + train.getArrivalTime());
        
        contentStream.endText();
        
        yPosition -= 15;
        
        // Date and Status row
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        
        // Date
        contentStream.newLineAtOffset(10, yPosition);
        String date = ticket.getBookingTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        contentStream.showText("DATE: " + date);
        
        // Status (colored based on status)
        if ("BOOKED".equals(ticket.getStatus())) {
            contentStream.setNonStrokingColor(SUCCESS_COLOR);
        } else {
            contentStream.setNonStrokingColor(ERROR_COLOR);
        }
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("STATUS: " + ticket.getStatus());
        
        contentStream.endText();
        
        yPosition -= 12;
        
        // Transaction ID
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 7);
        contentStream.newLineAtOffset(10, yPosition);
        String txnId = "TXN ID: TN" + ticket.getBookingTime().toLocalDate().toString().replace("-", "") + 
                      String.format("%06d", ticket.getTicketId());
        contentStream.showText(txnId);
        contentStream.endText();
        
        yPosition -= 10;
        
        // Contact information
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 6);
        contentStream.newLineAtOffset(10, yPosition);
        String contact = "CONTACT: " + ticket.getPassengerPhone() + " | EMAIL: " + ticket.getPassengerEmail();
        // Truncate if too long
        if (contact.length() > 50) {
            contact = contact.substring(0, 47) + "...";
        }
        contentStream.showText(contact);
        contentStream.endText();
    }
    
    private static void drawFooter(PDPageContentStream contentStream) throws IOException {
        float yPosition = 25;
        
        // Instructions
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 6);
        contentStream.newLineAtOffset(10, yPosition);
        contentStream.showText("• Carry valid photo ID • Report 30 min before departure • Keep ticket safe");
        contentStream.endText();
        
        yPosition -= 12;
        
        // Happy journey message
        contentStream.beginText();
        contentStream.setNonStrokingColor(HEADER_COLOR);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
        contentStream.newLineAtOffset(35, yPosition);
        contentStream.showText("★ ★ ★  HAPPY JOURNEY - TAMIL NADU RAILWAY  ★ ★ ★");
        contentStream.endText();
    }
    
    private static void drawBorders(PDPageContentStream contentStream) throws IOException {
        // Main border
        contentStream.setStrokingColor(TEXT_SECONDARY);
        contentStream.setLineWidth(1.5f);
        contentStream.addRect(0, 0, TICKET_WIDTH, TICKET_HEIGHT);
        contentStream.stroke();
        
        // Decorative lines
        contentStream.setStrokingColor(Color.LIGHT_GRAY);
        contentStream.setLineWidth(0.5f);
        
        // Line under header
        contentStream.moveTo(10, TICKET_HEIGHT - 45);
        contentStream.lineTo(TICKET_WIDTH - 10, TICKET_HEIGHT - 45);
        contentStream.stroke();
        
        // Line above footer
        contentStream.moveTo(10, 40);
        contentStream.lineTo(TICKET_WIDTH - 10, 40);
        contentStream.stroke();
        
        // Side accent line
        contentStream.setStrokingColor(ACCENT_COLOR);
        contentStream.setLineWidth(3f);
        contentStream.moveTo(5, 5);
        contentStream.lineTo(5, TICKET_HEIGHT - 5);
        contentStream.stroke();
    }
}