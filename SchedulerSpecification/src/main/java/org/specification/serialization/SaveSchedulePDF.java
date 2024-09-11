package org.specification.serialization;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.specification.classes.TimeSlot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used to save a schedule to a PDF file
 */
public class SaveSchedulePDF {
    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 20;
    private static final int FONT_SIZE = 12;
    private static final int MAX_LINE_LENGTH = 130;

    public static void createTimeSlotTablePDF(List<TimeSlot> timeSlots, String fileName) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = createLandscapePage(document);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);

            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            for (TimeSlot slot : timeSlots) {
                if (yPosition < MARGIN + ROW_HEIGHT) {
                    contentStream.close();
                    page = createLandscapePage(document);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                    yPosition = page.getMediaBox().getHeight() - MARGIN;
                }

                String tabDelimitedRow = Stream.of(
                        slot.getName(),
                        slot.getDayOfWeek().toString(),
                        slot.getStartTime().toString(),
                        slot.getEndTime().toString(),
                        slot.getStartDate().toString(),
                        slot.getEndDate().toString(),
                        slot.getPlace().getName(),
                        formatAdditional(slot.getPlace().getAdditional()),
                        formatAdditional(slot.getAdditional())
                ).collect(Collectors.joining("    "));

                List<String> wrappedLines = wrapText(tabDelimitedRow, MAX_LINE_LENGTH);
                for (String line : wrappedLines) {
                    drawRow(contentStream, yPosition, line);
                    yPosition -= ROW_HEIGHT;
                    if (yPosition < MARGIN) {
                        contentStream.close();
                        page = createLandscapePage(document);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                        yPosition = page.getMediaBox().getHeight() - MARGIN;
                    }
                }
            }

            contentStream.close();
            document.save(fileName);
        } catch (IOException e) {
            System.err.println("Error creating or writing to PDF: " + e.getMessage());
        }
    }

    private static PDPage createLandscapePage(PDDocument document) {
        PDPage page = new PDPage();
        PDRectangle mediaBox = page.getMediaBox();
        page.setMediaBox(new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth()));
        document.addPage(page);
        return page;
    }

    private static void drawRow(PDPageContentStream contentStream, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private static String formatAdditional(Map<String, String> additional) {
        if (additional == null) {
            return "";
        }
        return additional.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining("    "));
    }

    private static List<String> wrapText(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        for (String word : text.split(" ")) {
            if (currentLine.length() + word.length() > maxLineLength) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }
}
