package utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class PdfGenerator {

    public static String generateCertificate(String studentName, String courseName) {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float y = 700;

            // Title
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 26);
            content.newLineAtOffset(120, y);
            content.showText("Certificate of Completion");
            content.endText();

            y -= 60;

            // Subtitle
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 18);
            content.newLineAtOffset(80, y);
            content.showText("This certificate is awarded to:");
            content.endText();

            y -= 40;

            // Student Name
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 22);
            content.newLineAtOffset(80, y);
            content.showText(studentName);
            content.endText();

            y -= 50;

            // Course Name
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 18);
            content.newLineAtOffset(80, y);
            content.showText("For completing the course: " + courseName);
            content.endText();

            y -= 40;

            // Date
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 16);
            content.newLineAtOffset(80, y);
            content.showText("Date: " + LocalDate.now());
            content.endText();

            content.close();

            String fileName = "Certificate_" + studentName.replace(" ", "_")+"_"+ courseName.replace(" ", "_") + ".pdf";
            
            File outFile = new File(fileName);
            document.save(outFile);

            return outFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
