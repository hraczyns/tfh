package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.train.TrainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationTrainBinder {

    private final TrainRepository trainRepository;

    public void bind(Reservation reservation) {
        List<StopTime> reservedRoute = reservation.getReservedRoute();
        Set<Train> trains = new HashSet<>();
        log.info("Binding trains to Reservation");
        reservedRoute.forEach(s -> {
            Optional<Train> trainByStopTimeId = trainRepository.findTrainByStopTimesId(s.getId());
            trains.add(trainByStopTimeId.orElseThrow(() -> {
                log.error("Cannot find Train with stopTimeId = {}", s.getId());
                return new EntityNotFoundException(Train.class, "stopTimeId = " + s.getId());
            }));
        });
        reservation.setTrains(trains);
    }
}
