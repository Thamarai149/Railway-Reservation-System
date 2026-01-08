package com.railway.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

import com.railway.model.Ticket;
import com.railway.model.Train;

public class JPGTicketGenerator {

    private static final int WIDTH = 324;   // Small size width
    private static final int HEIGHT = 216;  // Small size height
    
    // IRCTC Brand Colors
    private static final Color IRCTC_BLUE = new Color(0, 51, 102);
    private static final Color IRCTC_ORANGE = new Color(255, 102, 0);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;

    public static boolean generateTicketJPG(Ticket ticket, Train train, String filePath) {
        try {
            // Create buffered image
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Enable anti-aliasing for better text quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Draw background and IRCTC branding
            drawBackground(g2d);
            drawIRCTCHeader(g2d);
            drawTicketContent(g2d, ticket, train);
            drawFooter(g2d);
            
            g2d.dispose();

            // Save as JPG
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            
            ImageIO.write(image, "jpg", file);
            System.out.println("JPG Ticket Generated Successfully: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Failed to generate JPG ticket: " + e.getMessage());
            return false;
        }
    }
    
    private static void drawBackground(Graphics2D g2d) {
        // Light gray background
        g2d.setColor(LIGHT_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // White main content area
        g2d.setColor(WHITE);
        g2d.fillRect(5, 5, WIDTH - 10, HEIGHT - 10);
        
        // IRCTC watermark in background (very light)
        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String watermark = "IRCTC";
        int x = (WIDTH - fm.stringWidth(watermark)) / 2;
        int y = HEIGHT / 2 + fm.getAscent() / 2;
        g2d.drawString(watermark, x, y);
    }
    
    private static void drawIRCTCHeader(Graphics2D g2d) {
        // IRCTC Blue header bar
        g2d.setColor(IRCTC_BLUE);
        g2d.fillRect(5, 5, WIDTH - 10, 40);
        
        // IRCTC Logo text
        g2d.setColor(WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("IRCTC", 15, 28);
        
        // Orange accent
        g2d.setColor(IRCTC_ORANGE);
        g2d.fillRect(60, 15, 3, 15);
        
        // Indian Railways text
        g2d.setColor(WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("INDIAN RAILWAYS", 70, 28);
        
        // E-Ticket label
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("E-TICKET", WIDTH - 80, 28);
        
        // Decorative orange line
        g2d.setColor(IRCTC_ORANGE);
        g2d.fillRect(5, 43, WIDTH - 10, 2);
    }
    
    private static void drawTicketContent(Graphics2D g2d, Ticket ticket, Train train) {
        g2d.setColor(BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        int leftCol = 20;
        int rightCol = 170;
        int startY = 65;
        int lineHeight = 15;
        
        // Left column - Ticket info
        g2d.drawString("PNR Number : " + ticket.getTicketId(), leftCol, startY);
        g2d.drawString("Passenger  : " + ticket.getPassengerName(), leftCol, startY + lineHeight);
        g2d.drawString("Seat No    : " + ticket.getSeatNumber(), leftCol, startY + 2 * lineHeight);
        g2d.drawString("Status     : " + ticket.getStatus(), leftCol, startY + 3 * lineHeight);
        
        // Train details header
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("Train Details", leftCol, startY + 5 * lineHeight);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Train No   : " + train.getTrainId(), leftCol, startY + 6 * lineHeight);
        g2d.drawString("Train Name : " + train.getTrainName(), leftCol, startY + 7 * lineHeight);
        g2d.drawString("From       : " + train.getSource(), leftCol, startY + 8 * lineHeight);
        g2d.drawString("To         : " + train.getDestination(), leftCol, startY + 9 * lineHeight);
        
        // Right column - Date and fare
        g2d.drawString("Date : " + ticket.getBookingTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), 
                      rightCol, startY + 6 * lineHeight);
        g2d.drawString("Fare : Rs. " + ticket.getFare(), rightCol, startY + 7 * lineHeight);
    }
    
    private static void drawFooter(Graphics2D g2d) {
        // Footer background
        g2d.setColor(LIGHT_GRAY);
        g2d.fillRect(5, HEIGHT - 30, WIDTH - 10, 25);
        
        // Footer text
        g2d.setColor(IRCTC_BLUE);
        g2d.setFont(new Font("Arial", Font.ITALIC, 9));
        g2d.drawString("Please carry a valid photo ID. Safe Journey! - IRCTC", 15, HEIGHT - 15);
        
        // Small IRCTC logo in footer
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("IRCTC", WIDTH - 50, HEIGHT - 15);
    }
}