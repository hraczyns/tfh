package com.hraczynski.trains.train;

import com.hraczynski.trains.reservations.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trains")
@Accessors(chain = true)
public class Train{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Number of seats is demanded")
    @Positive(message = "Wrong number value detected in number of seats")
    private int numberOfSeats;
    @ManyToMany(mappedBy = "trains",fetch = FetchType.LAZY)
    private Set<Reservation> reservations;
    @NotNull(message = "Model is demanded")
    @Enumerated(EnumType.STRING)
    private TrainType model;
    @NotNull(message = "Name is demanded")
    private String name;
    @NotNull(message = "Unique name is demanded")
    private String representationUnique;
}
