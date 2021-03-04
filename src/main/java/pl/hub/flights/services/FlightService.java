package pl.hub.flights.services;

import org.springframework.stereotype.Service;
import pl.hub.flights.entities.Flight;

import java.util.List;

public interface FlightService {
    List<Flight> findAll();
}
