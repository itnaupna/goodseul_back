package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.OfferDto;
import data.service.OfferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/lv1/offer")
@Api(value = "견적 관리", description = "Offer Controller", tags = "견적 API")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    @ApiOperation(value = "새로운 견적 설정")
    private ResponseEntity<String> setOffer(
            HttpServletRequest request,
            @ApiParam(value = "견적 상세", required = true) @RequestBody OfferDto dto
    ) {
        return new ResponseEntity<>(offerService.setOffer(request,dto), HttpStatus.OK);
    }

    @GetMapping("/my")
    @ApiOperation(value = "내 견적 리스트 검색")
    private ResponseEntity<List<OfferDto>> getList(
            HttpServletRequest request,
            @ApiParam(value = "페이지 번호", defaultValue = "0") @RequestParam(defaultValue = "0") int page
    ) {
        return new ResponseEntity<>(offerService.getMyOfferList(request,page),HttpStatus.OK);
    }

    @GetMapping("/list")
    @ApiOperation(value = "구슬이 받은 견적 리스트 검색")
    private ResponseEntity<List<OfferDto>> getWeekList(
            HttpServletRequest request,
            @ApiParam(value = "페이지 번호", defaultValue = "0") @RequestParam(defaultValue = "0") int page
    ) {
        return new ResponseEntity<>(offerService.getWeekOfferList(request, page),HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "견적 삭제")
    private ResponseEntity<String> deleteOffer(
            HttpServletRequest request,
            @ApiParam(value = "삭제할 견적의 인덱스", required = true) @RequestBody JsonNode jsonNode
    ) {
        offerService.deleteOffer(request,jsonNode.get("offerIdx").asInt());
        return new ResponseEntity<>(jsonNode.get("offerIdx").asText() + " 삭제 완료",HttpStatus.OK);
    }

    @PatchMapping
    @ApiOperation(value = "견적의 작성 날짜 업데이트 (기간이 만료된 견적의 최신화)")
    private ResponseEntity<String> updateWriteDate(
            HttpServletRequest request,
            @ApiParam(value = "기간이 만료된 견적의 인덱스", required = true) @RequestBody JsonNode jsonNode
    ) {
        offerService.updateWriteDate(request,jsonNode.get("offerIdx").asInt());
        return new ResponseEntity<>(jsonNode.get("offerIdx").asText() + " 작성일자 업데이트 완료",HttpStatus.OK);
    }
}
