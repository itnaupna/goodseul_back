package data.controller;

import data.dto.OfferDto;
import data.service.OfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/lv0/offer")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    private ResponseEntity<String> setOffer(@RequestBody OfferDto dto) {
        return new ResponseEntity<>(offerService.setOffer(dto), HttpStatus.OK);
    }
}
