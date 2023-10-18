package data.service;

import data.dto.PointDto;
import data.dto.PointHistoryDto;
import data.entity.PointEntity;
import data.entity.PointHistoryEntity;
import data.entity.UserEntity;
import data.repository.PointHistoryRepository;
import data.repository.PointRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import java.util.*;

@Service
@Slf4j
public class PointService {
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private  final JwtService jwtService;
    @Autowired
    public PointService(PointRepository pointRepository, PointHistoryRepository pointHistoryRepository, JwtService jwtService, UserRepository userRepository) {
        this.pointRepository = pointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // 포인트 이용 내역
    public Map<String, Object> getPagePoint (HttpServletRequest request, Pageable pageRequest) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();

        Page<PointDto> result =  pointRepository.findByUserEntity_idx(idx, pageRequest).map(PointDto::toPointDto);

        Map<String, Object> response = new HashMap<>();
        response.put("pointList", result.getContent() );
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }

    public int getTotalPoint (HttpServletRequest request) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        log.info("idx = " + idx);
        return  pointHistoryRepository.findTotalPointsByMemberIdx(idx);
    }


    // 포인트 적립 (A)
    @Transactional
    public void addPointEvent(PointDto dto, HttpServletRequest request) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        int score = dto.getPoint();

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // point 추가
        PointEntity point = new PointEntity();
        point.setUserEntity(user);
        point.setPoint(score);
        point.setComment(dto.getComment());
        point.setType("A");

        PointEntity savedPointEvent = pointRepository.save(point);

        // PointHistory 추가
        PointHistoryEntity pointHistory = new PointHistoryEntity();
        pointHistory.setUserEntity(user);
        pointHistory.setPoint(score);
        pointHistory.setType("적립");
        pointHistory.setOriginIdx(savedPointEvent.getPointIdx());
        pointHistory.setPointEntity(savedPointEvent);

        pointHistoryRepository.save(pointHistory);
    }

    private void savePointHistory(int pointIdx, long memberIdx, int point, java.sql.Date expireDate, int originIdx) {
        PointHistoryDto pointHistory = new PointHistoryDto();
        pointHistory.setPoint_idx(pointIdx);
        pointHistory.setMember_idx(memberIdx);
        pointHistory.setType("사용");
        pointHistory.setPoint(point);
        pointHistory.setExpire_date(expireDate);
        pointHistory.setOrigin_idx(originIdx);
        pointHistoryRepository.save(PointHistoryEntity.toPointHistoryEntity(pointHistory));
    }
    // 포인트 사용 (U)
    @Transactional
    public String usePoint(HttpServletRequest request, int point, String comment) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        try {

            // 사용자 정보 조회
            UserEntity user = userRepository.findById(idx)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

            // 사용자의 잔여 포인트 조회
            int totalPoint = pointHistoryRepository.findTotalPointsByMemberIdx(idx);

            // 잔여 포인트 부족
            if (totalPoint < point) {
                return "F";
            }
            // 포인트 이벤트 생성 및 포인트 디테일 생성
            PointDto pointEvent = new PointDto();
            pointEvent.setMember_idx(idx);
            pointEvent.setType("U");
            pointEvent.setPoint(-point);
            pointEvent.setComment(comment);

            // PointEntity 생성 시 userEntity 설정
            PointEntity pointEntity = PointEntity.toPointEntity(pointEvent, user);
            pointEntity = pointRepository.save(pointEntity);
            // 사용 가능한 포인트를 그룹별로 가져옴
            List<Object[]> groupPoints = pointHistoryRepository.findNonZeroGroupedByPointId(idx);

            for (Object[] groupPoint : groupPoints) {
                Integer pointId = (Integer) groupPoint[0];
                Integer availablePoints = ((Long) groupPoint[1]).intValue();
                java.sql.Date expireDate = (java.sql.Date) groupPoint[2];

                if (point <= 0)
                    break;

                int deduction = Math.min(availablePoints, point);
                savePointHistory((Integer) pointId, idx, -deduction, expireDate, pointEntity.getPointIdx());

                point -= deduction;
            }
            return "U";
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            return "E";
        }
    }
    // 포인트 취소 (C)
    @Transactional
    public void cancelPointUse(int pointIdx) {

        Optional<PointEntity> optionalPointEvent = pointRepository.findById(pointIdx);

        if (!optionalPointEvent.isPresent()) {
            log.info(optionalPointEvent + "포인트 테이블");
            return;
        }

        // 이미 취소 됐는지 검증
        if(pointHistoryRepository.existsByOriginIdxAndType(pointIdx, "취소")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 취소 됐다");
        }

        PointEntity existingEvent = optionalPointEvent.get();

        // U인 경우에만 취소 처리 진행
        if ("U".equals(existingEvent.getType())) {

            PointEntity cancelEvent = new PointEntity();
            UserEntity userEntity = existingEvent.getUserEntity(); // 수정된 부분
            cancelEvent.setUserEntity(userEntity); // 수정된 부분
            cancelEvent.setType("C");
            cancelEvent.setPoint(Math.abs(existingEvent.getPoint())); // 절대값 반환
            cancelEvent.setComment("취소");

            PointEntity savedCancelEvent = pointRepository.save(cancelEvent);

            List<PointHistoryEntity> pointHistory = pointHistoryRepository.findByOriginIdx(existingEvent.getPointIdx());

            for (PointHistoryEntity history : pointHistory) {
                PointHistoryEntity cancelHistory = new PointHistoryEntity();
                cancelHistory.setPointEntity(history.getPointEntity());
                cancelHistory.setPoint(Math.abs(history.getPoint()));
                cancelHistory.setExpireDate(history.getExpireDate());
                cancelHistory.setOriginIdx(savedCancelEvent.getPointIdx());
                cancelHistory.setType("취소");
                cancelHistory.setUserEntity(userEntity);
                pointHistoryRepository.save(cancelHistory);
            }
        }

    }

    // 유효 기간 만료 (E)
    @Transactional
    public void expirePoint() {
        List<Object[]> pointExpire = pointHistoryRepository.findAllExpiredPoints();

        if (pointExpire.isEmpty()) {
            log.info("유효기간 만료된 것이 없어용");
            return;
        }

        for (Object[] expire : pointExpire) {
            PointHistoryEntity expireEntity = (PointHistoryEntity) expire[0];
            int sumPoint = ((Number) expire[1]).intValue();

            UserEntity userEntity = expireEntity.getUserEntity();

            PointEntity expirePoint = new PointEntity();
            expirePoint.setUserEntity(userEntity);
            expirePoint.setType("E");
            expirePoint.setPoint(-sumPoint);
            expirePoint.setComment("유효기간만료");
            pointRepository.save(expirePoint);

            PointHistoryEntity expireHistory = new PointHistoryEntity();
            expireHistory.setUserEntity(userEntity);
            expireHistory.setPoint(-sumPoint);
            expireHistory.setExpireDate(expireEntity.getExpireDate());
            expireHistory.setOriginIdx(expirePoint.getPointIdx());
            expireHistory.setPointEntity(expireEntity.getPointEntity());
            expireHistory.setType("유효기간만료");
            pointHistoryRepository.save(expireHistory);
        }
    }



}
