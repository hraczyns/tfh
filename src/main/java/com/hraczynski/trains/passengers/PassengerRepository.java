package com.hraczynski.trains.passengers;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger, Long> {
    Set<Passenger> findAll();

    Optional<Passenger> findById(Long id);

    void deleteById(Long aLong);
}
