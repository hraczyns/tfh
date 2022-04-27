package com.hraczynski.trains.reservations;

import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.email.EmailExtractor;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.InvalidRouteInput;
import com.hraczynski.trains.passengers.*;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscount;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscountRequest;
import com.hraczynski.trains.passengers.unregistered.PassengerNotRegisteredMapper;
import com.hraczynski.trains.payment.Discount;
import com.hraczynski.trains.payment.price.PriceService;
import com.hraczynski.trains.reservations.binder.ReservationPricesBinder;
import com.hraczynski.trains.reservations.binder.ReservationTrainBinder;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.hraczynski.trains.utils.PropertiesCopier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl extends AbstractService<Reservation, ReservationRepository> implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassengerService passengerService;
    private final ReservationRepresentationModelAssembler assembler;
    private final ModelMapper mapper;
    private final StopTimeMapper stopTimeMapper;
    private final PassengerNotRegisteredMapper passengerNotRegisteredMapper;
    private final PriceService priceService;
    private final ReservationPricesBinder reservationPricesBinder;
    private final ReservationTrainBinder reservationTrainBinder;
    private final ReservationEmailService reservationEmailService;
    private final EmailExtractor emailExtractor;

    @Override
    public CollectionModel<ReservationDto> getAll() {
        log.info("Looking for all Reservations");
        Set<Reservation> reservations = reservationRepository.findAll();

        if (reservations == null || reservations.isEmpty()) {
            log.error("Cannot find any reservation");
            throw new EntityNotFoundException(Reservation.class, "none");
        }

        return assembler.toCollectionModel(reservations);
    }

    @Override
    public Reservation getById(Long id) {
        log.info("Looking for Reservation with id = {}", id);
        return getEntityById(id);
    }

    @Override
    @Transactional
    public Reservation addReservation(ReservationRequest reservationRequest) {
        return addReservation(reservationRequest, null);
    }

    @Override
    @Transactional
    public Reservation addReservation(ReservationRequest reservationRequest, BigDecimal resPrice) {
        Reservation reservation = mapper.map(reservationRequest, Reservation.class);
        checkRoute(reservationRequest);
        Set<PassengerWithDiscount> passengers = findPassengers(reservationRequest);
        Long id;
        if (verifyFoundPassengers(passengers, extractIdsFromPassengersRequest(reservationRequest.getIdPassengersWithDiscounts()).keySet())) {
            if (resPrice == null) {
                resPrice = priceService.getSumFromReservation(reservationRequest);
            }
            reservation.setPrice(resPrice);
            reservation.setPassengers(passengers);
            reservation.setStatus(ReservationStatus.INIT);
            reservation.setReservedRoute(getReservedRoute(reservationRequest));
            reservation.setReservationDate(LocalDateTime.now());
            reservation.setPassengersNotRegistered(passengerNotRegisteredMapper.serialize(reservationRequest.getPassengerNotRegisteredList()));

            addPricesToReservation(reservation, reservationRequest);
            addTrainsToReservation(reservation);

            log.info("Saving Reservation");
            id = reservationRepository.save(reservation).getId();
            reservation.setId(id);
            log.info("Successfully saved");
            reservationEmailService.sendReservationInitEmail(emailExtractor.getEmails(reservation, reservationRequest), reservation.getIdentifier());
        } else {
            log.error("Cannot find Passenger with id in {}", extractIdsFromPassengersRequest(reservationRequest.getIdPassengersWithDiscounts()));
            throw new EntityNotFoundException(Passenger.class, "id in " + extractIdsFromPassengersRequest(reservationRequest.getIdPassengersWithDiscounts()));
        }

        return reservation;
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

    private boolean verifyFoundPassengers(Set<PassengerWithDiscount> passengers, Set<Long> idPassengers) {
        return passengers != null && passengers.size() == idPassengers.size() && passengers.stream().noneMatch(Objects::isNull);
    }

    private Map<Long, String> extractIdsFromPassengersRequest(Set<PassengerWithDiscountRequest> passengerWithDiscountRequests) {
        return passengerWithDiscountRequests.stream()
                .collect(Collectors.toMap(PassengerWithDiscountRequest::getPassengerId, PassengerWithDiscountRequest::getDiscountCode));
    }

    private Set<PassengerWithDiscount> findAndSavePassengers(Set<PassengerWithDiscountRequest> request) {
        return request.stream()
                .map(passengerService::addPassengerWhileReservation)
                .collect(Collectors.toSet());
    }

    @Override
    public Reservation deleteById(Long id) {
        Reservation entityById = getEntityById(id);

        log.info("Deleting Reservation with id = {}", id);
        reservationRepository.deleteById(id);
        return entityById;
    }

    @Override
    public Reservation updateById(Long id, ReservationRequest request) {
        //TODO ?
        checkInput(request);
        Reservation entityById = getEntityById(id);

        log.info("Updating Reservation with id = {}", id);
        return reservationRepository.save(entityById);
    }

    @Override
    public Reservation patchById(Long id, ReservationRequest request) {
        checkInput(request);
        Reservation entityById = getEntityById(id);

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, entityById, "id");

        List<StopTime> stopTimes = stopTimeMapper.idsToEntities(request.getReservedRoute());
        Set<PassengerWithDiscount> passengers = findPassengers(request);
        entityById.setReservedRoute(stopTimes);
        entityById.setPassengers(passengers);

        log.info("Patching Reservation with id = {}", request.getId());
        return reservationRepository.save(entityById);
    }

    private Set<PassengerWithDiscount> findPassengers(ReservationRequest request) {
        return findAndSavePassengers(request.getIdPassengersWithDiscounts());
    }

    @Override
    public Map<String, Double> getPossibleDiscounts() {
        log.info("Looking for all discounts");
        return Arrays.stream(Discount.values())
                .collect(Collectors.toMap(s -> s.name().toLowerCase(), Discount::getValue));
    }

    @Override
    public void updateStatus(Reservation reservation, ReservationStatus status) {
        if (reservation != null && status != null) {
            log.info("Updating reservation to status {}", status.name());
            reservation.setStatus(status);
            reservationRepository.save(reservation);
            log.info("Reservation status has been successfully updated");
        } else {
            throw new IllegalStateException("Reservation is not found and therefore it cannot be updated!");
        }
    }

    @Override
    public Reservation getByUniqueIdentifier(String identifier, String email) {
        Reservation reservation = getByUniqueIdentifierInternalUsage(identifier);
        List<String> emails = emailExtractor.getEmails(reservation);
        if (email != null && emails.contains(email)) {
            return reservation;
        }
        log.error("Cannot find reservation with identifier = {} which contains email {}", identifier, email);
        throw new EntityNotFoundException(Reservation.class, "identifier = " + identifier, "email = " + email);
    }

    @Override
    public Reservation getByUniqueIdentifierInternalUsage(String identifier) {
        log.info("Looking for reservation with identifier = {}", identifier);
        Reservation reservation = reservationRepository.findByIdentifier(identifier);
        if (reservation == null) {
            log.error("Cannot find reservation with identifier = {}", identifier);
            throw new EntityNotFoundException(Reservation.class, "identifier = " + identifier);
        }
        return reservation;
    }

    @Override
    public Set<Reservation> getReservationsByPassengerId(Long id) {
        log.info("Looking for reservations for logged user with passenger id = {}", id);
        Set<Reservation> reservations = reservationRepository.findByPassengerId(id);
        if (reservations.isEmpty()) {
            log.warn("Cannot find any reservations with passenger id = {}", id);
            throw new EntityNotFoundException(Reservation.class, "passengerId = " + id);
        }
        return reservations;
    }


}
