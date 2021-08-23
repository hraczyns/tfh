package com.hraczynski.trains.payment;

import com.hraczynski.trains.exceptions.definitions.BadStopTimeIdsFormatRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    public PriceResponse getPrice(String stopTimeIds) {
        log.info("Start calculating price for ids in ({})", stopTimeIds);

        List<Long> stopTimeList = parseToList(stopTimeIds);
        PriceResponse res = new PriceResponse();
        for (int i = 1; i < stopTimeList.size(); i += 2) {
            Long id = stopTimeList.get(i);
            Long idPrev = stopTimeList.get(i - 1);
            PriceResolver resolver = new PriceResolver.PriceResolverBuilder()
                    .withStopTimeIds(Arrays.asList(id, idPrev))
                    .build();
            BigDecimal calculatePrice = resolver.calculatePrice();
            addToResponse(res, id, idPrev, calculatePrice);
        }
        log.info("Started calculated general price");
        res.setPriceInGeneral(getPriceInGeneral(res));
        log.info("Calculations were performed successfully");
        return res;
    }

    private BigDecimal getPriceInGeneral(PriceResponse res) {
        return res.getPrices().stream()
                .map(PricePartResponse::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private List<Long> parseToList(String stopTimeIds) {
        if (stopTimeIds == null) {
            throw new BadStopTimeIdsFormatRequestException();
        }
        String[] ids = stopTimeIds.split(",");
        if (ids.length == 0 || ids.length % 2 != 0) {
            throw new BadStopTimeIdsFormatRequestException(ids);
        }
        return Arrays.stream(ids)
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
    }

    private void addToResponse(PriceResponse res, Long id, Long idPrev, BigDecimal calculatedPrice) {
        PricePartResponse partResponse = new PricePartResponse();
        partResponse.setPrice(calculatedPrice);
        partResponse.setStartId(idPrev);
        partResponse.setStopId(id);
        if (res.getPrices() == null) {
            res.setPrices(new ArrayList<>());
        }
        res.getPrices().add(partResponse);
    }
}
