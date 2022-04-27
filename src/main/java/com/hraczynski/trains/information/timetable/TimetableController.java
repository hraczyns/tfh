package com.hraczynski.trains.information.timetable;

import com.hraczynski.trains.stoptime.StopTimeExtendedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/information/timetable", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class TimetableController {

    private final TimetableService timetableService;

    @GetMapping("/city")
    public ResponseEntity<Page<StopTimeExtendedDto>> getTimetableForCity(@RequestParam("id") Long cityId, @RequestParam(value = "startTime", required = false) String localDateTime, @RequestParam(value = "endTime", required = false) String localDateTimeEnd, @RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "results", defaultValue = "5") Integer results) {
        Page<StopTimeExtendedDto> timetableForCity = timetableService.getTimetableForCity(cityId, localDateTime, localDateTimeEnd, page, results);
        if (!timetableForCity.isEmpty()) {
            return new ResponseEntity<>(timetableForCity, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
