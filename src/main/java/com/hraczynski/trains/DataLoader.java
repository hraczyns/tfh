package com.hraczynski.trains;

import com.hraczynski.trains.city.CityDTO;
import com.hraczynski.trains.city.CityRequest;
import com.hraczynski.trains.journey.JourneyDTO;
import com.hraczynski.trains.journey.JourneyRepresentationModelAssembler;
import com.hraczynski.trains.passengers.PassengerDTO;
import com.hraczynski.trains.passengers.PassengerRequest;
import com.hraczynski.trains.passengers.PassengerService;
import com.hraczynski.trains.reservations.ReservationDiscount;
import com.hraczynski.trains.reservations.ReservationRequest;
import com.hraczynski.trains.reservations.ReservationService;
import com.hraczynski.trains.routefinder.RouteFinderService;
import com.hraczynski.trains.stoptime.StopTimeDTO;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import com.hraczynski.trains.train.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataLoader implements ApplicationRunner {
    private final TrainService trainService;
    private final PassengerService passengerService;
    private final ReservationService reservationService;
    private final StopQueriesGenerator stopQueries;
    private final RouteFinderService routeFinderService;


    @Autowired
    public DataLoader(TrainService trainService, PassengerService passengerService, ReservationService reservationService, StopQueriesGenerator stopQueriesGenerator, RouteFinderService routeFinderService) {
        this.trainService = trainService;
        this.passengerService = passengerService;
        this.reservationService = reservationService;
        this.stopQueries = stopQueriesGenerator;
        this.routeFinderService = routeFinderService;
    }


    @Override
    public void run(ApplicationArguments args) throws IOException {
//        CollectionModel<TrainDTO> all = null;
//        try {
////            all = trainService.getAll();
//        } catch (Exception ignored) {
//        }
//
////        if (all == null || all.getContent().isEmpty()) {
////            TrainDTO trainDTO = new TrainDTO();
////            trainDTO.setId(1L);
////            trainDTO.setArrivalTime(LocalDateTime.of(2020, 5, 12, 12, 32));
////            trainDTO.setDepartureTime(LocalDateTime.of(2021, 5, 12, 12, 32));
////            trainDTO.setNumberOfSeats(30);
////            trainDTO.setPrice(100);
////            trainDTO.setSource(1L);
////            trainDTO.setDestination(2L);
////            trainService.save(trainDTO);
////        }
//
//        CollectionModel<PassengerDTO> all1 = null;
//        try {
//            all1 = passengerService.getAll();
//        } catch (Exception ignored) {
//        }
//
//        if (all1 == null || all1.getContent().isEmpty()) {
//            PassengerDTO passengerDTO = new PassengerDTO();
//            passengerDTO.setId(1L);
//            passengerDTO.setNotes("Some notes");
//            passengerDTO.setSurname("Kowalski");
//            passengerDTO.setName("Jan");
//            passengerDTO.setCountry("Poland");
//            passengerDTO.setGender("male");
//            passengerDTO.setBornDate(LocalDate.now());
//            passengerService.save(passengerDTO);
//        }
//        CollectionModel<ReservationDTO> all2 = null;
//
//        try {
//            all2 = reservationService.getAll();
//        } catch (Exception ignored) {
//        }
//
//        if (all2 == null || all2.getContent().isEmpty()) {
//            CollectionModel<TrainDTO> trainDTOS = null;
//
//            try {
////                trainDTOS = trainService.getAll();
//            } catch (Exception ignored) {
//            }
//
////            assert trainDTOS != null;
//
////            Optional<TrainDTO> flightDTO = trainDTOS.getContent().stream().findAny();
        CollectionModel<PassengerDTO> passengerDTOS = null;

        try {
            passengerDTOS = passengerService.getAll();
        } catch (Exception ignored) {
        }

        assert passengerDTOS != null;

//        Optional<PassengerDTO> passengerDTO = passengerDTOS.getContent().stream().findAny();
        PassengerRequest passengerRequest = new PassengerRequest();
        passengerRequest.setName("asd");
        passengerRequest.setSurname("naziwkso");
        passengerRequest.setBornDate(LocalDate.now());
        passengerRequest.setCountry("Poland");
        passengerRequest.setGender("male");
        PassengerRequest passengerRequest1 = new PassengerRequest();
        passengerRequest1.setName("asd2");
        passengerRequest1.setSurname("naziwkso2");
        passengerRequest1.setBornDate(LocalDate.now());
        passengerRequest1.setCountry("Poland");
        passengerRequest1.setGender("male");
        PassengerRequest passengerRequest2 = new PassengerRequest();
        passengerRequest2.setName("asd3");
        passengerRequest2.setSurname("naziwkso3");
        passengerRequest2.setBornDate(LocalDate.now());
        passengerRequest2.setCountry("Poland");
        passengerRequest2.setGender("male");
        passengerService.addPassenger(passengerRequest1);
        passengerService.addPassenger(passengerRequest);
        passengerService.addPassenger(passengerRequest2);

        try {
            passengerDTOS = passengerService.getAll();
        } catch (Exception ignored) {
        }

        ReservationRequest reservationRequest = new ReservationRequest();
        CollectionModel<JourneyDTO> route = routeFinderService.findRoute(1L, 14L, LocalDateTime.of(2021, 3, 16, 14, 23));
        List<StopTimeRequest> stopTimeDTOS = parseRoute(route);
        reservationRequest.setId(1L);
        reservationRequest.setReservationDate(LocalDateTime.now());
        reservationRequest.setReservedRoute(stopTimeDTOS);
        reservationRequest.setDiscount(ReservationDiscount.STUDENT);
        reservationRequest.setIdPassengers(Set.of(1L, 2L, 3L));
        reservationService.addReservation(reservationRequest, null);

        CollectionModel<JourneyDTO> route2 = routeFinderService.findRoute(1L, 14L, LocalDateTime.of(2021, 3, 16, 14, 23));
        List<StopTimeRequest> parsed2 = parseRoute(route2);
        ReservationRequest dto2 = new ReservationRequest();
        dto2.setId(2L);
        dto2.setReservedRoute(parsed2);
        dto2.setReservationDate(LocalDateTime.now().minusDays(2));
        dto2.setIdPassengers(Set.of(1L));
        reservationService.addReservation(dto2, BigDecimal.valueOf(200));

//                    passengerDTO.ifPresent(s -> dto.setIdTourist(s.getId()));
//            flightDTO.ifPresent(s -> dto.setIdFlight(s.getId()));
//            reservationService.addReservation(dto);
//        }
//
//        stopQueries.generate();
    }

    private List<StopTimeRequest> parseRoute(CollectionModel<JourneyDTO> route) {
        List<StopTimeDTO> stopTimeDTOS = new ArrayList<>();
        List<JourneyDTO> content = new ArrayList<>(route.getContent());
        List<JourneyRepresentationModelAssembler.PartOfJourney> partOfJourneys = content.get(0).getPartOfJourneys();
        for (JourneyRepresentationModelAssembler.PartOfJourney partOfJourney : partOfJourneys) {
            JourneyRepresentationModelAssembler.PartOfJourneyTimeTable partOfJourneyTimeTable = (JourneyRepresentationModelAssembler.PartOfJourneyTimeTable) partOfJourney;
            StopTimeDTO start = partOfJourneyTimeTable.start();
            StopTimeDTO end = partOfJourneyTimeTable.end();
            if (stopTimeDTOS.stream().noneMatch(stopTimeDTO -> stopTimeDTO.getId().equals(start.getId()))) {
                stopTimeDTOS.add(start);
            }

            if (stopTimeDTOS.stream().noneMatch(stopTimeDTO -> stopTimeDTO.getId().equals(end.getId()))) {
                stopTimeDTOS.add(end);
            }
        }

        return stopTimeDTOS.stream()
                .map(s -> {
                    StopTimeRequest request = new StopTimeRequest();
                    request.setId(s.getId());
                    request.setArrivalTime(s.getArrivalTime());
                    request.setDepartureTime(s.getDepartureTime());

                    CityDTO cityDTO = s.getCityDTO();
                    CityRequest cityRequest = new CityRequest();
                    cityRequest.setId(cityDTO.getId());
                    cityRequest.setCountry(cityDTO.getCountry());
                    cityRequest.setLat(cityDTO.getLat());
                    cityRequest.setLon(cityDTO.getLon());
                    cityRequest.setName(cityDTO.getName());
                    request.setCityRequest(cityRequest);
                    return request;
                }).collect(Collectors.toList());
    }
}
