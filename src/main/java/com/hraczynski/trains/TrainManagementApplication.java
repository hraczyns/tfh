package com.hraczynski.trains;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

//@SpringBootApplication(scanBasePackages = { "com.hraczynski.trains","com.hraczynski.trains.city","com.hraczynski.trains.country","com.hraczynski.trains.passengers","com.hraczynski.trains.payment","com.hraczynski.trains.city.CityService.java","com.hraczynski.trains.reservations","com.hraczynski.trains.routefinder"})
@SpringBootApplication
public class TrainManagementApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
//        generatePDFFromHTML("HTML-Document.html");
//        generatePDFFromHTML2("HTML-Document.html");
        SpringApplication.run(TrainManagementApplication.class, args);


    }

    private static org.w3c.dom.Document html5ParseDocument(String urlStr) throws IOException {
        org.jsoup.nodes.Document doc;
        doc = Jsoup.parse(new File(urlStr), "UTF-8");
        return new W3CDom().fromJsoup(doc);
    }

    private static void generatePDFFromHTML2(String filename) throws IOException {
        File file = new File(UUID.randomUUID() + ".pdf");
        File outputfile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
        try (OutputStream os = new FileOutputStream(file.getPath())) {
            QRCodeWriter barcodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix =
                    barcodeWriter.encode(RandomStringUtils.randomAlphabetic(400), BarcodeFormat.QR_CODE, 300, 300);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(bufferedImage, "jpg", outputfile);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withW3cDocument(html5ParseDocument(filename), new File(filename).getAbsolutePath());
            builder.toStream(os);
            builder.run();

        } catch (WriterException e) {

        } finally {
            outputfile.delete();
        }


    }

    private static void generatePDFFromHTML(String filename) throws IOException {
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("inputFile", new FileSystemResource(new File(filename)));
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("includeBackgroundGraphics", "");
        headers.add("scaleFactor", "");
        headers.add("Content-Type", "multipart/form-data");
        headers.add("Apikey", "fecdd9a7-8559-4e68-8e0f-8ab708d4d266");
        headers.add("Content-Type", "*");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> exchange = restTemplate.exchange("https://api.cloudmersive.com/convert/html/to/pdf", HttpMethod.POST, entity, byte[].class);
        Files.write(Paths.get("demo.pdf"), Objects.requireNonNull(exchange.getBody()));
        System.out.println("asd");

    }
}

