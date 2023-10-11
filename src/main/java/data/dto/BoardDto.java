package data.dto;

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
}
