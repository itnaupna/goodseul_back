package data.dto;

import data.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class BoardDto {
    private Long idx;
    private String subject;
    private String content;
    private String nickname;
    private String category;
    private String tag;
    private Long userId;
    private LocalDate writeDate;
    private List<CommentResponseDto> comments;
    public BoardDto(BoardEntity boards) {
        this.idx =boards.getIdx();
        this.subject = boards.getSubject();
        this.content = boards.getContent();
        this.nickname = boards.getNickname();
        this.category = boards.getCategory();
        this.tag = boards.getTag();
        this.userId = boards.getUser().getIdx();
        this.writeDate = boards.getWriteDate();
        this.comments = boards.getComments().stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }

    public static BoardDto toBoardDto(BoardEntity board){
        return BoardDto.builder()
                .idx(board.getIdx())
                .subject(board.getSubject())
                .content(board.getContent())
                .nickname(board.getNickname())
                .category(board.getCategory())
                .tag(board.getTag())
                .writeDate(board.getWriteDate())
                .build();
    }
}
