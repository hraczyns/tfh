package pl.hub.flights.hateoasmodels;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightModel {
    private Long id;
    private String departureTime;
    private String arrivalTime;
    private int numberOfSeats;
    private double price;
    private List<TouristModel> tourists;
}
