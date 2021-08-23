package com.hraczynski.trains.stoptime;

import com.hraczynski.trains.trip.Trip;
import lombok.*;
import com.hraczynski.trains.city.City;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stops")
@ToString
public class StopTime {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_city")
    private City stop;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Trip trip;
}
