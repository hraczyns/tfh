package com.hraczynski.trains.city;

import lombok.*;
import com.hraczynski.trains.country.Country;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Name must not be null")
    private String name;
    @NotNull(message = "Lon must not be null")
    private Double lon;
    @NotNull(message = "Lat must not be null")
    private Double lat;
    @ManyToOne
    @NotNull(message = "Country must not be null")
    private Country country;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(id, city.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
