package com.hraczynski.trains.reservations.reservationscontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationContentDto {
    private String filename;
    private byte[] file;
}
