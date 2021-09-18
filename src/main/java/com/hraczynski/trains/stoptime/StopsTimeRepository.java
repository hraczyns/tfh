package com.hraczynski.trains.stoptime;

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
            "where b.id = :cityId " +
            "and a.arrivalTime > :startTime " +
            "order by a.arrivalTime")
    List<StopTime> findByCityIdAndArrivalTimeGreaterThanOrderByArrivalTime(@Param("cityId") Long cityId, @Param("startTime") LocalDateTime startTime);
}

