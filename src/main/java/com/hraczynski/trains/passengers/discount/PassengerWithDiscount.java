package com.hraczynski.trains.passengers.discount;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.payment.Discount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "passengers_with_discount")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerWithDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @NotNull
    private Passenger passenger;
    @Enumerated(EnumType.STRING)
    private Discount discount;
}
