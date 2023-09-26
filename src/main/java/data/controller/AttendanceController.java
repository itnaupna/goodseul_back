package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.AttendanceResponseDto;
import data.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/lv1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<AttendanceResponseDto> checkAttendance(@RequestBody JsonNode json) {
        return new ResponseEntity<AttendanceResponseDto>(attendanceService.returnData(json.get("userIdx").asInt()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Integer> attend(@RequestBody JsonNode json) {
        return new ResponseEntity<Integer>(attendanceService.attend(json.get("position").asInt(),json.get("userIdx").asInt()),HttpStatus.OK);
    }
}
