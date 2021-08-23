package com.hraczynski.trains.passengers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
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
    private final PassengerRepresentationModelAssembler assembler;

    @Override
    public CollectionModel<PassengerDTO> getAll() {
        Set<Passenger> all = passengerRepository.findAll();
        if (all == null || all.isEmpty()) {
            throw new EntityNotFoundException(Passenger.class, "none");
        }
        return assembler.toCollectionModel(all);
    }

    @Override
    public PassengerDTO getById(Long id) {
        log.info("Looking for Passenger with id = {}", id);
        return assembler.toModel(getEntityById(id));
    }

    @Override
    public PassengerDTO addPassenger(PassengerRequest request) {
        checkInput(request);

        log.info("Saving Passenger {}", request); // TODO reservations
        Passenger save = passengerRepository.save(mapper.map(request, Passenger.class));
        return assembler.toModel(save);

    }

    @Override
    public PassengerDTO deleteById(Long id) {
        Passenger entityById = getEntityById(id);

        log.info("Deleting Passenger with id = {}", id);
        passengerRepository.deleteById(id);
        return assembler.toModel(entityById);

    }

    @Override
    public PassengerDTO updateById(PassengerRequest request) {
        checkInput(request);
        Passenger entityById = getEntityById(request.getId());

        Passenger mapped = mapper.map(request, Passenger.class);
        mapped.setReservations(entityById.getReservations());
        log.info("Updating Passenger with id = {}", request.getId());
        Passenger saved = passengerRepository.save(mapped);
        return assembler.toModel(saved);
    }

    @Override
    public PassengerDTO patchById(PassengerRequest request) {
        checkInput(request);
        Passenger passenger = getEntityById(request.getId());

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, passenger);

        log.info("Patching Passenger with id = {} ", request.getId());
        Passenger saved = passengerRepository.save(passenger);
        return assembler.toModel(saved);
    }
}
