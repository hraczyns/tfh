package com.hraczynski.trains.city;

import org.hibernate.annotations.QueryHints;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {
    Optional<City> findById(Long id);

    @org.springframework.data.jpa.repository.QueryHints(value = @QueryHint(name = QueryHints.PASS_DISTINCT_THROUGH, value = "false"))
    @Query(value = "" +
            "select distinct a from City a " +
            "join fetch a.country ")
    Set<City> findAll();

    void deleteById(Long id);

}
