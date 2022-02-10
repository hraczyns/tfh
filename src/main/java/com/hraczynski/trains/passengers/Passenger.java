package com.hraczynski.trains.passengers;

import com.hraczynski.trains.reservations.Reservation;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is demanded")
    private String name;
    @NotBlank(message = "Surname is demanded")
    private String surname;
    @NotBlank(message = "Country is demanded")
    private String country;
    @NotNull(message = "Born date is demanded")
    private LocalDate bornDate;
    @NotNull(message = "Email is demanded")
    private String email;
    @ManyToMany(mappedBy = "passengers", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Set<Reservation> reservations;
}
