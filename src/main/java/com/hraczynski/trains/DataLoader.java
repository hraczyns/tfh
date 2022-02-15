//package com.hraczynski.trains;
//
//import com.hraczynski.trains.city.CityDto;
//import com.hraczynski.trains.city.CityRequest;
//import com.hraczynski.trains.journey.JourneyDto;
//import com.hraczynski.trains.journey.JourneyRepresentationModelAssembler;
//import com.hraczynski.trains.passengers.*;
//import com.hraczynski.trains.payment.Discount;
//import com.hraczynski.trains.reservations.ReservationRequest;
//import com.hraczynski.trains.reservations.ReservationService;
//import com.hraczynski.trains.routefinder.RouteFinderService;
//import com.hraczynski.trains.stoptime.StopTimeDto;
//import com.hraczynski.trains.stoptime.StopTimeRequest;
//import com.hraczynski.trains.train.TrainService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.hateoas.CollectionModel;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Component
//public class DataLoader implements ApplicationRunner {
//    private final TrainService trainService;
//    private final PassengerService passengerService;
//    private final ReservationService reservationService;
//    private final StopQueriesGenerator stopQueries;
//    private final RouteFinderService routeFinderService;
//
//
//    @Autowired
//    public DataLoader(TrainService trainService, PassengerService passengerService, ReservationService reservationService, StopQueriesGenerator stopQueriesGenerator, RouteFinderService routeFinderService) {
//        this.trainService = trainService;
//        this.passengerService = passengerService;
//        this.reservationService = reservationService;
//        this.stopQueries = stopQueriesGenerator;
//        this.routeFinderService = routeFinderService;
//    }
//
//
//    @Override
//    public void run(ApplicationArguments args) throws IOException {
////        CollectionModel<TrainDto> all = null;
////        try {
//////            all = trainService.getAll();
////        } catch (Exception ignored) {
////        }
////
//////        if (all == null || all.getContent().isEmpty()) {
//////            TrainDto trainDto = new TrainDto();
//////            trainDto.setId(1L);
//////            trainDto.setArrivalTime(LocalDateTime.of(2020, 5, 12, 12, 32));
//////            trainDto.setDepartureTime(LocalDateTime.of(2021, 5, 12, 12, 32));
//////            trainDto.setNumberOfSeats(30);
//////            trainDto.setPrice(100);
//////            trainDto.setSource(1L);
//////            trainDto.setDestination(2L);
//////            trainService.save(trainDto);
//////        }
////
////        CollectionModel<PassengerDto> all1 = null;
////        try {
////            all1 = passengerService.getAll();
////        } catch (Exception ignored) {
////        }
////
////        if (all1 == null || all1.getContent().isEmpty()) {
////            PassengerDto passengerDto = new PassengerDto();
////            passengerDto.setId(1L);
////            passengerDto.setNotes("Some notes");
////            passengerDto.setSurname("Kowalski");
////            passengerDto.setName("Jan");
////            passengerDto.setCountry("Poland");
////            passengerDto.setGender("male");
////            passengerDto.setBornDate(LocalDate.now());
////            passengerService.save(passengerDto);
////        }
////        CollectionModel<ReservationDto> all2 = null;
////
////        try {
////            all2 = reservationService.getAll();
////        } catch (Exception ignored) {
////        }
////
////        if (all2 == null || all2.getContent().isEmpty()) {
////            CollectionModel<TrainDto> trainDtoS = null;
////
////            try {
//////                trainDtoS = trainService.getAll();
////            } catch (Exception ignored) {
////            }
////
//////            assert trainDtoS != null;
////
//////            Optional<TrainDto> flightDto = trainDtoS.getContent().stream().findAny();
//        CollectionModel<PassengerDto> passengerDtoS = null;
//
//        try {
//            passengerDtoS = passengerService.getAll();
//        } catch (Exception ignored) {
//        }
//
////        assert passengerDtoS != null;
//
////        Optional<PassengerDto> passengerDto = passengerDtoS.getContent().stream().findAny();
//        PassengerRequest passengerRequest = new PassengerRequest();
//        passengerRequest.setName("asd");
//        passengerRequest.setSurname("naziwkso");
//        passengerRequest.setBornDate(LocalDate.now());
//        passengerRequest.setCountry("Poland");
//        passengerRequest.setGender("male");
//        PassengerRequest passengerRequest1 = new PassengerRequest();
//        passengerRequest1.setName("asd2");
//        passengerRequest1.setSurname("naziwkso2");
//        passengerRequest1.setBornDate(LocalDate.now());
//        passengerRequest1.setCountry("Poland");
//        passengerRequest1.setGender("male");
//        PassengerRequest passengerRequest2 = new PassengerRequest();
//        passengerRequest2.setName("asd3");
//        passengerRequest2.setSurname("naziwkso3");
//        passengerRequest2.setBornDate(LocalDate.now());
//        passengerRequest2.setCountry("Poland");
//        passengerRequest2.setGender("male");
//        passengerService.addPassenger(passengerRequest1);
//        passengerService.addPassenger(passengerRequest);
//        passengerService.addPassenger(passengerRequest2);
//
//        try {
//            passengerDtoS = passengerService.getAll();
//        } catch (Exception ignored) {
//        }
//
//        CollectionModel<JourneyDto> route = routeFinderService.findRoute(1L, 14L, LocalDateTime.of(2021, 3, 16, 14, 23));
//        List<StopTimeRequest> stopTimeDtoS = parseRoute(route);
//        ReservationRequest reservationRequest = new ReservationRequest();
//        reservationRequest.setId(1L);
//        reservationRequest.setReservedRoute(stopTimeDtoS.stream().map(StopTimeRequest::getId).collect(Collectors.toList()));
//        reservationRequest.setIdPassengersWithDiscounts(Set.of(
//                new PassengerWithDiscount().setPassengerId(1L).setDiscountCode("S"),
//                new PassengerWithDiscount().setPassengerId(2L),
//                new PassengerWithDiscount().setPassengerId(3L).setDiscountCode("V")
//        ));
//        reservationService.addReservation(reservationRequest);
//
//        CollectionModel<JourneyDto> route2 = routeFinderService.findRoute(1L, 14L, LocalDateTime.of(2021, 3, 16, 14, 23));
//        List<StopTimeRequest> parsed2 = parseRoute(route2);
//        ReservationRequest dto2 = new ReservationRequest();
//        dto2.setId(2L);
//        dto2.setReservedRoute(parsed2.stream().map(StopTimeRequest::getId).collect(Collectors.toList()));
//        dto2.setIdPassengersWithDiscounts(Set.of(new PassengerWithDiscount().setPassengerId(1L).setDiscountCode("S")));
//        dto2.setPassengerNotRegisteredSet(Set.of(
//                new PassengerNotRegistered()
//                        .setName("Szymon")
//                        .setSurname("Wojciechowski")
//        ));
//        reservationService.addReservation(dto2);
//
////                    passengerDto.ifPresent(s -> dto.setIdTourist(s.getId()));
////            flightDto.ifPresent(s -> dto.setIdFlight(s.getId()));
////            reservationService.addReservation(dto);
////        }
////
////        stopQueries.generate();
//    }
//
//    private List<StopTimeRequest> parseRoute(CollectionModel<JourneyDto> route) {
//        List<StopTimeDto> stopTimeDtoS = new ArrayList<>();
//        List<JourneyDto> content = new ArrayList<>(route.getContent());
//        List<JourneyRepresentationModelAssembler.PartOfJourney> partOfJourneys = content.get(0).getPartOfJourneys();
//        for (JourneyRepresentationModelAssembler.PartOfJourney partOfJourney : partOfJourneys) {
//            JourneyRepresentationModelAssembler.PartOfJourneyTimeTable partOfJourneyTimeTable = (JourneyRepresentationModelAssembler.PartOfJourneyTimeTable) partOfJourney;
//            StopTimeDto start = partOfJourneyTimeTable.start();
//            StopTimeDto end = partOfJourneyTimeTable.end();
//            if (stopTimeDtoS.stream().noneMatch(stopTimeDto -> stopTimeDto.getId().equals(start.getId()))) {
//                stopTimeDtoS.add(start);
//            }
//
//            if (stopTimeDtoS.stream().noneMatch(stopTimeDto -> stopTimeDto.getId().equals(end.getId()))) {
//                stopTimeDtoS.add(end);
//            }
//        }
//
//        return stopTimeDtoS.stream()
//                .map(s -> {
//                    StopTimeRequest request = new StopTimeRequest();
//                    request.setId(s.getId());
//                    request.setArrivalTime(s.getArrivalTime());
//                    request.setDepartureTime(s.getDepartureTime());
//
//                    CityDto cityDto = s.getCityDto();
////                    CityRequest cityRequest = new CityRequest();
////                    cityRequest.setId(cityDto.getId());
////                    cityRequest.setCountry(cityDto.getCountry());
////                    cityRequest.setLat(cityDto.getLat());
////                    cityRequest.setLon(cityDto.getLon());
////                    cityRequest.setName(cityDto.getName());
//                    request.setCityId(cityDto.getId());
//                    return request;
//                }).collect(Collectors.toList());
//    }
//}
