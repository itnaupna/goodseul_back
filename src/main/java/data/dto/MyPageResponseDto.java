package data.dto;

import lombok.Data;

@Data
public class MyPageResponseDto {
    private String email;
    private int favoriteCount;
    private int couponCount;
    private int chatRoomCount;
    private int reviewCount;
    private int myPoint;
}
