package com.hraczynski.trains.images;

import com.hraczynski.trains.train.TrainType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ImagesPaths {
    NORMAL(TrainType.NORMAL, "/static/images/normal.svg"),
    FAST(TrainType.FAST, "/static/images/fast.svg"),
    PREMIUM(TrainType.PREMIUM, "/static/images/premium.svg"),
    ECONOMIC(TrainType.ECONOMIC, "/static/images/economic.svg");

    private final TrainType trainType;
    private final String path;

    public static ImagesPaths getImagePathByType(TrainType trainType) {
        return Arrays.stream(values())
                .filter(s -> s.trainType == trainType)
                .findFirst()
                .orElse(null);
    }
}
