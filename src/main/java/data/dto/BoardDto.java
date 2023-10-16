package data.dto;

import data.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate writeDate;
    public BoardDto(String subject, String tag, LocalDate writeDate) {
        this.subject = subject;
        this.tag = tag;
        this.writeDate = writeDate;
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
