package com.hraczynski.trains.passengers;

import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscount;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscountRepository;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscountRequest;
import com.hraczynski.trains.payment.Discount;
import com.hraczynski.trains.utils.PropertiesCopier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl extends AbstractService<Passenger, PassengerRepository> implements PassengerService {

    private final ModelMapper mapper;
    private final PassengerRepository passengerRepository;
    private final PassengerWithDiscountRepository passengerWithDiscountRepository;

    @Override
    public Set<Passenger> getAll() {
        Set<Passenger> all = passengerRepository.findAll();
        if (all == null || all.isEmpty()) {
            throw new EntityNotFoundException(Passenger.class, "none");
        }
        return all;
    }

    @Override
    public Passenger getById(Long id) {
        log.info("Looking for Passenger with id = {}", id);
        return getEntityById(id);
    }

    @Override
    public Passenger addPassenger(PassengerRequest request) {
        checkInput(request);
        log.info("Saving Passenger {}", request.getEmail());
        Passenger passenger = passengerRepository.save(mapper.map(request, Passenger.class));
        log.info("Passenger saved");
        return passenger;
    }

    @Override
    public Passenger deleteById(Long id) {
        Passenger entityById = getEntityById(id);

        log.info("Deleting Passenger with id = {}", id);
        passengerRepository.deleteById(id);
        return entityById;

    }

    @Override
    public void update(Long id, PassengerRequest request) {
        checkInput(request);
        getEntityById(id);

        Passenger mapped = mapper.map(request, Passenger.class);
        mapped.setId(id);
        log.info("Updating Passenger with id = {}", id);
        passengerRepository.save(mapped);
    }

    @Override
    public void patch(Long id, PassengerRequest request) {
        checkInput(request);
        Passenger passenger = getEntityById(id);

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, passenger, "id");
//      !  passenger.setReservations(entityById.getReservations()); consider that

        log.info("Patching Passenger with id = {} ", id);
        passengerRepository.save(passenger);
    }

    @Override
    public PassengerWithDiscount addPassengerWhileReservation(PassengerWithDiscountRequest passengerWithDiscountRequest) {
        checkInput(passengerWithDiscountRequest);
        log.info("Saving passenger while reservation.");
        Long passengerId = passengerWithDiscountRequest.getPassengerId();
        Passenger entityById = getEntityById(passengerId);
        PassengerWithDiscount passengerWithDiscount = new PassengerWithDiscount(null, entityById, Discount.findByCode(passengerWithDiscountRequest.getDiscountCode()));
        return passengerWithDiscountRepository.save(passengerWithDiscount);
    }
}
