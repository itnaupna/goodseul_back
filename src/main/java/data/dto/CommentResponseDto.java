package data.dto;

import data.entity.CommentEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CommentResponseDto {
    private Long idx;
    private String content;
    private String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    private String modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    private String nickname;
    private Long boardId;

    public CommentResponseDto(CommentEntity comment){
        this.idx = comment.getIdx();
        this.content = comment.getContent();
        this.createDate = comment.getCreateDate();
        this.modifiedDate = comment.getModifiedDate();
        this.nickname = comment.getUser().getNickname();
        this.boardId = comment.getBoards().getIdx();
    }
}
