package com.hraczynski.trains.passengers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.utils.PropertiesCopier;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl extends AbstractService<Passenger, PassengerRepository> implements PassengerService {

    private final ModelMapper mapper;
    private final PassengerRepository passengerRepository;

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

        log.info("Saving Passenger {}", request);
        return passengerRepository.save(mapper.map(request, Passenger.class));

    }

    @Override
    public Passenger deleteById(Long id) {
        Passenger entityById = getEntityById(id);

        log.info("Deleting Passenger with id = {}", id);
        passengerRepository.deleteById(id);
        return entityById;

    }

    @Override
    public void update(PassengerRequest request) {
        checkInput(request);
        getEntityById(request.getId());

        Passenger mapped = mapper.map(request, Passenger.class);
        log.info("Updating Passenger with id = {}", request.getId());
        passengerRepository.save(mapped);
    }

    @Override
    public void patch(PassengerRequest request) {
        checkInput(request);
        Passenger passenger = getEntityById(request.getId());

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, passenger);
//      !  passenger.setReservations(entityById.getReservations()); consider that

        log.info("Patching Passenger with id = {} ", request.getId());
        passengerRepository.save(passenger);
    }
}
