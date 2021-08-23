package com.hraczynski.trains.trip;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TripsRepository extends PagingAndSortingRepository<Trip, Long> {
    @Query(value = "" +
            "select distinct a from Trip a " +
            "join fetch a.stopTimes b " +
            "join fetch b.stop c " +
            "join fetch c.country d " +
            "join fetch a.train")
    Set<Trip> findAll();

    Optional<Trip> findById(Long id);

    Set<Trip> findTripByTrainId(Long id);

    @Query(value = "" +
            "select a from Trip a " +
            "join fetch a.train d " +
            "join fetch a.stopTimes b " +
            "join fetch b.stop c " +
            "join fetch c.country " +
            "where b.id = :stopid "
    )
    Optional<Trip> findTripByStopTimesId(@Param("stopid") Long id);

}
