package data.controller;

import data.dto.FavoriteDto;
import data.dto.FavoriteResponseDto;
import data.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FavoriteController {
    private final FavoriteService favoriteService;
    public FavoriteController (FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/lv1/favorite")
    public ResponseEntity<Object> insertFavorite (@RequestBody FavoriteDto dto) {
        return new ResponseEntity<>(favoriteService.insertFavorite(dto), HttpStatus.OK);
    }

    @DeleteMapping("/lv1/favorite/{u_idx}/{g_idx}")
    public ResponseEntity<Object> deleteFavorite (@PathVariable Long u_idx, @PathVariable Long g_idx) {
        return new ResponseEntity<>(favoriteService.deleteFavorite(u_idx, g_idx), HttpStatus.OK);
    }

    @GetMapping("/lv1/favorite/{u_idx}")
    public ResponseEntity<Map<String, Object>> getPageFavorite (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "fIdx") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @PathVariable Long u_idx) {
        return new ResponseEntity<>(favoriteService.getPageFavorite(page, size, sortProperty, sortDirection, u_idx), HttpStatus.OK);
    }




}
