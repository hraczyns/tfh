package com.hraczynski.trains.city;

import lombok.RequiredArgsConstructor;
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
@CrossOrigin(origins = "http://localhost:3000")
public class CityController {
    private final CityService cityService;
    private final CityRepresentationModelAssembler assembler;

    @GetMapping(path = "/{id}")
    public ResponseEntity<CityDTO> findById(@PathVariable Long id) {
        City byId = cityService.findById(id);
        CityDTO cityDTO = assembler.toModel(byId);
        return new ResponseEntity<>(cityDTO, HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<CollectionModel<CityDTO>> findAll() {
        Set<City> all = cityService.findAll();
        CollectionModel<CityDTO> dtos = assembler.toCollectionModel(all);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CityDTO> addCity(@Valid @RequestBody CityRequest request) {
        City save = cityService.save(request);
        CityDTO cityDTO = assembler.toModel(save);
        return new ResponseEntity<>(cityDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CityDTO> deleteById(@PathVariable Long id) {
        City city = cityService.deleteById(id);
        CityDTO cityDTO = assembler.toModel(city);
        return new ResponseEntity<>(cityDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody CityRequest request) {
        cityService.update(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patchById(@Valid @RequestBody CityRequest request) {
        cityService.patch(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
