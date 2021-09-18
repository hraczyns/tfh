package com.hraczynski.trains.city;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/cities", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins = "http://localhost:3000")
public class CityController {
    private final CityService cityService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<CityDTO> findById(@PathVariable Long id) {
        CityDTO byId = cityService.getById(id);
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<CollectionModel<CityDTO>> findAll() {
        CollectionModel<CityDTO> all = cityService.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CityDTO> addCity(@Valid @RequestBody CityRequest request) {
        CityDTO save = cityService.save(request);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CityDTO> deleteById(@PathVariable Long id) {
        CityDTO cityDTO = cityService.deleteById(id);
        return new ResponseEntity<>(cityDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody CityRequest request) {
        CityDTO cityDTO = cityService.update(request);
        if (cityDTO == null) {
            throw new EntityNotFoundException(City.class, "id = " + request.getId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<Void> patchById(@Valid @RequestBody CityRequest request) {
        CityDTO cityDTO = cityService.patchById(request);
        if (cityDTO == null) {
            throw new EntityNotFoundException(City.class, "id = " + request.getId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
