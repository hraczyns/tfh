package com.hraczynski.trains.payment;

import com.hraczynski.trains.exceptions.definitions.BadStopTimeIdsFormatRequestException;
import com.hraczynski.trains.exceptions.definitions.InvalidRequestException;
import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.passengers.PassengerWithDiscountRequest;
import com.hraczynski.trains.reservations.ReservationRequest;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    private final PriceResolver priceResolver;

    public PriceResponse getPrice(String stopTimeIds) {
        return getPrice(stopTimeIds, null);
    }

    public PriceResponse getPrice(String stopTimeIds, Discount discount) {
        log.info("Start calculating price for ids in ({})", stopTimeIds);

        List<Long> stopTimeList = parseToList(stopTimeIds);
        PriceResponse res = new PriceResponse();
        for (int i = 1; i < stopTimeList.size(); i += 2) {
            Long id = stopTimeList.get(i);
            Long idPrev = stopTimeList.get(i - 1);
            PriceResolverConfig.PriceResolverConfigBuilder priceResolverConfigBuilder = PriceResolverConfig.builder()
                    .withStopTimeIds(Arrays.asList(id, idPrev));
            if (discount != null) {
                priceResolverConfigBuilder = priceResolverConfigBuilder.withDiscount(discount);
            }

            BigDecimal calculatePrice = priceResolver.calculatePrice(priceResolverConfigBuilder.build());
            addToResponse(res, id, idPrev, calculatePrice);
        }
        log.info("Started calculated general price");
        res.setPriceInGeneral(getPriceInGeneral(res));
        log.info("Calculations were performed successfully");
        return res;
    }

    private BigDecimal getPriceInGeneral(PriceResponse res) {
        return res.getPrices().stream()
                .map(PricePartResponse::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private List<Long> parseToList(String stopTimeIds) {
        if (stopTimeIds == null) {
            throw new BadStopTimeIdsFormatRequestException();
        }
        String[] ids = stopTimeIds.split(",");
        if (ids.length == 0 || ids.length % 2 != 0) {
            throw new BadStopTimeIdsFormatRequestException(ids);
        }
        return Arrays.stream(ids)
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
    }

    private void addToResponse(PriceResponse res, Long id, Long idPrev, BigDecimal calculatedPrice) {
        PricePartResponse partResponse = new PricePartResponse();
        partResponse.setPrice(calculatedPrice);
        partResponse.setStartId(idPrev);
        partResponse.setStopId(id);
        if (res.getPrices() == null) {
            res.setPrices(new ArrayList<>());
        }
        res.getPrices().add(partResponse);
    }

    public PriceResponseWithPassengers getPriceResponseWithPassengers(Set<PassengerWithDiscountRequest> passengersRegisteredWithPotentialDiscounts, List<StopTimeRequest> stopTimeRequests) {
        List<Long> stopTimeIds = stopTimeRequests.stream().map(StopTimeRequest::getId).collect(Collectors.toList());
        return getPriceResponseWithPassengersCommon(passengersRegisteredWithPotentialDiscounts, null, stopTimeIds);
    }

    public PriceResponseWithPassengers getPriceResponseWithPassengers(Set<PassengerWithDiscountRequest> passengersRegisteredWithPotentialDiscounts, List<Long> stopTimeIds, @SuppressWarnings("unused") boolean onlyStopTimeIds) {
        return getPriceResponseWithPassengersCommon(passengersRegisteredWithPotentialDiscounts, null, stopTimeIds);
    }

    public PriceResponseWithPassengers getPriceResponseWithPassengers(Set<PassengerWithDiscountRequest> passengersRegisteredWithPotentialDiscounts, List<PassengerNotRegistered> passengerNotRegisteredList, List<StopTimeRequest> stopTimeRequests) {
        List<Long> stopTimeIds = stopTimeRequests.stream().map(StopTimeRequest::getId).collect(Collectors.toList());
        return getPriceResponseWithPassengersCommon(passengersRegisteredWithPotentialDiscounts, passengerNotRegisteredList, stopTimeIds);
    }

    public PriceResponseWithPassengers getPriceResponseWithPassengers(Set<PassengerWithDiscountRequest> passengersRegisteredWithPotentialDiscounts, List<PassengerNotRegistered> passengerNotRegisteredList, List<Long> stopTimeIds, @SuppressWarnings("unused") boolean onlyStopTimeIds) {
        return getPriceResponseWithPassengersCommon(passengersRegisteredWithPotentialDiscounts, passengerNotRegisteredList, stopTimeIds);
    }

    private PriceResponseWithPassengers getPriceResponseWithPassengersCommon(Set<PassengerWithDiscountRequest> passengersRegisteredWithPotentialDiscounts, List<PassengerNotRegistered> passengerNotRegisteredList, List<Long> stopTimeIds) {

        Set<PricePerPassenger> pricePerPassengers = passengersRegisteredWithPotentialDiscounts.stream()
                .map(passenger -> new PricePerPassenger()
                        .setPassenger(passenger)
                        .setPriceResponse(getPriceResponse(stopTimeIds, passenger.getDiscountCode())))
                .collect(Collectors.toSet());
        Set<PricePerNogRegisteredPassenger> pricePerNogRegisteredPassengers = new HashSet<>();
        if (passengerNotRegisteredList != null && !passengerNotRegisteredList.isEmpty()) {
            pricePerNogRegisteredPassengers = passengerNotRegisteredList.stream()
                    .map(passenger -> new PricePerNogRegisteredPassenger()
                            .setPassengerNotRegistered(passenger)
                            .setPriceResponse(getPriceResponse(stopTimeIds, passenger.getDiscountCode())))
                    .collect(Collectors.toSet());
        }
        PriceResponseWithPassengers priceResponseWithPassengers = new PriceResponseWithPassengers();

        if (!pricePerPassengers.isEmpty()) {
            priceResponseWithPassengers.setPriceResponseForPassengersIds(pricePerPassengers);
        }

        if (!pricePerNogRegisteredPassengers.isEmpty()) {
            priceResponseWithPassengers.setPricePerNogRegisteredPassengers(pricePerNogRegisteredPassengers);
        }

        return priceResponseWithPassengers;
    }

    private PriceResponse getPriceResponse(List<Long> stopTimeIds, String discountCode) {
        Discount discount = Discount.findByCode(discountCode);
        String stopTimeIdsString = stopTimeIds
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return getPrice(stopTimeIdsString, discount);

    }

    public PriceWithDiscount getPriceWithDiscount(String price, String discount) {
        BigDecimal priceNumber;
        Discount reservationDiscount;
        try {
            log.info("Parsing price");
            priceNumber = BigDecimal.valueOf(Double.parseDouble(price));
        } catch (NumberFormatException e) {
            log.error("Price is not a number!");
            throw new InvalidRequestException("Price is not a number!");
        }
        try {
            log.info("Parsing discount");
            reservationDiscount = Discount.valueOf(discount.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            log.error("Discount unrecognized");
            throw new InvalidRequestException("Discount unrecognized");
        }
        log.info("Performing calculations");
        BigDecimal priceWithDiscount = priceNumber.divide(BigDecimal.valueOf(100 / (100 - reservationDiscount.getValue())), 2, RoundingMode.HALF_EVEN);
        return new PriceWithDiscount(priceNumber, priceWithDiscount, reservationDiscount);
    }

    public BigDecimal getSumFromReservation(ReservationRequest request) {
        PriceResponseWithPassengers priceResponseWithPassengers = getPriceResponseWithPassengers(
                request.getIdPassengersWithDiscounts(),
                request.getPassengerNotRegisteredList(),
                request.getReservedRoute(),
                true
        );
        BigDecimal price = BigDecimal.ZERO;
        if (priceResponseWithPassengers.getPriceResponseForPassengersIds() != null) {
            price = price.add(priceResponseWithPassengers.getPriceResponseForPassengersIds().stream()
                    .map(PricePerPassenger::getPriceResponse)
                    .map(PriceResponse::getPriceInGeneral)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (priceResponseWithPassengers.getPricePerNogRegisteredPassengers() != null) {
            price = price.add(priceResponseWithPassengers.getPricePerNogRegisteredPassengers().stream()
                    .map(PricePerNogRegisteredPassenger::getPriceResponse)
                    .map(PriceResponse::getPriceInGeneral)
                    .reduce(price, BigDecimal::add));
        }

        return price;

    }
}
