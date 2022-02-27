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
    public static void main(String[] args) {
        SpringApplication.run(TrainManagementApplication.class, args);
    }
}

