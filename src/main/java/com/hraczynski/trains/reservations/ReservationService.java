package com.hraczynski.trains.reservations;

import org.springframework.hateoas.CollectionModel;

import java.math.BigDecimal;

public interface ReservationService {
    CollectionModel<ReservationDTO> getAll();

    ReservationDTO getById(Long id);

    ReservationDTO deleteById(Long id);

    ReservationDTO updateById(ReservationRequest request);

    ReservationDTO patchById(ReservationRequest request);

    ReservationDTO addReservation(ReservationRequest request, BigDecimal price);
}
