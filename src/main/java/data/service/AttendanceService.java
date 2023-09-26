package data.service;

import data.dto.AttendanceResponseDto;
import data.entity.AttendanceEntity;
import data.repository.AttendanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;


@Service
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public boolean checkAttendance(int userIdx) {
        AttendanceEntity attendanceEntity = getAttendanceEntity(userIdx);

        if(attendanceEntity.getLastAttendance() == null) {
            return false;
        } else {
            return attendanceEntity.getLastAttendance().toLocalDateTime().toLocalDate().equals(LocalDate.now());
        }
    }

    public AttendanceResponseDto returnData(int userIdx) {
        AttendanceResponseDto attendanceResponseDto = new AttendanceResponseDto();

        attendanceResponseDto.setAttend(checkAttendance(userIdx));

        AttendanceEntity attendanceEntity = getAttendanceEntity(userIdx);

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

    public int attend(int position, int userIdx) {
        AttendanceEntity attendanceEntity = getAttendanceEntity(userIdx);
        StringTokenizer st = new StringTokenizer(attendanceEntity.getAttendanceData());
        int[] countPoint = {5,2,1,1,1};

        int[] point = new int[10];

        for(int i = 0; i < 10; i++) {
            int checkPoint = Integer.parseInt(st.nextToken());

            switch (checkPoint) {
                case 5 :
                    countPoint[0]--;
                    break;
                case 10 :
                    countPoint[1]--;
                    break;
                case 15 :
                    countPoint[2]--;
                    break;
                case 20 :
                    countPoint[3]--;
                    break;
                case 25 :
                    countPoint[4]--;
                    break;
            }

            point[i] = checkPoint;
            if(position == i) {
                if(checkPoint != 0 ) {
                    log.error("에러 발생 : 이미 선택된 위치 재 선택");
                }
            }
        }

        int returnedPoint = getRandomPoint(countPoint);
        log.info(Arrays.toString(point));
        point[position] = returnedPoint;
        StringBuilder sb = new StringBuilder();

        for(int a : point) {
            sb.append(a).append(" ");
        }

        attendanceEntity.setAttendanceData(sb.toString());
        attendanceEntity.setLastAttendance(new Timestamp(System.currentTimeMillis()));

        attendanceRepository.save(attendanceEntity);

        return returnedPoint;
    }

    private AttendanceEntity getAttendanceEntity(int userIdx) {
        Optional<AttendanceEntity> attendanceEntityOptional = attendanceRepository.findAttendanceEntityByUserIdx(userIdx);

        AttendanceEntity attendanceEntity = new AttendanceEntity();
        if (attendanceEntityOptional.isPresent()) {
            attendanceEntity = attendanceEntityOptional.get();
        } else {
            log.info("해당 회원의 출석체크 기록없음.");
            attendanceEntity.setUserIdx(userIdx);
            attendanceEntity.setAttendanceData("0 0 0 0 0 0 0 0 0 0");
            attendanceRepository.save(attendanceEntity);
            log.info("새로운 출석체크 기록 저장 완료.");
        }

        return attendanceEntity;
    }

    private int getRandomPoint(int[] countPoint) {
        Queue<Integer> queue = new LinkedList<>();

        for(int i = 1; i <= 5 ; i++) {
            for(int j = 0 ; j < countPoint[i-1]; j++) {
                queue.add(5 * i);
            }
        }

        for(int i = 0; i < new Random().nextInt(); i++) {
            queue.add(queue.poll());
        }

        return queue.peek();
    }

}
