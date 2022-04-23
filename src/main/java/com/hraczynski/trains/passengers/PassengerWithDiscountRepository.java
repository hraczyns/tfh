package com.hraczynski.trains.passengers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerWithDiscountRepository extends JpaRepository<PassengerWithDiscount, Long> {
}
