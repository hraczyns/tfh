package com.hraczynski.trains.train;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/trains", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "images/svg+xml"})
@Slf4j
@CrossOrigin(origins = "http://localhost:3006")
public class TrainController {
    private final TrainService trainService;
    private final TripService tripService;
    private final TrainRepresentationModelAssembler assembler;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<TrainDTO>> findAll() {
        Set<Train> trains = trainService.findAll();
        CollectionModel<TrainDTO> collectionModel = assembler.toCollectionModel(trains);
        collectionModel.forEach(this::specifyUsedParameter);
        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<TrainDTO> findById(@RequestParam("id") Long id) {
        Train train = trainService.findById(id);
        TrainDTO trainDTO = assembler.toModel(train);
        specifyUsedParameter(trainDTO);
        return new ResponseEntity<>(trainDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TrainDTO> addTrain(@Valid @RequestBody TrainRequest trainRequest) {
        Train saved = trainService.save(trainRequest);
        return new ResponseEntity<>(assembler.toModel(saved), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<TrainDTO> deleteById(@PathVariable Long id) {
        Train train = trainService.deleteById(id);
        TrainDTO trainDTO = assembler.toModel(train);
        specifyUsedParameter(trainDTO);
        return new ResponseEntity<>(trainDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody TrainRequest request) {
        trainService.update(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patch(@Valid @RequestBody TrainRequest request) {
        trainService.patch(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/many_images") // FIXME doesn't work as expected
    public ResponseEntity<List<byte[]>> getSomeTrainImages(@RequestParam(name = "train_id") String trainIds) {
        List<byte[]> imageList = trainService.getTrainImages(trainIds);
        final HttpHeaders headers = getHttpHeadersForImageOutput();
        if (imageList == null || imageList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(imageList, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/images")
    public ResponseEntity<byte[]> getTrainImage(@RequestParam(name = "train_id") Long trainId) {
        byte[] image = trainService.getOneTrainImage(trainId);
        final HttpHeaders headers = getHttpHeadersForImageOutput();

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    private HttpHeaders getHttpHeadersForImageOutput() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "images/svg+xml");
        return headers;
    }

    private void specifyUsedParameter(TrainDTO trainDTO) {
        log.info("Looking for Trips by train id = {}", trainDTO.getId());
        Set<Trip> tripsByTrainId;
        try {
            tripsByTrainId = tripService.getTripsByTrainId(trainDTO.getId());
        } catch (EntityNotFoundException e) {
            tripsByTrainId = null;
        }
        if (tripsByTrainId != null && !tripsByTrainId.isEmpty()) {
            trainDTO.setUsed(true);
        }
    }


}