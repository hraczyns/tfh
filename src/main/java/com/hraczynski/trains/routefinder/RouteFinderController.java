package com.hraczynski.trains.routefinder;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hraczynski.trains.journey.JourneyDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins = "http://localhost:3006")
public class RouteFinderController {
    private final RouteFinderService routeFinderService;

    @GetMapping
    public ResponseEntity<CollectionModel<JourneyDTO>> getRoute(@RequestParam(name = "source") Long source, @RequestParam(name = "destination") Long dest, @RequestParam(name = "startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime, @RequestParam(name = "results", required = false, defaultValue = "5") Integer results) {
        CollectionModel<JourneyDTO> routeList;
        if (results != null) {
            routeList = routeFinderService.findManyRoutes(source, dest, startTime, results);
            return new ResponseEntity<>(routeList, HttpStatus.OK);
        }

        CollectionModel<JourneyDTO> route = routeFinderService.findRoute(source, dest, startTime);
        return new ResponseEntity<>(route, HttpStatus.OK);
    }

}

