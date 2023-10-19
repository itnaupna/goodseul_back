package data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ChatResponseDto {
    private int sender;

    private int receiver;

    private String message;

    private Timestamp sendTime;

    private boolean readCheck;

    private String roomId;
}
