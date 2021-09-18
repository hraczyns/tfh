package com.hraczynski.trains.payment;

import com.hraczynski.trains.reservations.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation_prices")
public class Price {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Reservation reservation;
    private Long passengerId;
    @NotNull(message = "Name cannot be null")
    private String name;
    @NotNull(message = "Surname cannot be null")
    private String surname;
    @Positive
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private Discount discount;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST},mappedBy = "priceEntity")
    private List<PartPrice> partPrices;
}
