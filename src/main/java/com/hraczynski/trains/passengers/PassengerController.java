package com.hraczynski.trains.passengers;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/passengers", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<PassengerDTO>> getAll() {
        CollectionModel<PassengerDTO> all = passengerService.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PassengerDTO> getById(@PathVariable Long id) {
        PassengerDTO byId = passengerService.getById(id);
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addPassenger(@Valid @RequestBody PassengerRequest request) {
        PassengerDTO save = passengerService.addPassenger(request);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<PassengerDTO> deleteById(@PathVariable Long id) {
        PassengerDTO passengerDTO = passengerService.deleteById(id);
        return new ResponseEntity<>(passengerDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateById(@Valid @RequestBody PassengerRequest request) {
        PassengerDTO passengerDTO = passengerService.updateById(request);
        if (passengerDTO == null) {
            throw new EntityNotFoundException(Passenger.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patchById(@Valid @RequestBody PassengerRequest request) {
        PassengerDTO passengerDTO = passengerService.patchById(request);
        if (passengerDTO == null) {
            throw new EntityNotFoundException(Passenger.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

