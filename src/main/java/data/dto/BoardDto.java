package data.dto;

import data.entity.BoardEntity;
import data.entity.UserEntity;
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


    public static BoardDto toBoardDto(BoardEntity board){
        return BoardDto.builder()
                .idx(board.getIdx())
                .subject(board.getSubject())
                .content(board.getContent())
                .nickname(board.getNickname())
                .category(board.getCategory())
                .tag(board.getTag())
                .writeDate(board.getWriteDate())
                .userId(board.getUser().getIdx())
                .build();
    }

    public static BoardDto toBoardDto(BoardEntity board, UserEntity user, String nickname){
        return BoardDto.builder()
                .idx(board.getIdx())
                .subject(board.getSubject())
                .content(board.getContent())
                .nickname(nickname)
                .category(board.getCategory())
                .tag(board.getTag())
                .writeDate(board.getWriteDate())
                .userId(board.getUser().getIdx())
                .build();
    }
}
