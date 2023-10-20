package data.controller;

import data.dto.FavoriteDto;
import data.service.FavoriteService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/lv1/favorite")
@Api(value = "찜 기능", description = "Favorite Controller", tags = "찜 목록 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @ApiOperation(value = "찜목록 추가", notes = "내가 찜한 구슬 추가")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "f_idx", value = "favorite idx", dataType = "long", required = false, paramType = "query"),
            @ApiImplicitParam(name = "g_idx", value = "구슬 idx", dataType = "long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "u_idx", value = "유저 idx", dataType = "long", required = false, paramType = "query")
    })
    @PostMapping
    public ResponseEntity<Object> insertFavorite(@RequestBody FavoriteDto dto) {
        return new ResponseEntity<>(favoriteService.insertFavorite(dto), HttpStatus.OK);
    }

    @ApiOperation(value = "찜목록 삭제", notes = "찜한 구슬 삭제")
    @ApiImplicitParam(name = "g_idx", value = "구슬 아이디", dataType = "long", required = true, paramType = "path")
    @DeleteMapping("/{g_idx}")
    public ResponseEntity<Object> deleteFavorite(HttpServletRequest request, @PathVariable Long g_idx) {
        return new ResponseEntity<>(favoriteService.deleteFavorite(request, g_idx), HttpStatus.OK);
    }

    @ApiOperation(value = "내 찜목록 리스트", notes = "내가 찜한 구슬 리스트")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPageFavorite(
            @ApiParam(value = "페이지 번호", defaultValue = "0", required = true)
            @RequestParam(defaultValue = "0") int page,

            @ApiParam(value = "페이지당 보여지는 갯수", defaultValue = "6", required = true)
            @RequestParam(defaultValue = "6") int size,

            @ApiParam(value = "정렬 기준", defaultValue = "fIdx")
            @RequestParam(defaultValue = "fIdx") String sortProperty,

            @ApiParam(value = "정렬 순서", defaultValue = "DESC", allowableValues = "ASC, DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection,

            HttpServletRequest request) {
        return new ResponseEntity<>(favoriteService.getPageFavorite(page, size, sortProperty, sortDirection, request), HttpStatus.OK);
    }
}