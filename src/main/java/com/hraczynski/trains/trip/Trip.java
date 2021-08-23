package com.hraczynski.trains.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.stoptime.StopTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "trip",fetch = FetchType.LAZY)
    @Size(message = "Trip must have at least 2 elements", min = 2)
    private List<StopTime> stopTimes;
    @Positive(message = "Price must be positive number!")
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    @NotNull(message = "Trip must have specified train!")
    private Train train;
    private double distance;

}
