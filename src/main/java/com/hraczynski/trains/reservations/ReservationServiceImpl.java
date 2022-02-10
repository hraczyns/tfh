package com.hraczynski.trains.reservations;

import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.InvalidRouteInput;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerNotRegisteredMapper;
import com.hraczynski.trains.passengers.PassengerRepository;
import com.hraczynski.trains.passengers.PassengerWithDiscount;
import com.hraczynski.trains.payment.Discount;
import com.hraczynski.trains.payment.PriceService;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.hraczynski.trains.utils.PropertiesCopier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl extends AbstractService<Reservation, ReservationRepository> implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassengerRepository passengerRepository;
    private final ReservationRepresentationModelAssembler assembler;
    private final ModelMapper mapper;
    private final StopTimeMapper stopTimeMapper;
    private final PassengerNotRegisteredMapper passengerNotRegisteredMapper;
    private final PriceService priceService;
    private final ReservationPricesBinder reservationPricesBinder;
    private final ReservationTrainBinder reservationTrainBinder;

    @Override
    public CollectionModel<ReservationDTO> getAll() {
        log.info("Looking for all Reservations");
        Set<Reservation> reservations = reservationRepository.findAll();

        if (reservations == null || reservations.isEmpty()) {
            log.error("Cannot find any reservation");
            throw new EntityNotFoundException(Reservation.class, "none");
        }

        return assembler.toCollectionModel(reservations);
    }

    @Override
    public ReservationDTO getById(Long id) {
        log.info("Looking for Reservation with id = {}", id);
        Reservation entityById = getEntityById(id);
        return assembler.toModel(entityById);
    }

    @Override
    public ReservationDTO addReservation(ReservationRequest request) {
        Reservation reservation = mapper.map(request, Reservation.class);
        checkRoute(request);
        Set<Passenger> passengers = findPassengers(request);
        Long id;
        if (verifyFoundPassengers(passengers, extractIdsFromPassengersRequest(request.getIdPassengersWithDiscounts()))) {
            BigDecimal resPrice = priceService.getSumFromReservation(request);

            reservation.setPrice(resPrice);
            reservation.setPassengers(passengers);
            reservation.setStatus(ReservationStatus.IN_PROGRESS);
            reservation.setReservedRoute(getReservedRoute(request));
            reservation.setReservationDate(LocalDateTime.now());
            reservation.setPassengersNotRegistered(passengerNotRegisteredMapper.serialize(request.getPassengerNotRegisteredList()));

            addPricesToReservation(reservation, request);
            addTrainsToReservation(reservation);

            log.info("Saving Reservation");
            id = reservationRepository.save(reservation).getId();
            log.info("Successfully saved");

        } else {
            log.error("Cannot find Passenger with id in {}", extractIdsFromPassengersRequest(request.getIdPassengersWithDiscounts()));
            throw new EntityNotFoundException(Passenger.class, "id in " + extractIdsFromPassengersRequest(request.getIdPassengersWithDiscounts()));
        }
        ReservationDTO reservationDTO = assembler.toModel(reservation.setId(id));
        reservationDTO.setPassengerNotRegisteredList(request.getPassengerNotRegisteredList());

        return reservationDTO;
    }

    private void addPricesToReservation(Reservation reservation, ReservationRequest request) {
        reservationPricesBinder.bind(
                reservation,
                priceService.getPriceResponseWithPassengers(
                        request.getIdPassengersWithDiscounts(),
                        request.getPassengerNotRegisteredList(),
                        request.getReservedRoute(),
                        true
                ));
    }

    private List<StopTime> getReservedRoute(ReservationRequest request) {
        return stopTimeMapper.idsToEntities(request.getReservedRoute());
    }

    private void checkRoute(ReservationRequest request) {
        List<Long> reservedRoute = request.getReservedRoute();
        if (reservedRoute == null || reservedRoute.size() < 2) {
            log.error("Route provided in input has uncompleted information.");
            throw new InvalidRouteInput();
        }
    }

    private void addTrainsToReservation(Reservation reservation) {
        reservationTrainBinder.bind(reservation);
    }

    private boolean verifyFoundPassengers(Set<Passenger> passengers, Set<Long> idPassengers) {
        return passengers != null && passengers.size() == idPassengers.size() && passengers.stream().noneMatch(Objects::isNull);
    }

    private Set<Long> extractIdsFromPassengersRequest(Set<PassengerWithDiscount> passengerWithDiscounts) {
        return passengerWithDiscounts.stream()
                .map(PassengerWithDiscount::getPassengerId)
                .collect(Collectors.toSet());
    }

    private Set<Passenger> findPassengers(Set<Long> idPassengers) {
        return idPassengers.stream()
                .map(s -> {
                    Optional<Passenger> byId = passengerRepository.findById(s);
                    if (byId.isPresent()) {
                        log.info("Found Passenger for reservation with id = {}", s);
                        return byId.get();
                    } else {
                        log.error("Cannot find Passenger for reservation with id = {}", s);
                        throw new EntityNotFoundException(Passenger.class, "id = " + s);
                    }

                }).collect(Collectors.toSet());
    }

    @Override
    public ReservationDTO deleteById(Long id) {
        Reservation entityById = getEntityById(id);

        log.info("Deleting Reservation with id = {}", id);
        reservationRepository.deleteById(id);
        return assembler.toModel(entityById);
    }

    @Override
    public ReservationDTO updateById(ReservationRequest request) {
        checkInput(request);
        Reservation entityById = getEntityById(request.getId());

        log.info("Updating Reservation with id = {}", request.getId());
        Reservation saved = reservationRepository.save(entityById);
        return assembler.toModel(saved);

    }

    @Override
    public ReservationDTO patchById(ReservationRequest request) {
        checkInput(request);
        Reservation entityById = getEntityById(request.getId());

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, entityById);

        List<StopTime> stopTimes = stopTimeMapper.idsToEntities(request.getReservedRoute());
        Set<Passenger> passengers = findPassengers(request);
        entityById.setReservedRoute(stopTimes);
        entityById.setPassengers(passengers);

        log.info("Patching Reservation with id = {}", request.getId());
        Reservation saved = reservationRepository.save(entityById);
        return assembler.toModel(saved);
    }

    private Set<Passenger> findPassengers(ReservationRequest request) {
        return findPassengers(extractIdsFromPassengersRequest(request.getIdPassengersWithDiscounts()));
    }

    @Override
    public Map<String, Double> getPossibleDiscounts() {
        log.info("Looking for all discounts");
        return Arrays.stream(Discount.values())
                .collect(Collectors.toMap(s -> s.name().toLowerCase(Locale.ROOT), Discount::getValue));

    }
}
