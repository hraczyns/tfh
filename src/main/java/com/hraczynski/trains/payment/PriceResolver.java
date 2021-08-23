package com.hraczynski.trains.payment;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.exceptions.definitions.CannotCalculatePriceException;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.reservations.ReservationDiscount;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import com.hraczynski.trains.stoptime.StopsTimeRepository;
import com.hraczynski.trains.train.TrainType;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripsRepository;
import com.hraczynski.trains.utils.BeanUtil;
import com.hraczynski.trains.utils.DistanceCalculator;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;


@Slf4j
@Builder(setterPrefix = "with") // todo repair passengerNumber
public class PriceResolver {
    private final List<StopTimeRequest> stopTimeRequests;
    private final List<Long> stopTimeIds;
    private final ReservationDiscount reservationDiscount;
    private final TripsRepository tripsRepository = BeanUtil.getBean(TripsRepository.class);
    private final StopsTimeRepository stopsTimeRepository = BeanUtil.getBean(StopsTimeRepository.class);
    private final DistanceCalculator distanceCalculator = new DistanceCalculator();
    private final int passengersNumber;

    public BigDecimal calculatePrice() {
        try {
            BigDecimal price;
            if (stopTimeRequests != null) {
                log.info("Started calculating price for trip {}", stopTimeRequests);
                price = calculateSinglePrice();
            } else {
                log.info("Started calculating price for trip ids {}", stopTimeIds);
                price = calculateSinglePriceWithOnlyIds();
            }

            if (reservationDiscount != null) {
                double disc = reservationDiscount.getValue();
                price = price.multiply(BigDecimal.valueOf((100 - disc) / 100));
            }
            return price.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("Cannot perform calculation on the reservation.");
            throw new CannotCalculatePriceException(e.getMessage());
        }

    }

    private BigDecimal calculateSinglePriceWithOnlyIds() {
        validate(this.stopTimeIds);
        BigDecimal price = new BigDecimal(0);
        List<Long> timeIds = this.stopTimeIds;
        for (int i = 1, timeIdsSize = timeIds.size(); i < timeIdsSize; i++) {
            Long stopTimeId = timeIds.get(i);
            Long stopTimeIdPrev = timeIds.get(i - 1);
            Optional<Trip> tripByStopTimesId = tripsRepository.findTripByStopTimesId(stopTimeId);
            Optional<Trip> tripByStopTimesIdPrev = tripsRepository.findTripByStopTimesId(stopTimeId);
            if (tripByStopTimesId.isPresent() && tripByStopTimesIdPrev.isPresent()) {
                Trip trip = tripByStopTimesId.get();
                double distanceBetween = distanceCalculator.calculate(extractCityFromStopTimeById(stopTimeId), extractCityFromStopTimeById(stopTimeIdPrev));
                price = calc(price, trip, distanceBetween);
            } else {
                log.error("Cannot find Trip by StopTime with id = " + (tripByStopTimesId.isPresent() ? stopTimeIdPrev : stopTimeId));
                throw new IllegalArgumentException("Cannot find Trip by StopTime with id = " + (tripByStopTimesId.isPresent() ? stopTimeIdPrev : stopTimeId));
            }
        }
        return price;
    }

    private BigDecimal calc(BigDecimal price, Trip trip, double distanceBetween) {
        double wholeTripDistance = trip.getDistance();
        double distanceFactor = distanceBetween / wholeTripDistance;
        BigDecimal priceForWholeTrip = trip.getPrice();
        price = price.add(new BigDecimal(String.valueOf(priceForWholeTrip.multiply(BigDecimal.valueOf(distanceFactor)))));
        TrainType model = trip.getTrain().getModel();
        price = includeTrainDependingPricing(price, model);
        return price;
    }

    private City extractCityFromStopTimeById(Long stopTimeId) {
        StopTime stopTime = stopsTimeRepository.findById(stopTimeId)
                .orElseThrow(() -> {
                    log.error("Cannot find StopTime with id = {}", stopTimeId);
                    return new EntityNotFoundException(City.class, "id = " + stopTimeId);
                });
        return stopTime.getStop();
    }

    private BigDecimal calculateSinglePrice() {
        BigDecimal price = new BigDecimal(0);
        List<StopTimeRequest> stopTimeDTOList = this.stopTimeRequests;
        validate(stopTimeDTOList);
        for (int i = 1; i < stopTimeDTOList.size(); i++) {
            StopTimeRequest current = stopTimeDTOList.get(i);
            StopTimeRequest prev = stopTimeDTOList.get(i - 1);
            validate(current);
            validate(prev);
            Optional<Trip> tripOpt = tripsRepository.findTripByStopTimesId(current.getId());
            Optional<Trip> tripPrevOpt = tripsRepository.findTripByStopTimesId(prev.getId());
            if (tripOpt.isPresent() && tripPrevOpt.isPresent()) {
                Trip trip = tripOpt.get();
                double distanceBetween = distanceCalculator.calculate(current.getCityRequest(), prev.getCityRequest());
                price = calc(price, trip, distanceBetween);
            } else {
                log.error("Cannot find Trip by StopTime with id = " + (tripOpt.isPresent() ? prev.getId() : current.getId()));
                throw new IllegalArgumentException("Cannot find Trip by StopTime with id = " + (tripOpt.isPresent() ? prev.getId() : current.getId()));
            }
        }
        return price;
    }

    private void validate(Object input) {
        if (input == null || (input instanceof Iterable && !((Iterable<?>) input).iterator().hasNext())) {
            log.error("Element {} can't be recognized or calculated!", input);
            throw new IllegalArgumentException("Element " + input + " can't be recognized or calculated!");
        }

    }

    private BigDecimal includeTrainDependingPricing(BigDecimal price, TrainType model) {
        return price.multiply(BigDecimal.valueOf(model.getPriceRatio()));
    }

}
