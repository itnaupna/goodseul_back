package data.dto;

import lombok.Data;

@Data
public class GoodseulInfoDto {
    private GoodseulDto goodseulDto;
    private UserDto userDto;
    private boolean favoriteStatus;

}
