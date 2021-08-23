package com.hraczynski.trains.train;

import org.springframework.hateoas.CollectionModel;

import java.util.List;

public interface TrainService {
    CollectionModel<TrainDTO> getAll();

    TrainDTO getById(Long id);

    TrainDTO save(TrainRequest request);

    TrainDTO deleteById(Long id);

    TrainDTO updateById(TrainRequest request);

    TrainDTO patchById(TrainRequest request);

    byte[] getOneTrainImage(Long trainId);

    List<byte[]> getTrainImages(String trainIds);
}
