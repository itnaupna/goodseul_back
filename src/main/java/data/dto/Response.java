package data.dto;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private String message;
    private String imageLocation;
    private List<String> imageLocations;
    private boolean success;
}
