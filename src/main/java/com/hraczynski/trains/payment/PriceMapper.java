package com.hraczynski.trains.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PriceMapper {

    public Set<PriceDTO> entitiesToDTOs(Set<Price> prices) {
        log.info("Mapping prices to dtos");
        return prices.stream()
                .map(price -> new PriceDTO(
                                price.getPassengerId(),
                                price.getName(),
                                price.getSurname(),
                                price.getPrice(),
                                price.getDiscount(),
                                getMappedPartPrices(price.getPartPrices())
                        )
                )
                .collect(Collectors.toSet());
    }

    private List<PartPriceDTO> getMappedPartPrices(List<PartPrice> partPrices) {
        return partPrices.stream()
                .map(partPrice -> new PartPriceDTO(
                                partPrice.getPrice(),
                                partPrice.getStartStopTime().getId(),
                                partPrice.getEndStopTime().getId()
                        )
                )
                .collect(Collectors.toList());
    }

}
