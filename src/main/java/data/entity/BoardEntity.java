package data.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String category;

    private LocalDate writeDate;

    @PrePersist
    public void prePersist(){
        if (writeDate == null) {
            writeDate = LocalDate.now();
        }
    }
    @OneToMany(mappedBy = "boards", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("idx asc")//댓글 정렬
    private List<CommentEntity> comments;

    public void update(String subject, String content){
        this.subject = subject;
        this.content = content;
    }
}
