package com.hraczynski.trains.train;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/trains", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "images/svg+xml"})
@CrossOrigin(origins = "http://localhost:3000")
public class TrainController {
    private final TrainService trainService;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<TrainDTO>> getAll() {
        CollectionModel<TrainDTO> flights = trainService.getAll();
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<TrainDTO> getById(@RequestParam("id") Long id) {
        TrainDTO flight = trainService.getById(id);
        return new ResponseEntity<>(flight, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TrainDTO> addTrain(@Valid @RequestBody TrainRequest trainRequest) {
        TrainDTO saved = trainService.save(trainRequest);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<TrainDTO> deleteById(@PathVariable Long id) {
        TrainDTO flight = trainService.deleteById(id);
        return new ResponseEntity<>(flight, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateById(@Valid @RequestBody TrainRequest request) {
        TrainDTO updated = trainService.updateById(request);
        if (updated == null) {
            throw new EntityNotFoundException(Train.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patchById(@Valid @RequestBody TrainRequest request) {
        TrainDTO trainDTO = trainService.patchById(request);
        if (trainDTO == null) {
            throw new EntityNotFoundException(Train.class, "id = " + request.getId(), request.toString());
        }
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


}