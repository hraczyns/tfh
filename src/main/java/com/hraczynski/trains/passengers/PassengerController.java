package com.hraczynski.trains.passengers;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/passengers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public
class PassengerController {

    private final PassengerService passengerService;
    private final PassengerRepresentationModelAssembler assembler;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<PassengerDTO>> getAll() {
        Set<Passenger> all = passengerService.getAll();
        return new ResponseEntity<>(assembler.toCollectionModel(all), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PassengerDTO> getById(@PathVariable Long id) {
        Passenger entity = passengerService.getById(id);
        return new ResponseEntity<>(assembler.toModel(entity), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PassengerDTO> addPassenger(@Valid @RequestBody PassengerRequest request) {
        Passenger saved = passengerService.addPassenger(request);
        return new ResponseEntity<>(assembler.toModel(saved), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<PassengerDTO> deleteById(@PathVariable Long id) {
        Passenger passenger = passengerService.deleteById(id);
        return new ResponseEntity<>(assembler.toModel(passenger), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody PassengerRequest request) {
        passengerService.update(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patch(@Valid @RequestBody PassengerRequest request) {
        passengerService.patch(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

