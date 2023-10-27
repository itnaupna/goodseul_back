package data.dto;

import lombok.Data;

@Data
public class GoodseulListDto {
    private GoodseulDto goodseulDto;
    private String userProfile;
    private double avgStar;
}
