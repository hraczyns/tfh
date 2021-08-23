package com.hraczynski.trains.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/estimation")
    public ResponseEntity<PriceResponse> getPriceResponse(@RequestParam(name = "ids") String stopTimeIds) {
        return new ResponseEntity<>(priceService.getPrice(stopTimeIds), HttpStatus.OK);
    }

}
