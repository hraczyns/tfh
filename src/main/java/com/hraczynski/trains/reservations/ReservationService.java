package com.hraczynski.trains.reservations;

import org.springframework.hateoas.CollectionModel;

import java.math.BigDecimal;
import java.util.Map;

public interface ReservationService {
    CollectionModel<ReservationDto> getAll();

    Reservation getById(Long id);

    Reservation deleteById(Long id);

    Reservation updateById(ReservationRequest request);

    Reservation patchById(ReservationRequest request);

    Reservation addReservation(ReservationRequest reservationRequest);

    Reservation addReservation(ReservationRequest reservationRequest, BigDecimal sumFromReservation);

    Map<String, Double> getPossibleDiscounts();

    void updateStatus(Reservation reservation, ReservationStatus status);

    Reservation getByUniqueIdentifier(String identifier, String email);
    Reservation getByUniqueIdentifierInternalUsage(String identifier);
}
