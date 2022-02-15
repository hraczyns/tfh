package com.hraczynski.trains.reservations;

import com.hraczynski.trains.passengers.PassengerDto;
import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.payment.PriceDto;
import com.hraczynski.trains.stoptime.StopTimeDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class ReservationDto extends RepresentationModel<ReservationDto> {
    private Long id;
    private List<StopTimeDto> reservedRoute;
    private Set<PassengerDto> passengers;
    private List<PassengerNotRegistered> passengerNotRegisteredList;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private BigDecimal price;
    private Set<PriceDto> pricesInDetails;
    private String identifier;

}
