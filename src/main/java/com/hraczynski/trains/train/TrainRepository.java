package com.hraczynski.trains.train;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TrainRepository extends CrudRepository<Train, Long> {
    Set<Train> findAll();

    Optional<Train> findById(Long id);

    void deleteById(Long aLong);

    @Query(value = "" +
            "select a from Train a " +
            "join StopTime c " +
            "on c.id = :stopId " +
            "where a.id = (select b.train.id from Trip b " +
            "where c.trip.id = b.id)")
    Optional<Train> findTrainByStopTimesId(@Param("stopId") Long stopTimeId);

    boolean existsByRepresentationUnique(String s);

}
