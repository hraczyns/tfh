package com.hraczynski.trains.reservations;

import org.springframework.hateoas.CollectionModel;

import java.math.BigDecimal;
import java.util.Map;

public interface ReservationService {
    CollectionModel<ReservationDto> getAll();

    ReservationDto getById(Long id);

    ReservationDto deleteById(Long id);

    ReservationDto updateById(ReservationRequest request);

    ReservationDto patchById(ReservationRequest request);

    ReservationDto addReservation(ReservationRequest reservationRequest);

    ReservationDto addReservation(ReservationRequest reservationRequest, BigDecimal sumFromReservation);

    Map<String, Double> getPossibleDiscounts();

    Reservation getEntityById(Long id);

    void updateStatus(Reservation reservation, ReservationStatus status);

}
