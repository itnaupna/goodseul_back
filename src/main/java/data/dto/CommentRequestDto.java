package data.dto;

import data.entity.BoardEntity;
import data.entity.CommentEntity;
import data.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDto {
    private Long idx;
    private String content;
    private String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    private String modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    private UserEntity user;
    private BoardEntity boards;

    //Dto -> Entity
    public CommentEntity toCommentEntity(){
        CommentEntity comments = CommentEntity.builder()
                .idx(idx)
                .content(content)
                .createDate(createDate)
                .modifiedDate(modifiedDate)
                .user(user)
                .boards(boards)
                .build();

        return comments;
    }
}
