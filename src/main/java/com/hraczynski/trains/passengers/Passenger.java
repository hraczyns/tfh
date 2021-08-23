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
    @NotBlank(message = "Gender is demanded")
    private String gender;
    @NotBlank(message = "Country is demanded")
    private String country;
    private String notes;
    @NotNull(message = "Born date is demanded")
    private LocalDate bornDate;
    @ManyToMany(mappedBy = "passengers", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Set<Reservation> reservations;
}
