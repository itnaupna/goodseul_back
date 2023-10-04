package data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name = "attendance")
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int AttendanceIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx", referencedColumnName = "idx")
    UserEntity user;

    private Timestamp lastAttendance;

    private String attendanceData;

}

