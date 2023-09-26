package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.RankResponseDto;
import data.service.RankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> submitScore(@RequestBody JsonNode jsonNode) {
        rankingService.submitScoreWithHash(jsonNode.get("gameIdx").asText(),jsonNode.get("userIdx").asText(),jsonNode.get("score").asDouble());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RankResponseDto>> getRanking (String gameIdx, String userIdx) {
        return new ResponseEntity<>(rankingService.getTopRankings(gameIdx,userIdx,10),HttpStatus.OK);
    }


}
