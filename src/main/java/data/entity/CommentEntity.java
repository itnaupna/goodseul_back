package data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "comment")
@Entity
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @CreatedDate
    private String createDate;

    @LastModifiedDate
    private String modifiedDate;

    @ManyToOne
    @JoinColumn(name ="board_idx")
    private BoardEntity boards;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private UserEntity user; // 작성자
}















