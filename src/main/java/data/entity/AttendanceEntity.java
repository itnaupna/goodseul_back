package data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name = "attendance")
@NoArgsConstructor
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int AttendanceIdx;

    private int userIdx;

    private Timestamp lastAttendance;

    private String attendanceData;

}

