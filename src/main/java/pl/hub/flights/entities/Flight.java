package pl.hub.flights.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "DepartureTime is demanded")
    private LocalDateTime departureTime;
    @NotNull(message = "ArrivalTime is demanded")
    private LocalDateTime arrivalTime;
    private int numberOfSeats;
    private double price;
    @ManyToMany
    @JsonIgnoreProperties(value = "flightList")
    private List<Tourist> tourists;
}
