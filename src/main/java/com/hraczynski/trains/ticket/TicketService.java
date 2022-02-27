package com.hraczynski.trains.ticket;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private static final String HTML_TEMPLATE_TICKET_NAME = "pdf-html-template";

    private final TemplateEngine templateEngine;

    public File prepareTicketPdf(Map<String, Object> params, String identifier) {
        Context context = new Context();
        params.forEach(context::setVariable);
        String qrPath = createQrCode(identifier);
        context.setVariable("qrSource", adjustPathForPdf(qrPath));
        String preparedHtml = templateEngine.process(HTML_TEMPLATE_TICKET_NAME, context);
        String tempFilePath = createTempFile(preparedHtml);
        String pdfFilePath = generatePDFFromHTML(tempFilePath);
        return new File(pdfFilePath);
    }

    private String adjustPathForPdf(String qrPath) {
        return "file:///" + qrPath.replace("\\","/");
    }

    private String createQrCode(String identifier) {
        File outputFile;
        try {
            outputFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
        } catch (IOException e) {
            return "";
        }
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = barcodeWriter.encode(identifier, BarcodeFormat.QR_CODE, 300, 300);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(bufferedImage, "jpg", outputFile);
            return outputFile.getPath();
        } catch (WriterException | IOException e) {
            return "";
        }
    }


    private org.w3c.dom.Document html5ParseDocument(String urlStr) throws IOException {
        org.jsoup.nodes.Document doc;
        doc = Jsoup.parse(new File(urlStr), "UTF-8");
        return new W3CDom().fromJsoup(doc);
    }

    private String generatePDFFromHTML(String filename) {
        File file = new File(UUID.randomUUID() + ".pdf");

        try (OutputStream os = new FileOutputStream(file.getPath())) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withW3cDocument(html5ParseDocument(filename), new File(filename).getAbsolutePath());
            builder.toStream(os);
            builder.run();
            return file.getPath();
        } catch (IOException e) {
            return "";
        }
    }

    private String createTempFile(String preparedHtml) {
        try {
            String name = UUID.randomUUID().toString();
            File file = File.createTempFile(name, ".html");
            Files.writeString(Paths.get(file.getPath()), preparedHtml);
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
