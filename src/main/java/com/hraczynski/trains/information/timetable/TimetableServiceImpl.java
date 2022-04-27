package com.hraczynski.trains.information.timetable;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.InvalidRequestException;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeExtendedDto;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.hraczynski.trains.stoptime.StopsTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimetableServiceImpl implements TimetableService {

    private static final int PAGE_LIMIT = 50;

    private final StopsTimeRepository stopsTimeRepository;
    private final StopTimeMapper stopTimeMapper;

    @Override
    public Page<StopTimeExtendedDto> getTimetableForCity(Long cityId, String localDateTime, String localDateTimeEnd, int pageNum, int resultsNum) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            if (localDateTime != null) {
                startTime = LocalDateTime.parse(localDateTime);
                if (localDateTimeEnd != null) {
                    endTime = LocalDateTime.parse(localDateTimeEnd);
                } else {
                    endTime = LocalDateTime.parse(localDateTime).plusDays(1);
                }
            } else {
                startTime = LocalDateTime.now().minusMonths(1);
                endTime = LocalDateTime.now().plusMonths(1);
            }
        } catch (DateTimeParseException e) {
            log.error("Wrong startTime {} provided", localDateTime);
            throw new InvalidRequestException("Wrong startTime " + localDateTime + " provided");
        }

        if (resultsNum > PAGE_LIMIT) {
            resultsNum = PAGE_LIMIT;
        }

        log.info("Looking for timetable for city id = {}, startTime = {}, endTime = {}, results = {}, page = {}", cityId, startTime, endTime, resultsNum, pageNum);

        int total = stopsTimeRepository.countByCityIdAndDepartureTimeBetweenAndOrderByArrivalTime(cityId, startTime, endTime);
        PageRequest pageable = PageRequest.of(pageNum, resultsNum);
        List<StopTime> stopTimes = stopsTimeRepository.findByCityIdAndDepartureTimeBetweenAndOrderByArrivalTime(cityId, startTime, endTime, pageable);

        if (stopTimes.isEmpty()) {
            log.error("No timetable found for city id = {} and startTime between {} and {} ", cityId, startTime, endTime);
            throw new EntityNotFoundException(StopTime.class, "cityId = " + cityId, "startTime between " + startTime + " and " + endTime);
        }

        List<StopTimeExtendedDto> stopTimeExtendedDtos = stopTimes.stream()
                .map(stopTimeMapper::entityToExtendedDto)
                .collect(Collectors.toList());
        return new PageImpl<>(stopTimeExtendedDtos, pageable, total);
    }
}
