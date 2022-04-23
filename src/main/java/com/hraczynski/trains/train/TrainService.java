package com.hraczynski.trains.train;

import java.util.List;
import java.util.Set;

public interface TrainService {
    Set<Train> findAll();

    Train findById(Long id);

    Train save(TrainRequest request);

    Train deleteById(Long id);

    void update(Long id, TrainRequest request);

    void patch(Long id, TrainRequest request);

    byte[] getOneTrainImage(Long trainId);

    List<byte[]> getTrainImages(String trainIds);
}
