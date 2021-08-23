package com.hraczynski.trains.train;

import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.exceptions.definitions.CannotReceiveImagesException;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.InputDoesNotMatchesPattern;
import com.hraczynski.trains.exceptions.definitions.NonUniqueFieldException;
import com.hraczynski.trains.images.ImagesProcessor;
import com.hraczynski.trains.trip.TripDTO;
import com.hraczynski.trains.trip.TripService;
import com.hraczynski.trains.utils.PropertiesCopier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainServiceImpl extends AbstractService<Train, TrainRepository> implements TrainService {

    private static final Pattern TRAIN_ID_PATTERN = Pattern.compile("([0-9]+,?)+");
    private static final String SAMPLE = "1,2,3,4";

    private final ModelMapper mapper;
    private final TrainRepository trainRepository;
    private final TrainRepresentationModelAssembler assembler;
    private final TripService tripService;
    private final ImagesProcessor imagesProcessor;

    public CollectionModel<TrainDTO> getAll() {
        log.info("Looking for all Trains");
        Set<Train> all = trainRepository.findAll();

        if (all == null || all.isEmpty()) {
            log.error("Cannot find any Train");
            throw new EntityNotFoundException(Train.class, "none");
        }
        CollectionModel<TrainDTO> trainDTOS = assembler.toCollectionModel(all);
        trainDTOS.forEach(this::specifyUsedParameter);
        return trainDTOS;
    }

    @Override
    public TrainDTO getById(Long id) {
        Train entityById = getEntityById(id);
        TrainDTO trainDTO = assembler.toModel(entityById);
        specifyUsedParameter(trainDTO);
        return trainDTO;

    }

    private void specifyUsedParameter(TrainDTO trainDTO) {
        log.info("Looking for Trips by train id = {}", trainDTO.getId());
        CollectionModel<TripDTO> tripsByTrainId;
        try {
            tripsByTrainId = tripService.getTripsByTrainId(trainDTO.getId());
        } catch (EntityNotFoundException e) {
            tripsByTrainId = null;
        }
        if (tripsByTrainId != null && !tripsByTrainId.getContent().isEmpty()) {
            trainDTO.setUsed(true);
        }
    }

    @Override
    public TrainDTO save(TrainRequest request) {
        //TODO reservations
        log.info("Saving Train {}", request);
        Train train = mapper.map(request, Train.class);
        if (trainRepository.existsByRepresentationUnique(request.getRepresentationUnique())) {
            log.error("Representation unique value has to be unique.");
            throw new NonUniqueFieldException(TrainRequest.class, "RepresentationUnique", request.getRepresentationUnique());
        }
        Train saved = trainRepository.save(train);
        return assembler.toModel(saved);
    }

    @Override
    public TrainDTO deleteById(Long id) {
        Train entityById = getEntityById(id);

        log.info("Deleting Train with id = {}", id);
        trainRepository.deleteById(id);
        return assembler.toModel(entityById);
    }

    @Override
    public TrainDTO updateById(TrainRequest request) {
        checkInput(request);
        getEntityById(request.getId());

        log.info("Updating Train with id = {}", request.getId());
        Train saved = trainRepository.save(mapper.map(request, Train.class));
        return assembler.toModel(saved);
    }

    @Override
    public TrainDTO patchById(TrainRequest request) {
        checkInput(request);
        Train entityById = getEntityById(request.getId());

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, entityById);

        log.info("Patching Train with id = {}", request.getId());
        Train saved = trainRepository.save(entityById);
        return assembler.toModel(saved);
    }

    @Override
    public byte[] getOneTrainImage(Long trainId) {
        log.info("Looking for train images");
        Train entityById = getEntityById(trainId);
        return imagesProcessor.getParsedTrainImageByType(entityById.getModel());
    }

    @Override
    public List<byte[]> getTrainImages(String trainIds) {
        log.info("Looking for train images");
        trainIds = validateWithPatternAndGet(trainIds);
        List<TrainType> trainTypes = parseTrainIdsToTrainsTypes(trainIds);
        return trainTypes.stream()
                .map(imagesProcessor::getParsedTrainImageByType)
                .collect(Collectors.toList());
    }

    private String validateWithPatternAndGet(String trainIds) {
        log.info("Validating input {}", trainIds);
        if (!TRAIN_ID_PATTERN.matcher(trainIds).matches()) {
            log.error("Input {} doesn't match pattern. Valid example {}", trainIds, SAMPLE);
            throw new InputDoesNotMatchesPattern(trainIds, SAMPLE);
        }
        return trainIds.endsWith(",") ? trainIds.substring(0, trainIds.length() - 1) : trainIds;
    }

    private List<TrainType> parseTrainIdsToTrainsTypes(String trainIds) {

        try {
            log.info("Parsing ids to train types");
            return Arrays.stream(trainIds.split(","))
                    .map(idStr -> {
                        long id = Integer.parseInt(idStr);
                        Train entityById = getEntityById(id);
                        return entityById.getModel();
                    })
                    .collect(Collectors.toList());

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred during obtaining information about trains");
            throw new CannotReceiveImagesException("Wrong input!");
        }
    }

}
