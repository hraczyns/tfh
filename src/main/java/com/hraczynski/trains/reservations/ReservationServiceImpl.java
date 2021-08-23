package com.hraczynski.trains.reservations;

import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.InvalidRouteInput;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerRepository;
import com.hraczynski.trains.payment.PriceResolver;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.train.TrainRepository;
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
    private final TrainRepository trainRepository;

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
    public ReservationDTO addReservation(ReservationRequest request, BigDecimal price) {
        Reservation reservation = mapper.map(request, Reservation.class);
        checkRoute(request);
        Set<Passenger> passengers = findPassengers(request.getIdPassengers());
        Long id;
        if (verifyFoundPassengers(passengers, request.getIdPassengers())) {
            BigDecimal resPrice;
            if (price == null) {
                resPrice = calculatePrice(request);
            } else {
                resPrice = price;
            }
            reservation.setPrice(resPrice);
            reservation.setPassengers(passengers);
            reservation.setStatus(ReservationStatus.IN_PROGRESS);
            reservation.setReservedRoute(request.getReservedRoute().stream()
                    .map(stopTimeMapper::requestToEntity)
                    .collect(Collectors.toList()));
            reservation.setReservationDate(LocalDateTime.now());

            bindTrainToReservation(reservation);

            log.info("Saving Reservation {}", reservation);
            id = reservationRepository.save(reservation).getId();

        } else {
            log.error("Cannot find Passenger with id in {}", request.getIdPassengers());
            throw new EntityNotFoundException(Passenger.class, "id in " + request.getIdPassengers());
        }
        ReservationDTO reservationDTO = assembler.toModel(reservation.setId(id));
        reservationDTO.setReservedRoute(stopTimeMapper.requestToDTOs(request.getReservedRoute()));
        reservationDTO.setIdPassengers(request.getIdPassengers());

        return reservationDTO;
    }

    private void checkRoute(ReservationRequest request) {
        List<StopTimeRequest> reservedRoute = request.getReservedRoute();
        if (reservedRoute == null || reservedRoute.size() < 2) {
            log.error("Route provided in input has uncompleted information.");
            throw new InvalidRouteInput();
        }
    }

    private void bindTrainToReservation(Reservation reservation) {
        List<StopTime> reservedRoute = reservation.getReservedRoute();
        Set<Train> trains = new HashSet<>();
        log.info("Binding trains to Reservation {}", reservation);
        reservedRoute.forEach(s -> {
            Optional<Train> trainByStopTimeId = trainRepository.findTrainByStopTimesId(s.getId());
            trains.add(trainByStopTimeId.orElseThrow(() -> {
                log.error("Cannot find Train with id = {}", s.getId());
                return new EntityNotFoundException(Train.class, "stopTimeId = " + s.getId());
            }));
        });
        reservation.setTrains(trains);
    }

    private BigDecimal calculatePrice(ReservationRequest request) {
        PriceResolver.PriceResolverBuilder builder = PriceResolver.builder();
        builder = builder
                .withStopTimeRequests(request.getReservedRoute())
                .withPassengersNumber(request.getIdPassengers().size());
        if (request.getDiscount() != null) {
            builder = builder.withReservationDiscount(request.getDiscount());
        }
        PriceResolver resolver = builder.build();
        return resolver.calculatePrice();
    }

    private boolean verifyFoundPassengers(Set<Passenger> passengers, Set<Long> idPassengers) {
        return passengers != null && !passengers.isEmpty() && passengers.size() == idPassengers.size() && passengers.stream().noneMatch(Objects::isNull);
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

        List<StopTime> stopTimes = stopTimeMapper.requestToEntities(request.getReservedRoute());
        Set<Passenger> passengers = findPassengers(request.getIdPassengers());
        entityById.setReservedRoute(stopTimes);
        entityById.setPassengers(passengers);

        log.info("Patching Reservation with id = {}", request.getId());
        Reservation saved = reservationRepository.save(entityById);
        return assembler.toModel(saved);
    }

}
