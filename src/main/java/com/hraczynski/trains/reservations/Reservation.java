package com.hraczynski.trains.reservations;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerWithDiscount;
import com.hraczynski.trains.payment.Price;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.train.Train;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private Set<PassengerWithDiscount> passengers;
    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull
    @Size(min = 2, message = "Reservation must contain at least two elements of route")
    private List<StopTime> reservedRoute;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Error creating reservation. Status of reservation cannot be resolved.")
    private ReservationStatus status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "trains_reservations", joinColumns = @JoinColumn(name = "reservations_id"), inverseJoinColumns = @JoinColumn(name = "train_id"))
    @NotNull(message = "Error during binding trains with the reservation.")
    private Set<Train> trains;
    @Pattern(regexp = "(^([a-zA-Z]+<>[a-zA-Z]+<>[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+<>[a-zA-z]*,?)+$)|^null$")
    private String passengersNotRegistered;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "reservation")
    @Size(min = 1, message = "Prices must be specified")
    private Set<Price> prices;
    @Setter(AccessLevel.NONE)
    @Column(unique = true)
    @NotNull(message = "Identifier is null")
    private String identifier = UUID.randomUUID().toString();
}
