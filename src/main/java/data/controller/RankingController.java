package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.RankResponseDto;
import data.service.RankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/lv1/rank")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping
    public ResponseEntity<Void> submitScore(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        rankingService.submitScoreWithHash(jsonNode.get("gameIdx").asText(),jsonNode.get("score").asDouble(),request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RankResponseDto>> getRanking (String gameIdx, HttpServletRequest request, int orderBy) {
        return new ResponseEntity<>(rankingService.getTopRankings(gameIdx,10, request, orderBy),HttpStatus.OK);
    }


}
