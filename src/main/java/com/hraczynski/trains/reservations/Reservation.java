package com.hraczynski.trains.reservations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.passengers.Passenger;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;
    @Positive(message = "Price must be specified")
    private BigDecimal price;
    @Column(name = "reservation_date")
    @NotNull(message = "Error creating reservation. Reservation date is empty.")
    private LocalDateTime reservationDate;
    @ManyToMany
    @JoinColumn(name = "id_passenger")
    @NotNull
    @Size(min = 1,message = "Cannot create reservation with not specified target passenger")
    private Set<Passenger> passengers;
    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull
    @Size(min = 2,message = "Reservation must contain at least two elements of route")
    private List<StopTime> reservedRoute;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Error creating reservation. Status of reservation cannot be resolved.")
    private ReservationStatus status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "trains_reservations",joinColumns = @JoinColumn(name = "reservations_id"),inverseJoinColumns = @JoinColumn(name = "train_id"))
    @NotNull(message = "Error during binding trains with the reservation.")
    private Set<Train> trains;
}
