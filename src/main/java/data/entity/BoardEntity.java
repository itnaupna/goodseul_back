package data.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name="board")
@AllArgsConstructor
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String subject;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String content;

    private String tag;

    private String nickname;

    private String category;

    private LocalDate writeDate;
    @PrePersist
    public void prePersist(){
        if (writeDate == null) {
            writeDate = LocalDate.now();
        }
    }
}
