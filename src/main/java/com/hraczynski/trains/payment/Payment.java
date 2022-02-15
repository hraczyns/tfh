package com.hraczynski.trains.payment;

import com.hraczynski.trains.reservations.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Error creating payment. Reservation cannot be resolved.")
    private Long reservationId;
    @NotEmpty(message = "Error creating payment. Payment intent cannot be resolved.")
    private String paymentId;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Error creating reservation. Status of reservation cannot be resolved.")
    private ReservationStatus status;
    @NotNull(message = "Date cannot be null")
    private LocalDateTime createdAt = LocalDateTime.now();
    @NotNull(message = "Date cannot be null")
    private LocalDateTime updatedAt;
}
