package com.hraczynski.trains.payment;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.exceptions.definitions.CannotCalculatePriceException;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import com.hraczynski.trains.stoptime.StopsTimeRepository;
import com.hraczynski.trains.train.TrainType;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripsRepository;
import com.hraczynski.trains.utils.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PriceResolver {
    private final TripsRepository tripsRepository;
    private final StopsTimeRepository stopsTimeRepository;
    private final CityRepository cityRepository;
    private final DistanceCalculator distanceCalculator = new DistanceCalculator();

    public static BigDecimal includeDiscount(BigDecimal price, Discount discount) {
        return price.multiply(BigDecimal.valueOf((100 - discount.getValue()) / 100));
    }

    public BigDecimal calculatePrice(PriceResolverConfig config) {
        if(config == null){
            return BigDecimal.valueOf(0.0);
        }
        List<StopTimeRequest> stopTimeRequests = config.getStopTimeRequests();
        Discount discount = config.getDiscount();
        List<Long> stopTimeIds = config.getStopTimeIds();

        try {
            BigDecimal price;
            if (stopTimeRequests != null) {
                log.info("Started calculating price for trip {}", stopTimeRequests);
                price = calculateSinglePrice(stopTimeRequests);
            } else {
                log.info("Started calculating price for trip ids {}", stopTimeIds);
                price = calculateSinglePriceWithOnlyIds(stopTimeIds);
            }
            if (discount != null) {
                log.info("Using set discount");
                price = PriceResolver.includeDiscount(price, discount);
            }
            return price.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("Cannot perform calculation on the reservation.");
            throw new CannotCalculatePriceException(e.getMessage());
        }
    }

    private BigDecimal calculateSinglePriceWithOnlyIds(List<Long> stopTimeIds) {
        validate(stopTimeIds);
        BigDecimal price = new BigDecimal(0);
        for (int i = 1, timeIdsSize = stopTimeIds.size(); i < timeIdsSize; i++) {
            Long stopTimeId = stopTimeIds.get(i);
            Long stopTimeIdPrev = stopTimeIds.get(i - 1);
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
                    return new EntityNotFoundException(StopTime.class, "id = " + stopTimeId);
                });
        return stopTime.getStop();
    }

    private BigDecimal calculateSinglePrice(List<StopTimeRequest> stopTimeRequests) {
        BigDecimal price = new BigDecimal(0);
        validate(stopTimeRequests);
        for (int i = 1; i < stopTimeRequests.size(); i++) {
            StopTimeRequest current = stopTimeRequests.get(i);
            StopTimeRequest prev = stopTimeRequests.get(i - 1);
            validate(current);
            validate(prev);
            Optional<Trip> tripOpt = tripsRepository.findTripByStopTimesId(current.getId());
            Optional<Trip> tripPrevOpt = tripsRepository.findTripByStopTimesId(prev.getId());
            if (tripOpt.isPresent() && tripPrevOpt.isPresent()) {
                Trip trip = tripOpt.get();
                City cityCurrent = findCityById(current.getCityId());
                City cityPrev = findCityById(prev.getCityId());
                double distanceBetween = distanceCalculator.calculate(cityCurrent, cityPrev);
                price = calc(price, trip, distanceBetween);
            } else {
                log.error("Cannot find Trip by StopTime with id = " + (tripOpt.isPresent() ? prev.getId() : current.getId()));
                throw new IllegalArgumentException("Cannot find Trip by StopTime with id = " + (tripOpt.isPresent() ? prev.getId() : current.getId()));
            }
        }
        return price;
    }

    private City findCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot find City with id = {}", id);
                    return new EntityNotFoundException(City.class, "id = " + id);
                });
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
