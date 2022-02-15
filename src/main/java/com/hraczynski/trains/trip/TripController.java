package com.hraczynski.trains.trip;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/trips", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class TripController {

    private final TripService tripService;
    private final TripRepresentationModelAssembler assembler;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TripDto> getById(@PathVariable Long id) {
        Trip byId = tripService.getById(id);
        return new ResponseEntity<>(assembler.toModel(byId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<CollectionModel<TripDto>> getAll() {
        Set<Trip> all = tripService.getAll();
        return new ResponseEntity<>(assembler.toCollectionModel(all), HttpStatus.OK);
    }

    @GetMapping(params = "train_id")
    public ResponseEntity<CollectionModel<TripDto>> getTripsByTrainId(@RequestParam(name = "train_id") Long trainId) {
        Set<Trip> tripsByTrainId = tripService.getTripsByTrainId(trainId);
        return new ResponseEntity<>(assembler.toCollectionModel(tripsByTrainId), HttpStatus.OK);
    }
}
