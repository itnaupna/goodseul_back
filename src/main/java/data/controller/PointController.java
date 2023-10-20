package data.controller;

import data.dto.PointDto;
import data.service.PointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
@Api(value = "포인트 관리", description = "Point Controller", tags = "포인트 API")
@RequestMapping("/api/lv1/point")
public class PointController {

    @Autowired
    public  PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // 적립 내역
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPointPage(@ApiParam(value = "페이지 번호", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                            @ApiParam(value = "페이지 당 보여지는 갯수", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
                                                              HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("pointIdx").descending());
        return new ResponseEntity<>(pointService.getPagePoint(request, pageRequest), HttpStatus.OK);

    }

    @ApiOperation(value = "총 포인트 조회")
    @GetMapping("/total")
    public int getTotalPoint (HttpServletRequest request) {
        return pointService.getTotalPoint(request);
    }

    @ApiOperation(value = "적립")
    @PostMapping
    public ResponseEntity<String> addPoint (@ApiParam(value = "포인트 DTO") @RequestBody PointDto dto, HttpServletRequest request) {
        try {
            pointService.addPointEvent(dto, request);
            return ResponseEntity.ok("적립 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @ApiOperation(value = "사용")
    @PostMapping("/use")
    public String usePoint(@ApiParam(value = "포인트 DTO") @RequestBody PointDto dto, HttpServletRequest request) {
        return pointService.usePoint(request, dto.getPoint(), dto.getComment());
    }

    @ApiOperation(value = "포인트 사용 취소")
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPointUse(@ApiParam(value = "포인트 idx") @RequestParam int pointIdx) {
        try {
            pointService.cancelPointUse(pointIdx);
            return ResponseEntity.ok("사용 취소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @ApiOperation(value = "포인트 만료")
    @PostMapping("/expire")
    public ResponseEntity<String> expirePoint () {
        try {
            pointService.expirePoint();
            return ResponseEntity.ok("유효기간만료 완료");
        } catch(Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }


}
