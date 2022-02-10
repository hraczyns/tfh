package com.hraczynski.trains.services;

import com.hraczynski.trains.exceptions.definitions.CannotReceiveImagesException;
import com.hraczynski.trains.images.ImagesProcessor;
import com.hraczynski.trains.train.TrainType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Image processor tests")
public class ImageProcessorTest {

    private final ImagesProcessor imagesProcessor = new ImagesProcessor();

    @ParameterizedTest(name = "Get image with value {0}")
    @DisplayName("Get image tests")
    @EnumSource(TrainType.class)
    void getImageTest(TrainType trainType) {
        //when
        byte[] parsedTrainImageByType = imagesProcessor.getParsedTrainImageByType(trainType);

        //then
        assertThat(parsedTrainImageByType).isNotNull();
        assertThat(parsedTrainImageByType).isNotEmpty();
    }

    @Test
    @DisplayName("Does not get image tests")
    void notGetImageTest() {
        //when
        //then
        assertThatCode(() -> imagesProcessor.getParsedTrainImageByType(null)).isInstanceOf(CannotReceiveImagesException.class);
    }


}
