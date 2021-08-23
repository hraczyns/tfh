package com.hraczynski.trains.trip;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/trips", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class TripController {

    private final TripService tripService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TripDTO> getById(@PathVariable Long id) {
        TripDTO byId = tripService.getById(id);
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<CollectionModel<TripDTO>> getAll() {
        CollectionModel<TripDTO> all = tripService.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping( params = "train_id")
    public ResponseEntity<CollectionModel<TripDTO>> getTripsByTrainId(@RequestParam(name = "train_id") Long trainId) {
        CollectionModel<TripDTO> tripsByTrainId = tripService.getTripsByTrainId(trainId);
        return new ResponseEntity<>(tripsByTrainId,HttpStatus.OK);
    }
}
