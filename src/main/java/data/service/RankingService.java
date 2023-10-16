package data.service;

import data.dto.RankResponseDto;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class RankingService {

    private final JwtService jwtService;
    private static final String RANKING_PREFIX = "game:ranking:";
    private final RedisTemplate<String, Object> redisTemplate;

    public RankingService(JwtService jwtService, RedisTemplate<String, Object> redisTemplate) {
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    public void submitScoreWithHash(String gameId, double score, HttpServletRequest request, int orderBy) {

        String userId = String.valueOf(jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get());

        // 유저의 현재 점수를 가져옵니다.
        Double currentScore = redisTemplate.opsForZSet().score(RANKING_PREFIX + gameId, userId);

        // orderBy 값에 따라 조건을 분기합니다.
        boolean shouldUpdate;
        if (orderBy == 1) {
            shouldUpdate = (currentScore == null || score < currentScore); // 낮은 점수가 높은 랭크인 경우
        } else {
            shouldUpdate = (currentScore == null || score > currentScore); // 높은 점수가 높은 랭크인 경우
        }

        if (shouldUpdate) {
            redisTemplate.opsForZSet().add(RANKING_PREFIX + gameId, userId, score);

            // 새로운 점수가 저장되었으므로 타임스탬프도 갱신합니다.
            String hashKey = RANKING_PREFIX + gameId + ":timestamps";
            redisTemplate.opsForHash().put(hashKey, userId, Long.toString(System.currentTimeMillis()));
        }
    }




//    public List<RankResponseDto> getRanking(String gameIdx, String userIdx) {
//
//        List<RankResponseDto> list = new LinkedList<>();
//
//        Set<ZSetOperations.TypedTuple<Object>> top10 = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(RANKING_PREFIX + gameIdx,0,Double.MAX_VALUE);
//
//        int rank = 1;
//
//        for(ZSetOperations.TypedTuple<Object> entry : Objects.requireNonNull(top10)) {
//
//            StringTokenizer st = new StringTokenizer((String) entry.getValue(),"/");
//
//            RankResponseDto dto = new RankResponseDto();
//            dto.setRank(rank++);
//            dto.setUserIdx(st.nextToken());
//            dto.setScore(entry.getScore());
//            dto.setDate(st.nextToken());
//
//            list.add(dto);
//
//            if(rank == 11) {
//                break;
//            }
//        }
//
//        list.add(getUserRankAndScore(gameIdx,userIdx));
//
//        return list;
//    }

    public List<RankResponseDto> getTopRankings(String gameId, int limit, HttpServletRequest request, int orderBy) {
        String userIdx = String.valueOf(jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get());

        Set<ZSetOperations.TypedTuple<Object>> rankings;

        // 기준에 따라 오름차순, 내림차순을 결정합니다.
        if(orderBy == 1) {
            // 오름차순(ASC) - 낮은 점수부터
            rankings = redisTemplate.opsForZSet()
                    .rangeWithScores(RANKING_PREFIX + gameId, 0, limit - 1);
        } else {
            // 내림차순(DESC) - 높은 점수부터
            rankings = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(RANKING_PREFIX + gameId, 0, limit - 1);
        }

        List<RankResponseDto> result = new ArrayList<>();

        int rank = 1;

        for (ZSetOperations.TypedTuple<Object> tuple : rankings) {
            String userId = (String) tuple.getValue();
            double score = tuple.getScore();

            // Hash에서 타임스탬프 조회
            long timestamp = Long.parseLong((String) redisTemplate.opsForHash().get(RANKING_PREFIX + gameId + ":timestamps", userId));

            result.add(new RankResponseDto(userId, rank++, (int) score, timestamp));
        }

        result.add(getUserRanking(gameId, userIdx,orderBy));

        return result;
    }



//    public RankResponseDto getUserRankAndScore(String gameIdx, String UserIdx) {
//        Long rank = redisTemplate.opsForZSet().reverseRank(RANKING_PREFIX + gameIdx, UserIdx);
//        Double score = redisTemplate.opsForZSet().score(RANKING_PREFIX + gameIdx, UserIdx);
//
//        if (rank == null || score == null) {
//            // 사용자가 랭킹에 없는 경우
//            return null;
//        }
//
//        StringTokenizer st = new StringTokenizer((String) UserIdx,"/");
//
//        RankResponseDto dto = new RankResponseDto();
//        dto.setRank(rank.intValue() + 1);  // 순위는 0부터 시작하므로 1을 더합니다.
//        dto.setUserIdx(UserIdx);
//
//        return dto;
//    }

    public RankResponseDto getUserRanking(String gameId, String userId, int orderBy) {
        Long rank;
        if (orderBy == 1) {
            // 오름차순 랭킹: 낮은 점수부터
            rank = redisTemplate.opsForZSet().rank(RANKING_PREFIX + gameId, userId);
        } else {
            // 내림차순 랭킹: 높은 점수부터 (기존 로직)
            rank = redisTemplate.opsForZSet().reverseRank(RANKING_PREFIX + gameId, userId);
        }
        Double score = redisTemplate.opsForZSet().score(RANKING_PREFIX + gameId, userId);

        // Hash에서 타임스탬프 조회
        long timestamp = Long.parseLong((String) redisTemplate.opsForHash().get(RANKING_PREFIX + gameId + ":timestamps", userId));

        return new RankResponseDto(userId, (int) (rank + 1), score.intValue(), timestamp);  // Redis의 rank는 0부터 시작하므로 1을 더함
    }

}
