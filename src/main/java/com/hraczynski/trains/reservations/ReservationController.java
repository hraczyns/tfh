package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.payment.payment.Payment;
import com.hraczynski.trains.payment.payment.PaymentService;
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
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservations", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationContentService reservationContentService;
    private final PaymentService paymentService;
    private final ReservationRepresentationModelAssembler assembler;

    @GetMapping(value = "/all")
    public ResponseEntity<CollectionModel<ReservationDto>> getAll() {
        CollectionModel<ReservationDto> all = reservationService.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ReservationDto> getByUniqueIdentifierAndEmail(@RequestParam("identifier") String identifier, @RequestParam("email") String email) {
        Reservation reservation = reservationService.getByUniqueIdentifier(identifier, email);
        return new ResponseEntity<>(assembler.toModel(reservation), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        Reservation reservation = reservationService.getById(id);
        return new ResponseEntity<>(assembler.toModel(reservation), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReservationDto> addReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.addReservation(request);
        return new ResponseEntity<>(assembler.toModel(reservation), HttpStatus.CREATED);
    }

    @GetMapping("/passengers/{passengerId}")
    public ResponseEntity<CollectionModel<ReservationDto>> getReservations(@PathVariable("passengerId") Long id) {
        Set<Reservation> reservations = reservationService.getReservationsByPassengerId(id);
        return new ResponseEntity<>(assembler.toCollectionModel(reservations),HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ReservationDto> deleteById(@PathVariable Long id) {
        Reservation reservation = reservationService.deleteById(id);
        return new ResponseEntity<>(assembler.toModel(reservation), HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> updateById(@PathVariable Long id, @Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.updateById(id, request);
        if (reservation == null) {
            throw new EntityNotFoundException(Reservation.class, "id = " + id, request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Void> patchById(@PathVariable Long id, @Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.patchById(id, request);
        if (reservation == null) {
            throw new EntityNotFoundException(Reservation.class, "id = " + id, request.toString());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/discounts/all")
    public ResponseEntity<Map<String, Double>> getPossibleDiscounts() {
        return new ResponseEntity<>(reservationService.getPossibleDiscounts(), HttpStatus.OK);
    }

    @GetMapping("/{paymentId}/content")
    public ResponseEntity<byte[]> getContentByPaymentId(@PathVariable(name = "paymentId") String paymentContentId) {
        Payment payment = paymentService.getPayment(paymentContentId);
        ReservationContentDto fileDto = reservationContentService.getContent(payment);
        return getResponse(fileDto);
    }

    @GetMapping("/content")
    public ResponseEntity<byte[]> getContentByReservationIdentifierAndEmail(@RequestParam(name = "identifier") String reservationIdentifier, @RequestParam("email") String email) {
        ReservationContentDto fileDto = reservationContentService.getContent(reservationIdentifier, email);
        return getResponse(fileDto);
    }

    private ResponseEntity<byte[]> getResponse(ReservationContentDto fileDto) {
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

