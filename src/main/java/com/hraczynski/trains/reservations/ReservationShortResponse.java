package com.hraczynski.trains.reservations;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationShortResponse {
    private String identifier;
    private String firstMail;
}
