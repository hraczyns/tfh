package com.hraczynski.trains.images;

import com.hraczynski.trains.exceptions.definitions.CannotReceiveImagesException;
import com.hraczynski.trains.train.TrainType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class ImagesProcessor {
    public byte[] getParsedTrainImageByType(TrainType type) {
        try {
            log.info("Parsing train image for type {}", type);
            ImagesPaths imagePathByType = ImagesPaths.getImagePathByType(type);
            if (imagePathByType == null) {
                log.error("Cannot find image related to train type");
                throw new CannotReceiveImagesException("Cannot find image related to train type");
            }
            String path = imagePathByType.getPath();
            URL res = getClass().getResource(path);
            if (res == null) {
                log.error("Cannot find image related to train type");
                throw new CannotReceiveImagesException("Cannot find image related to train type");
            }
            File file = Paths.get(res.toURI()).toFile();
            if (file.exists()) {
                return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            } else {
                log.error("Input stream was not received");
                throw new CannotReceiveImagesException("Input stream was not received, possible cause: cannot find image related to input train id.");
            }
        } catch (Exception e) {
            if (e instanceof IOException) {
                log.error("Error occurred during parsing image");
            }
            throw new CannotReceiveImagesException(e.getMessage());
        }
    }


}

