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
    
    // Medium ticket size: 6" x 4" at 72 DPI = 432 x 288 points
    private static final float TICKET_WIDTH = 432f;
    private static final float TICKET_HEIGHT = 288f;
    
    // Modern Color Scheme
    private static final Color PRIMARY_COLOR = new Color(13, 71, 161); // Deep blue
    private static final Color SECONDARY_COLOR = new Color(255, 193, 7); // Amber
    private static final Color ACCENT_COLOR = new Color(76, 175, 80); // Green
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    public static boolean generateTicketPDF(Ticket ticket, Train train, String outputPath) {
        try {
            // Suppress PDFBox font warnings
            java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
            
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
        // Modern gradient-like background
        contentStream.setNonStrokingColor(BACKGROUND_COLOR);
        contentStream.addRect(0, 0, TICKET_WIDTH, TICKET_HEIGHT);
        contentStream.fill();
        
        // Main card background with rounded corners effect
        contentStream.setNonStrokingColor(CARD_COLOR);
        contentStream.addRect(12, 12, TICKET_WIDTH - 24, TICKET_HEIGHT - 24);
        contentStream.fill();
        
        // Draw modern header
        drawModernHeader(contentStream, ticket);
        
        // Draw route section
        drawRouteSection(contentStream, train);
        
        // Draw passenger info card
        drawPassengerCard(contentStream, ticket);
        
        // Draw journey details
        drawJourneyDetails(contentStream, ticket, train);
        
        // Draw footer with QR placeholder
        drawModernFooter(contentStream);
        
        // Draw modern borders and shadows
        drawModernBorders(contentStream);
    }
    
    private static void drawModernHeader(PDPageContentStream contentStream, Ticket ticket) throws IOException {
        // Top accent bar
        contentStream.setNonStrokingColor(SECONDARY_COLOR);
        contentStream.addRect(12, TICKET_HEIGHT - 20, TICKET_WIDTH - 24, 8);
        contentStream.fill();
        
        // Header background with gradient effect
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.addRect(12, TICKET_HEIGHT - 65, TICKET_WIDTH - 24, 45);
        contentStream.fill();
        
        // Railway logo placeholder (circle)
        contentStream.setNonStrokingColor(SECONDARY_COLOR);
        contentStream.addRect(25, TICKET_HEIGHT - 55, 30, 25);
        contentStream.fill();
        
        // Header text
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.newLineAtOffset(70, TICKET_HEIGHT - 35);
        contentStream.showText("TAMIL NADU RAILWAY");
        contentStream.endText();
        
        // Subtitle
        contentStream.beginText();
        contentStream.setNonStrokingColor(SECONDARY_COLOR);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(70, TICKET_HEIGHT - 50);
        contentStream.showText("ELECTRONIC RESERVATION SLIP");
        contentStream.endText();
        
        // PNR in modern style
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(300, TICKET_HEIGHT - 35);
        contentStream.showText("PNR: TN" + String.format("%010d", ticket.getTicketId()));
        contentStream.endText();
        
        // Status badge
        Color statusColor = "BOOKED".equals(ticket.getStatus()) ? SUCCESS_COLOR : DANGER_COLOR;
        contentStream.setNonStrokingColor(statusColor);
        contentStream.addRect(300, TICKET_HEIGHT - 55, 80, 15);
        contentStream.fill();
        
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
        contentStream.newLineAtOffset(315, TICKET_HEIGHT - 50);
        contentStream.showText(ticket.getStatus());
        contentStream.endText();
    }
    
    private static void drawRouteSection(PDPageContentStream contentStream, Train train) throws IOException {
        float yPos = TICKET_HEIGHT - 85;
        
        // Route background card
        contentStream.setNonStrokingColor(new Color(240, 248, 255));
        contentStream.addRect(25, yPos - 35, TICKET_WIDTH - 50, 30);
        contentStream.fill();
        
        // Route text with modern typography
        contentStream.beginText();
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.newLineAtOffset(35, yPos - 15);
        contentStream.showText(train.getSource().toUpperCase());
        contentStream.endText();
        
        // Arrow
        contentStream.beginText();
        contentStream.setNonStrokingColor(SECONDARY_COLOR);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(180, yPos - 15);
        contentStream.showText("→");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.newLineAtOffset(210, yPos - 15);
        contentStream.showText(train.getDestination().toUpperCase());
        contentStream.endText();
        
        // Train info
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(35, yPos - 30);
        contentStream.showText("TRAIN: " + train.getTrainId() + " - " + train.getTrainName().toUpperCase());
        contentStream.endText();
    }
    
    private static void drawPassengerCard(PDPageContentStream contentStream, Ticket ticket) throws IOException {
        float yPos = TICKET_HEIGHT - 135;
        
        // Passenger info card
        contentStream.setNonStrokingColor(new Color(255, 248, 225));
        contentStream.addRect(25, yPos - 40, TICKET_WIDTH - 50, 35);
        contentStream.fill();
        
        // Card border
        contentStream.setStrokingColor(SECONDARY_COLOR);
        contentStream.setLineWidth(1f);
        contentStream.addRect(25, yPos - 40, TICKET_WIDTH - 50, 35);
        contentStream.stroke();
        
        // Passenger icon placeholder
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.addRect(35, yPos - 25, 15, 15);
        contentStream.fill();
        
        // Passenger details
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_PRIMARY);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(60, yPos - 15);
        contentStream.showText("PASSENGER: " + ticket.getPassengerName().toUpperCase());
        contentStream.endText();
        
        // Seat and class info in modern layout
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(60, yPos - 30);
        contentStream.showText("SEAT: " + ticket.getSeatNumber());
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(150, yPos - 30);
        contentStream.showText("CLASS: GENERAL");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(250, yPos - 30);
        contentStream.showText("QUOTA: GN");
        contentStream.endText();
    }
    
    private static void drawJourneyDetails(PDPageContentStream contentStream, Ticket ticket, Train train) throws IOException {
        float yPos = TICKET_HEIGHT - 190;
        
        // Journey details grid
        float[] colWidths = {100, 100, 100, 100};
        String[] headers = {"DEPARTURE", "ARRIVAL", "DATE", "FARE"};
        String[] values = {
            train.getDepartureTime(),
            train.getArrivalTime(),
            ticket.getBookingTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
            "Rs. " + String.format("%.2f", ticket.getFare())
        };
        
        float xPos = 35;
        
        // Draw grid headers
        for (int i = 0; i < headers.length; i++) {
            // Header background
            contentStream.setNonStrokingColor(PRIMARY_COLOR);
            contentStream.addRect(xPos, yPos, colWidths[i] - 5, 20);
            contentStream.fill();
            
            // Header text
            contentStream.beginText();
            contentStream.setNonStrokingColor(Color.WHITE);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
            contentStream.newLineAtOffset(xPos + 5, yPos + 8);
            contentStream.showText(headers[i]);
            contentStream.endText();
            
            xPos += colWidths[i];
        }
        
        // Draw grid values
        xPos = 35;
        for (int i = 0; i < values.length; i++) {
            // Value background
            contentStream.setNonStrokingColor(Color.WHITE);
            contentStream.addRect(xPos, yPos - 20, colWidths[i] - 5, 20);
            contentStream.fill();
            
            // Value border
            contentStream.setStrokingColor(new Color(220, 220, 220));
            contentStream.setLineWidth(0.5f);
            contentStream.addRect(xPos, yPos - 20, colWidths[i] - 5, 20);
            contentStream.stroke();
            
            // Value text
            contentStream.beginText();
            Color textColor = i == 3 ? DANGER_COLOR : TEXT_PRIMARY; // Fare in red
            contentStream.setNonStrokingColor(textColor);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.newLineAtOffset(xPos + 5, yPos - 12);
            contentStream.showText(values[i]);
            contentStream.endText();
            
            xPos += colWidths[i];
        }
        
        // Transaction details
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.newLineAtOffset(35, yPos - 35);
        String txnId = "TXN ID: TN" + ticket.getBookingTime().toLocalDate().toString().replace("-", "") + 
                      String.format("%06d", ticket.getTicketId());
        contentStream.showText(txnId);
        contentStream.endText();
        
        // Contact info
        contentStream.beginText();
        contentStream.newLineAtOffset(35, yPos - 48);
        String contact = "CONTACT: " + ticket.getPassengerPhone() + " | EMAIL: " + ticket.getPassengerEmail();
        if (contact.length() > 70) {
            contact = contact.substring(0, 67) + "...";
        }
        contentStream.showText(contact);
        contentStream.endText();
    }
    
    private static void drawModernFooter(PDPageContentStream contentStream) throws IOException {
        float yPos = 50;
        
        // QR code placeholder
        contentStream.setNonStrokingColor(TEXT_PRIMARY);
        contentStream.addRect(35, yPos - 30, 30, 30);
        contentStream.fill();
        
        // QR label
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 7);
        contentStream.newLineAtOffset(35, yPos - 35);
        contentStream.showText("QR CODE");
        contentStream.endText();
        
        // Instructions with modern styling
        contentStream.beginText();
        contentStream.setNonStrokingColor(TEXT_SECONDARY);
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.newLineAtOffset(80, yPos - 10);
        contentStream.showText("• Carry valid photo ID • Report 30 min before departure");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(80, yPos - 22);
        contentStream.showText("• Keep this ticket safe • Show QR code for verification");
        contentStream.endText();
        
        // Modern footer message
        contentStream.beginText();
        contentStream.setNonStrokingColor(PRIMARY_COLOR);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(120, yPos - 40);
        contentStream.showText("SAFE JOURNEY WITH TAMIL NADU RAILWAY");
        contentStream.endText();
    }
    
    private static void drawModernBorders(PDPageContentStream contentStream) throws IOException {
        // Outer shadow effect
        contentStream.setStrokingColor(new Color(200, 200, 200));
        contentStream.setLineWidth(3f);
        contentStream.addRect(10, 10, TICKET_WIDTH - 20, TICKET_HEIGHT - 20);
        contentStream.stroke();
        
        // Main card border
        contentStream.setStrokingColor(new Color(220, 220, 220));
        contentStream.setLineWidth(1f);
        contentStream.addRect(12, 12, TICKET_WIDTH - 24, TICKET_HEIGHT - 24);
        contentStream.stroke();
        
        // Left accent stripe
        contentStream.setStrokingColor(ACCENT_COLOR);
        contentStream.setLineWidth(6f);
        contentStream.moveTo(12, 12);
        contentStream.lineTo(12, TICKET_HEIGHT - 12);
        contentStream.stroke();
        
        // Corner decorations
        contentStream.setNonStrokingColor(SECONDARY_COLOR);
        // Top left corner
        contentStream.addRect(12, TICKET_HEIGHT - 12, 8, 8);
        contentStream.fill();
        // Top right corner
        contentStream.addRect(TICKET_WIDTH - 20, TICKET_HEIGHT - 12, 8, 8);
        contentStream.fill();
        // Bottom left corner
        contentStream.addRect(12, 4, 8, 8);
        contentStream.fill();
        // Bottom right corner
        contentStream.addRect(TICKET_WIDTH - 20, 4, 8, 8);
        contentStream.fill();
    }
}