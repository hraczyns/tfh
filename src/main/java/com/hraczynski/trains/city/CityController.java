package com.hraczynski.trains.city;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/cities", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CityController {
    private final CityService cityService;
    private final CityRepresentationModelAssembler assembler;

    @GetMapping(path = "/{id}")
    public ResponseEntity<CityDto> findById(@PathVariable Long id) {
        City byId = cityService.findById(id);
        CityDto cityDto = assembler.toModel(byId);
        return new ResponseEntity<>(cityDto, HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<CollectionModel<CityDto>> findAll() {
        Set<City> all = cityService.findAll();
        CollectionModel<CityDto> dtos = assembler.toCollectionModel(all);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CityDto> addCity(@Valid @RequestBody CityRequest request) {
        City save = cityService.save(request);
        CityDto cityDto = assembler.toModel(save);
        return new ResponseEntity<>(cityDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CityDto> deleteById(@PathVariable Long id) {
        City city = cityService.deleteById(id);
        CityDto cityDto = assembler.toModel(city);
        return new ResponseEntity<>(cityDto, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        cityService.update(id,request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Void> patchById(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        cityService.patch(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
