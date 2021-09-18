package com.hraczynski.trains.reservations;

import com.hraczynski.trains.passengers.PassengerDTO;
import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.payment.PriceDTO;
import com.hraczynski.trains.stoptime.StopTimeDTO;
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
public class ReservationDTO extends RepresentationModel<ReservationDTO> {
    private Long id;
    private List<StopTimeDTO> reservedRoute;
    private Set<PassengerDTO> passengers;
    private Set<PassengerNotRegistered> passengerNotRegisteredList;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private BigDecimal price;
    private Set<PriceDTO> pricesInDetails;

}
