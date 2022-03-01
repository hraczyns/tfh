package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.payment.Payment;
import com.hraczynski.trains.payment.PaymentService;
import com.hraczynski.trains.reservations.reservationscontent.ReservationContentDto;
import com.hraczynski.trains.reservations.reservationscontent.ReservationContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservations", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationContentService reservationContentService;
    private final PaymentService paymentService;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<ReservationDto>> getAll() {
        CollectionModel<ReservationDto> all = reservationService.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        ReservationDto dto = reservationService.getById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReservationDto> addReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationDto reservationDto = reservationService.addReservation(request);
        return new ResponseEntity<>(reservationDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ReservationDto> deleteById(@PathVariable Long id) {
        ReservationDto dto = reservationService.deleteById(id);
        return new ResponseEntity<>(dto, HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> updateById(@Valid @RequestBody ReservationRequest request) {
        ReservationDto reservationDto = reservationService.updateById(request);
        if (reservationDto == null) {
            throw new EntityNotFoundException(Reservation.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patchById(@Valid @RequestBody ReservationRequest request) {
        ReservationDto reservationDto = reservationService.patchById(request);
        if (reservationDto == null) {
            throw new EntityNotFoundException(Reservation.class, "id = " + request.getId(), request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/discounts/all")
    public ResponseEntity<Map<String, Double>> getPossibleDiscounts() {
        return new ResponseEntity<>(reservationService.getPossibleDiscounts(), HttpStatus.OK);
    }

    @GetMapping("/{paymentId}/content")
    public ResponseEntity<byte[]> getContent(@PathVariable(name = "paymentId") String paymentContentId) {
        Payment payment = paymentService.getPayment(paymentContentId);
        ReservationContentDto fileDto = reservationContentService.getContent(payment);
        if (fileDto.getFilename() == null || fileDto.getFilename().isEmpty()) {
            return new ResponseEntity<>(new byte[]{}, HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(fileDto.getFile().length);
        headers.set("Content-Disposition", "attachment; filename=" + fileDto.getFilename());
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileDto.getFile());

    }
}

