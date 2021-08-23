package com.hraczynski.trains.reservations;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    @Query(value = "" +
            "select distinct a from Reservation a " +
            "join fetch a.trains")
    Set<Reservation> findAll();

    Optional<Reservation> findById(Long id);

    void deleteById(Long id);
}
