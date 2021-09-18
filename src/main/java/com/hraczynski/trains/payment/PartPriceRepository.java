package com.hraczynski.trains.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartPriceRepository extends JpaRepository<PartPrice,Long> {
}
