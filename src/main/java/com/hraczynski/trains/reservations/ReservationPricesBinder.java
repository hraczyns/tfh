package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.CannotBindToReservationException;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.passengers.PassengerRepository;
import com.hraczynski.trains.payment.*;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopsTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationPricesBinder {

    private final StopsTimeRepository stopsTimeRepository;
    private final PassengerRepository passengerRepository;

    public void bind(Reservation reservation, PriceResponseWithPassengers priceResponseWithPassengers) {
        try {
            log.info("Processing price binding");
            Set<Price> prices = new HashSet<>();

            if (priceResponseWithPassengers.getPriceResponseForPassengersIds() != null) {
                priceResponseWithPassengers.getPriceResponseForPassengersIds()
                        .forEach(price -> {
                            Price priceEntity = new Price();
                            Long passengerId = price.getPassenger().getPassengerId();
                            Passenger passenger = getPassengerById(passengerId);
                            fillPriceEntityWithBasicInfo(priceEntity, passenger.getName(), passenger.getSurname(), price.getPassenger().getDiscountCode(), price.getPriceResponse(), reservation);
                            priceEntity.setPassengerId(passengerId);
                            prices.add(priceEntity);
                        });
            }

            if (priceResponseWithPassengers.getPricePerNogRegisteredPassengers() != null) {
                priceResponseWithPassengers.getPricePerNogRegisteredPassengers()
                        .forEach(price -> {
                            Price priceEntity = new Price();
                            PassengerNotRegistered passengerNotRegistered = price.getPassengerNotRegistered();
                            fillPriceEntityWithBasicInfo(priceEntity, passengerNotRegistered.getName(), passengerNotRegistered.getSurname(), passengerNotRegistered.getDiscountCode(), price.getPriceResponse(), reservation);

                            prices.add(priceEntity);
                        });
            }

            reservation.setPrices(prices);
        } catch (Exception e) {
            log.error("Binding was not successful");
            throw new CannotBindToReservationException(e.getMessage());
        }
    }

    private void fillPriceEntityWithBasicInfo(Price price, String name, String surname, String discountCode, PriceResponse priceResponse, Reservation reservation) {
        price.setPartPrices(getPartPricesAndSave(price, priceResponse.getPrices()));
        price.setReservation(reservation);
        price.setSurname(surname);
        price.setName(name);
        price.setDiscount(getDiscountByCode(discountCode));
        price.setPrice(priceResponse.getPriceInGeneral());
    }

    private List<PartPrice> getPartPricesAndSave(Price price, List<PricePartResponse> pricePartResponses) {

        return pricePartResponses.stream()
                .map(pricePartResponse -> {
                    PartPrice partPrice = new PartPrice();
                    partPrice.setPrice(pricePartResponse.getPrice());
                    partPrice.setStartStopTime(getStopById(pricePartResponse.getStartId()));
                    partPrice.setEndStopTime(getStopById(pricePartResponse.getStopId()));
                    partPrice.setPriceEntity(price);
                    return partPrice;
                }).collect(Collectors.toList());
    }

    private Passenger getPassengerById(Long passengerId) {
        return passengerRepository.findById(passengerId)
                .orElseThrow(() -> {
                    log.error("Cannot find Passenger by id = {}", passengerId);
                    return new EntityNotFoundException(Passenger.class, "id = " + passengerId);
                });
    }

    private Discount getDiscountByCode(String discountCode) {
        return Discount.findByCode(discountCode);
    }

    private StopTime getStopById(Long stopId) {
        return stopsTimeRepository.findById(stopId)
                .orElseThrow(() -> {
                    log.error("Cannot find StopTime by id = {}", stopId);
                    return new EntityNotFoundException(StopTime.class, "id = " + stopId);
                });
    }
}
