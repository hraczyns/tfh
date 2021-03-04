package pl.hub.flights.hateoasmodels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TouristModel {
    private Long id;
    private String name;
    private String surname;
    private String gender;
    private String country;
    private String notes;
    private String bornDate;
}
