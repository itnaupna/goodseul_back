package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.RankResponseDto;
import data.exception.TestException;
import data.service.RankingService;
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
@Slf4j
@RequestMapping("/api/lv1/rank")
@Api(tags = "게임 랭킹 API")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping
    @ApiOperation(value = "점수 입력 API", notes = "게임의 점수를 등록합니다.")
    public ResponseEntity<Void> submitScore(
            @ApiParam(value = "Json body containing game index, score and order", required = true) @RequestBody JsonNode jsonNode,
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request) {
        rankingService.submitScoreWithHash(jsonNode.get("gameIdx").asText(),jsonNode.get("score").asDouble(),request, jsonNode.get("orderBy").asInt());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/week")
    @ApiOperation(value = "주간 랭킹 조회 API", notes = "특정 게임의 주간 랭킹을 조회합니다.")
    public ResponseEntity<List<RankResponseDto>> getWeekRank(
            @ApiParam(value = "Game index for ranking", required = true) String gameIdx,
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request,
            @ApiParam(value = "Order by value for ranking", required = true) int orderBy) {
        return new ResponseEntity<>(rankingService.getWeekRankings(gameIdx,10, request, orderBy),HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "전체 누적 랭킹 조회 API", notes = "특정 게임의 누적 랭킹을 조회합니다.")
    public ResponseEntity<List<RankResponseDto>> getAllRank(
            @ApiParam(value = "Game index for ranking", required = true) String gameIdx,
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request,
            @ApiParam(value = "Order by value for ranking", required = true) int orderBy) {
        return new ResponseEntity<>(rankingService.getTopRankings(gameIdx,10, request, orderBy),HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        throw new TestException();
    }
}
