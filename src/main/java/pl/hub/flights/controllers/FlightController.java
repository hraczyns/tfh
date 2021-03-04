package pl.hub.flights.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hub.flights.entities.Flight;
import pl.hub.flights.services.FlightService;
import pl.hub.flights.services.TouristService;
import pl.hub.flights.utils.FlightsRepresentationModelAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/flights", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class FlightController {
    private final FlightService flightService;
    private final TouristService touristService;
    private final FlightsRepresentationModelAssembler assembler;

    @Autowired
    public FlightController(FlightService flightService, TouristService touristService, FlightsRepresentationModelAssembler assembler) {
        this.flightService = flightService;
        this.touristService = touristService;
        this.assembler = assembler;
    }

    @GetMapping("/all")
    public ResponseEntity<CollectionModel<Flight>> getAll() {
        List<Flight> flights = flightService.findAll();
        if (flights == null || flights.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(assembler.toCollectionModel(flights,HttpStatus.OK));
    }

//    @GetMapping(path = "{flightId}")
//    public Flight getById(@PathVariable("flightId") Long id) {
//        return flightService.findById(id).orElseThrow(() -> new DataNotFoundException(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<?> addFlight(@RequestBody Optional<Flight> flight) {
//        try {
//            if (flight.isPresent() && flight.get().getNumberOfTourist() > 0) {
//                flightService.save(flight.get());
//                return ResponseEntity.ok(flight.get());
//            } else {
//                throw new DataNotFoundException();
//            }
//        } catch (ConstraintViolationException e) {
//            throw new DataNotFoundException();
//        }
//    }
//
//    @DeleteMapping
//    public ResponseEntity<?> deleteById(@RequestParam Long id) {
//        Flight flight = flightService.findById(id)
//                .orElseThrow(() -> new DataNotFoundException(id));
//        flightService.delete(flight);
//        return ResponseEntity.ok(flight);
//    }
//
//    @DeleteMapping("/deleteflight")
//    public ResponseEntity<?> deleteFlight(@RequestParam("touristId") Long touristId, @RequestParam("flightId") Long flightId) {
//        Optional<Tourist> tourist = touristService.findById(touristId);
//        Optional<Flight> flight = flightService.findById(flightId);
//        if (tourist.isPresent() && flight.isPresent()) {
//            List<Flight> list = tourist.get().getFlightList() == null ? new ArrayList<>() : tourist.get().getFlightList();
//            int index = Math.toIntExact(flightId);
//            list.remove(index - 1);
//            Tourist tourist1 = new Tourist(tourist.get().getName(), tourist.get().getSurname(), tourist.get().getGender(), tourist.get().getCountry(), tourist.get().getNotes(), tourist.get().getBornDate(), list);
//            tourist1.setId(touristId);
//            touristService.save(tourist1);
//            return ResponseEntity.ok().build();
//        } else {
//            throw new DataNotFoundException();
//        }
//    }
}

//TODO
//1. pobierz wszystkie
//2. pobierz po ID
//3. dodaj lot
//4. modyfikuj lot
//5. usun po id
//6. usun turyste