package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservations", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<ReservationDTO>> getAll() {
        CollectionModel<ReservationDTO> all = reservationService.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ReservationDTO> getById(@PathVariable Long id) {
        ReservationDTO dto = reservationService.getById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> addReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationDTO reservationDTO = reservationService.addReservation(request);
        return new ResponseEntity<>(reservationDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ReservationDTO> deleteById(@PathVariable Long id) {
        ReservationDTO dto = reservationService.deleteById(id);
        return new ResponseEntity<>(dto, HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> updateById(@Valid @RequestBody ReservationRequest request) {
        ReservationDTO reservationDTO = reservationService.updateById(request);
        if (reservationDTO == null) {
            throw new EntityNotFoundException(Reservation.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patchById(@Valid @RequestBody ReservationRequest request) {
        ReservationDTO reservationDTO = reservationService.patchById(request);
        if (reservationDTO == null) {
            throw new EntityNotFoundException(Reservation.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/discounts/all")
    public ResponseEntity<Map<String,Double>> getPossibleDiscounts() {
        return new ResponseEntity<>(reservationService.getPossibleDiscounts(), HttpStatus.OK);
    }
}
