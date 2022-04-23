package com.hraczynski.trains.security;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerService;
import com.hraczynski.trains.reservations.Reservation;
import com.hraczynski.trains.reservations.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

@Component("userSecurityCheck")
@RequiredArgsConstructor
@Slf4j
public class UserSecurityCheck {

    private static final Pattern IS_NUMBER = Pattern.compile("\\d+");

    private final PassengerService passengerService;
    private final ReservationService reservationService;

    public boolean hasPassengerId(Authentication authentication, String passengerId) {
        Passenger passenger;
        try {
            if (!IS_NUMBER.matcher(passengerId).matches()) {
                return false;
            }
            passenger = passengerService.getById(Long.parseLong(passengerId));
            String email = passenger.getEmail();
            return areEmailsEqual(authentication, email);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private boolean areEmailsEqual(Authentication authentication, String email) {
        if (authentication == null || email == null) {
            return false;
        }
        return Objects.equals(email, authentication.getName());
    }

    public boolean hasPassengerIdByReservationId(Authentication authentication, String reservationId) {
        try {
            if (!IS_NUMBER.matcher(reservationId).matches()) {
                return false;
            }
            Reservation reservation = reservationService.getById(Long.parseLong(reservationId));
            return reservation.getPassengers().stream()
                    .anyMatch(passenger -> areEmailsEqual(authentication, passenger.getPassenger().getEmail()));
        } catch (RuntimeException e) {
            return false;
        }
    }
}
