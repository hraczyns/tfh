package com.hraczynski.trains.reservations;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.hraczynski.trains.stoptime.StopTimeRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@ToString
public class ReservationRequest {
    private Long id;
    private List<StopTimeRequest> reservedRoute;
    private Set<Long> idPassengers;
    private LocalDateTime reservationDate;
    private ReservationDiscount discount;
    private ReservationStatus status;
    private BigDecimal price;
}
