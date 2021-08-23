package com.hraczynski.trains.country;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
    Optional<Country> findCountryByName(String country);
}
