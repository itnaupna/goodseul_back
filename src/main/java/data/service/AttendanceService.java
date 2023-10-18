package data.service;

import data.dto.AttendanceResponseDto;
import data.dto.PointDto;
import data.entity.AttendanceEntity;
import data.entity.UserEntity;
import data.repository.AttendanceRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;


@Service
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final PointService pointService;

    public AttendanceService(AttendanceRepository attendanceRepository, JwtService jwtService, UserRepository userRepository, PointService pointService) {
        this.attendanceRepository = attendanceRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.pointService = pointService;
    }

    public boolean checkAttendance(HttpServletRequest request) {
        AttendanceEntity attendanceEntity = getAttendanceEntity(request);

        if(attendanceEntity.getLastAttendance() == null) {
            return false;
        } else {
            return attendanceEntity.getLastAttendance().toLocalDateTime().toLocalDate().equals(LocalDate.now());
        }
    }

    public AttendanceResponseDto returnData(HttpServletRequest request) {
        AttendanceResponseDto attendanceResponseDto = new AttendanceResponseDto();

        attendanceResponseDto.setAttend(checkAttendance(request));

        AttendanceEntity attendanceEntity = getAttendanceEntity(request);

        StringTokenizer st = new StringTokenizer(attendanceEntity.getAttendanceData());

        boolean isFull = true;

        for(int i = 0 ; i < 10; i++) {
            if(Integer.parseInt(st.nextToken()) == 0) {
                isFull = false;
            }
        }

        if(isFull && !attendanceResponseDto.isAttend()) {
            attendanceEntity.setAttendanceData("0 0 0 0 0 0 0 0 0 0");
            attendanceResponseDto.setPointData(attendanceEntity.getAttendanceData());
            attendanceRepository.save(attendanceEntity);
        } else {
            attendanceResponseDto.setPointData(attendanceEntity.getAttendanceData());
        }

        return attendanceResponseDto;

    }

    @NotNull
    private AttendanceEntity getAttendanceEntity(HttpServletRequest request) {
        long userIdx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        Optional<UserEntity> user = userRepository.findByIdx(userIdx);

        AttendanceEntity attendanceEntity = new AttendanceEntity();

        if(user.isPresent()){
            log.info("User data 조회 성공");
            attendanceEntity = getAttendanceEntity(user.get());
        } else {
            log.error("User data 존재하지않음.");
        }
        return attendanceEntity;
    }

    public int attend(int position, HttpServletRequest request) {

        if(checkAttendance(request)) {
           return -1;
        } else {
            AttendanceEntity attendanceEntity = getAttendanceEntity(request);
            StringTokenizer st = new StringTokenizer(attendanceEntity.getAttendanceData());
            int[] countPoint = {5, 2, 1, 1, 1};

            int[] point = new int[10];
            int returnedPoint = -1;

            for (int i = 0; i < 10; i++) {
                int checkPoint = Integer.parseInt(st.nextToken());

                switch (checkPoint) {
                    case 5:
                        countPoint[0]--;
                        break;
                    case 10:
                        countPoint[1]--;
                        break;
                    case 15:
                        countPoint[2]--;
                        break;
                    case 20:
                        countPoint[3]--;
                        break;
                    case 25:
                        countPoint[4]--;
                        break;
                }

                point[i] = checkPoint;
                if (position == i) {
                    if (checkPoint != 0) {
                        log.error("에러 발생 : 이미 선택된 위치 재 선택");
                    } else {
                        returnedPoint = getRandomPoint(countPoint);
                        log.info(Arrays.toString(point));
                        point[position] = returnedPoint;
                        StringBuilder sb = new StringBuilder();

                        for (int a : point) {
                            sb.append(a).append(" ");
                        }

                        if (sb.charAt(sb.length() - 1) == ' ') {
                            //마지막에 공백 삽입시 제거
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        attendanceEntity.setAttendanceData(sb.toString());
                        attendanceEntity.setLastAttendance(new Timestamp(System.currentTimeMillis()));

                        attendanceRepository.save(attendanceEntity);

                        PointDto pointDto = new PointDto();
                        pointDto.setPoint(returnedPoint);

                        pointService.addPointEvent(pointDto,request);
                    }
                }
            }
            return returnedPoint;
        }
    }

    private AttendanceEntity getAttendanceEntity(UserEntity user) {
        Optional<AttendanceEntity> attendanceEntityOptional = attendanceRepository.findAttendanceEntityByUserIdx(user.getIdx());

        AttendanceEntity attendanceEntity = new AttendanceEntity();
        if (attendanceEntityOptional.isPresent()) {
            attendanceEntity = attendanceEntityOptional.get();
        } else {
            log.info("해당 회원의 출석체크 기록없음.");
            attendanceEntity.setUser(user);
            attendanceEntity.setAttendanceData("0 0 0 0 0 0 0 0 0 0");
            attendanceRepository.save(attendanceEntity);
            log.info("새로운 출석체크 기록 저장 완료.");
        }

        return attendanceEntity;
    }

    private int getRandomPoint(int[] countPoint) {
        List<Integer> points = new ArrayList<>();

        for(int i = 1; i <= 5; i++) {
            for(int j = 0; j < countPoint[i-1]; j++) {
                points.add(5 * i);
            }
        }

        Collections.shuffle(points);

        return points.get(0); // 셔플 후 첫 번째 요소 얻기
    }


}
