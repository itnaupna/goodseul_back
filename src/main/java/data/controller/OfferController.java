package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.OfferDto;
import data.service.OfferService;
import kotlinx.serialization.json.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/lv1/offer")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    private ResponseEntity<String> setOffer(@RequestBody OfferDto dto) {
        return new ResponseEntity<>(offerService.setOffer(dto), HttpStatus.OK);
    }

    @GetMapping("/my")
    private ResponseEntity<List<OfferDto>> getList(HttpServletRequest request) {
        return new ResponseEntity<>(offerService.getMyOfferList(request),HttpStatus.OK);
    }

    @GetMapping("/list")
    private ResponseEntity<List<OfferDto>> getWeekList(HttpServletRequest request, @RequestParam(defaultValue = "0") int page) {
        return new ResponseEntity<>(offerService.getWeekOfferList(request, page),HttpStatus.OK);
    }

    @DeleteMapping
    private ResponseEntity<String> deleteOffer(HttpServletRequest request, @RequestBody JsonNode jsonNode) {
        offerService.deleteOffer(request,jsonNode.get("offerIdx").asInt());
        return new ResponseEntity<>(jsonNode.get("offerIdx").asText() + "삭제 완료",HttpStatus.OK);
    }
    
    @PatchMapping
    private ResponseEntity<String> updateWriteDate(HttpServletRequest request, @RequestBody JsonNode jsonNode) {
        offerService.updateWriteDate(request,jsonNode.get("offerIdx").asInt());
        return new ResponseEntity<>(jsonNode.get("offerIdx").asText() + "작성일자 업데이트 완료",HttpStatus.OK);
    }


}
