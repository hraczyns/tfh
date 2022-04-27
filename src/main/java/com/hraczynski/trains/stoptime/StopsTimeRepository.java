package com.hraczynski.trains.stoptime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StopsTimeRepository extends CrudRepository<StopTime, Long> {
    @Query(value = "" +
            "select distinct a from StopTime a " +
            "join fetch a.trip " +
            "join fetch a.stop b " +
            "join fetch b.country")
    List<StopTime> findAll();

    @Query(value = "" +
            "select a from StopTime a " +
            "join fetch a.stop b " +
            "where a.id = :id")
    Optional<StopTime> findById(@Param("id") Long id);

    @Query(value = "" +
            "select distinct a from StopTime a " +
            "join fetch a.stop b " +
            "join fetch a.trip c " +
            "join fetch c.train d " +
            "where b.id = :cityId " +
            "and a.departureTime between :startTime and :stopTime " +
            "order by a.departureTime")
    List<StopTime> findByCityIdAndDepartureTimeBetweenAndOrderByArrivalTime(@Param("cityId") Long cityId, @Param("startTime") LocalDateTime startTime, @Param("stopTime") LocalDateTime stopTime, Pageable pageable);

    @Query(value = "" +
            "select count(a) from StopTime a " +
            "join a.stop b " +
            "where b.id = :cityId " +
            "and a.departureTime between :startTime and :stopTime ")
    int countByCityIdAndDepartureTimeBetweenAndOrderByArrivalTime(@Param("cityId") Long cityId, @Param("startTime") LocalDateTime startTime, @Param("stopTime") LocalDateTime stopTime);

}

