package data.controller;

import data.dto.FavoriteDto;
import data.dto.FavoriteResponseDto;
import data.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/lv1/favorite")
public class FavoriteController {
    private final FavoriteService favoriteService;
    public FavoriteController (FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<Object> insertFavorite (@RequestBody FavoriteDto dto) {
        return new ResponseEntity<>(favoriteService.insertFavorite(dto), HttpStatus.OK);
    }

    @DeleteMapping("/{g_idx}")
    public ResponseEntity<Object> deleteFavorite (HttpServletRequest request, @PathVariable Long g_idx) {
        return new ResponseEntity<>(favoriteService.deleteFavorite(request, g_idx), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPageFavorite (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "fIdx") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            HttpServletRequest request) {
        return new ResponseEntity<>(favoriteService.getPageFavorite(page, size, sortProperty, sortDirection, request), HttpStatus.OK);
    }


}
