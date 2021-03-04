package pl.hub.flights.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tourist {
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
    @ManyToMany
    @JsonIgnoreProperties(value = "tourists")
    private List<Flight> flightList;


}
