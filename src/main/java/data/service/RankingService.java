package data.service;

import data.dto.RankResponseDto;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RankingService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final String RANKING_PREFIX = "game:ranking:";
    private static final String WEEKLY_RANKING_PREFIX = "game:weekly-ranking:";
    private final RedisTemplate<String, Object> redisTemplate;

    public RankingService(JwtService jwtService, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public void submitScoreWithHash(String gameId, double score, HttpServletRequest request, int orderBy) {

        String userId = String.valueOf(jwtService.extractIdxFromRequest(request));

        // 유저의 현재 점수를 가져옵니다.
        Double currentScore = redisTemplate.opsForZSet().score(RANKING_PREFIX + gameId, userId);

        // 유저의 주간 점수를 가져옵니다.
        Double weekScore = redisTemplate.opsForZSet().score(WEEKLY_RANKING_PREFIX + gameId, userId);

        // orderBy 값에 따라 조건을 분기합니다.
        boolean shouldUpdateTotal;
        boolean shouldUpdateWeek;
        if (orderBy == 1) {
            shouldUpdateTotal = (currentScore == null || score < currentScore); // 낮은 점수가 높은 랭크인 경우
            shouldUpdateWeek = (weekScore == null || score < weekScore);
        } else {
            shouldUpdateTotal = (currentScore == null || score > currentScore); // 높은 점수가 높은 랭크인 경우
            shouldUpdateWeek = (weekScore == null || score > weekScore);
        }

        if (shouldUpdateTotal) {
            redisTemplate.opsForZSet().add(RANKING_PREFIX + gameId, userId, score);

            // 새로운 점수가 저장되었으므로 타임스탬프도 갱신합니다.
            String hashKey = RANKING_PREFIX + gameId + ":timestamps";
            redisTemplate.opsForHash().put(hashKey, userId, Long.toString(System.currentTimeMillis()));
        }

        if (shouldUpdateWeek) {
            redisTemplate.opsForZSet().add(WEEKLY_RANKING_PREFIX + gameId, userId, score);
            redisTemplate.expire(WEEKLY_RANKING_PREFIX + gameId,7, TimeUnit.DAYS);

            // 새로운 점수가 저장되었으므로 타임스탬프도 갱신합니다.
            String hashKey = WEEKLY_RANKING_PREFIX + gameId + ":timestamps";
            redisTemplate.opsForHash().put(hashKey, userId, Long.toString(System.currentTimeMillis()));
            redisTemplate.expire(hashKey,7, TimeUnit.DAYS);
        }
    }

    public List<RankResponseDto> getTopRankings(String gameId, int limit, HttpServletRequest request, int orderBy) {
        String userIdx = String.valueOf(jwtService.extractIdxFromRequest(request));

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
            String userNickname = userRepository.findByIdx(Long.parseLong(userId)).get().getNickname();
            double score = tuple.getScore();

            // Hash에서 타임스탬프 조회
            long timestamp = Long.parseLong((String) redisTemplate.opsForHash().get(RANKING_PREFIX + gameId + ":timestamps", userId));

            result.add(new RankResponseDto(userNickname, rank++, score, timestamp));
        }

        if(redisTemplate.opsForZSet().score(RANKING_PREFIX + gameId, userIdx) != null) {
            result.add(getUserRanking(gameId, userIdx,orderBy));
        }

        return result;
    }

    public List<RankResponseDto> getWeekRankings(String gameId, int limit, HttpServletRequest request, int orderBy) {
        String userIdx = String.valueOf(jwtService.extractIdxFromRequest(request));

        Set<ZSetOperations.TypedTuple<Object>> rankings;

        // 기준에 따라 오름차순, 내림차순을 결정합니다.
        if(orderBy == 1) {
            rankings = redisTemplate.opsForZSet()
                    .rangeWithScores(WEEKLY_RANKING_PREFIX + gameId, 0, limit - 1);
        } else {
            rankings = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(WEEKLY_RANKING_PREFIX + gameId, 0, limit - 1);
        }

        List<RankResponseDto> result = new ArrayList<>();

        int rank = 1;

        // 1. 현재 시간에서 7일 (일주일)을 뺀 타임스탬프를 구합니다.

        for (ZSetOperations.TypedTuple<Object> tuple : rankings) {
            String userId = (String) tuple.getValue();
            String userNickname = userRepository.findByIdx(Long.parseLong(userId)).get().getNickname();
            double score = tuple.getScore();

            // Hash에서 타임스탬프 조회
            long timestamp = Long.parseLong((String) redisTemplate.opsForHash().get(WEEKLY_RANKING_PREFIX + gameId + ":timestamps", userId));

            // 2. 타임스탬프가 일주일 이내인지 확인
                result.add(new RankResponseDto(userNickname, rank++, score, timestamp));
        }

        if(redisTemplate.opsForZSet().score(WEEKLY_RANKING_PREFIX + gameId, userIdx) != null) {
            result.add(getUserWeekRanking(gameId, userIdx, orderBy));
        }

        return result;
    }


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

        String userNickname = userRepository.findByIdx(Long.parseLong(userId)).get().getNickname();

        return new RankResponseDto(userNickname, (int) (rank + 1), score, timestamp);  // Redis의 rank는 0부터 시작하므로 1을 더함
    }

    public RankResponseDto getUserWeekRanking(String gameId, String userId, int orderBy) {
        Long rank;
        if (orderBy == 1) {
            // 오름차순 랭킹: 낮은 점수부터
            rank = redisTemplate.opsForZSet().rank(WEEKLY_RANKING_PREFIX + gameId, userId);
        } else {
            // 내림차순 랭킹: 높은 점수부터 (기존 로직)
            rank = redisTemplate.opsForZSet().reverseRank(WEEKLY_RANKING_PREFIX + gameId, userId);
        }
        Double score = redisTemplate.opsForZSet().score(WEEKLY_RANKING_PREFIX + gameId, userId);

        // Hash에서 타임스탬프 조회
        long timestamp = Long.parseLong((String) redisTemplate.opsForHash().get(WEEKLY_RANKING_PREFIX + gameId + ":timestamps", userId));

        String userNickname = userRepository.findByIdx(Long.parseLong(userId)).get().getNickname();

        return new RankResponseDto(userNickname, (int) (rank + 1), score, timestamp);  // Redis의 rank는 0부터 시작하므로 1을 더함
    }

}
