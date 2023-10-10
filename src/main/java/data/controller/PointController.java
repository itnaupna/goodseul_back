package data.controller;

import data.dto.PointDto;
import data.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class PointController {

    @Autowired
    public  PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // 리스트
    @GetMapping("/point")
    public ResponseEntity<Map<String, Object>> getPointPage(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam int memberIdx) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("pointIdx").descending());
        return new ResponseEntity<>(pointService.getPagePoint(memberIdx, pageRequest), HttpStatus.OK);

    }

    // 총 포인트
    @GetMapping("/total")
    public int getTotalPoint (@RequestParam int member_idx) {
        return pointService.getTotalPoint(member_idx);
    }


    // 적립
    @PostMapping("/point")
    public ResponseEntity<String> addPoint (@RequestBody PointDto dto) {
        try {
            pointService.addPointEvent(dto);
            return ResponseEntity.ok("적립 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    // 사용
    @PostMapping("/use")
    public String usePoint(@RequestBody PointDto dto) {
        return pointService.usePoint(dto.getMember_idx(), dto.getPoint(), dto.getComment());
    }

    // 취소
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPointUse(@RequestParam int pointIdx) {
        try {
            pointService.cancelPointUse(pointIdx);
            return ResponseEntity.ok("사용 취소 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    // 만료
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
