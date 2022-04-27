package com.hraczynski.trains.stoptime;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.trip.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stops")
public class StopTime {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_city")
    private City stop;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Trip trip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StopTime)) return false;
        StopTime stopTime = (StopTime) o;
        return Objects.equals(id, stopTime.id) && Objects.equals(stop, stopTime.stop) && Objects.equals(departureTime, stopTime.departureTime) && Objects.equals(arrivalTime, stopTime.arrivalTime) && Objects.equals(trip, stopTime.trip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stop, departureTime, arrivalTime, trip);
    }
}
