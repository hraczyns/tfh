package com.hraczynski.trains.payment;

import com.hraczynski.trains.stoptime.StopTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "part_prices")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartPrice {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal price;
    @ManyToOne
    private StopTime startStopTime;
    @ManyToOne
    private StopTime endStopTime;
    @ManyToOne
    private Price priceEntity;
}