package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.AttendanceResponseDto;
import data.service.AttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Api(tags = "출석 API")
@RequestMapping("/api/lv1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    @ApiOperation(value = "출석 여부 확인 API", notes = "사용자의 출석 여부를 확인합니다.")
    public ResponseEntity<AttendanceResponseDto> checkAttendance(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request) {
        return new ResponseEntity<AttendanceResponseDto>(attendanceService.returnData(request), HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "출석 등록 API", notes = "사용자의 출석을 등록합니다.")
    public ResponseEntity<Integer> attend(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request,
            @ApiParam(value = "Json body containing position data", required = true) @RequestBody JsonNode json) {
        return new ResponseEntity<Integer>(attendanceService.attend(json.get("position").asInt(),request),HttpStatus.OK);
    }
}
