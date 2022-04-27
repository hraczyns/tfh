package com.hraczynski.trains.images;

import com.hraczynski.trains.train.TrainType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ImagesPaths {
    NORMAL(TrainType.NORMAL, "/static/images/normal_alt.png"),
    FAST(TrainType.FAST, "/static/images/fast_alt.png"),
    PREMIUM(TrainType.PREMIUM, "/static/images/premium_alt.png"),
    ECONOMIC(TrainType.ECONOMIC, "/static/images/economic_alt.png");

    private final TrainType trainType;
    private final String path;

    public static ImagesPaths getImagePathByType(TrainType trainType) {
        return Arrays.stream(values())
                .filter(s -> s.trainType == trainType)
                .findFirst()
                .orElse(null);
    }
}
