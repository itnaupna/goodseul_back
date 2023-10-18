package data.service;

import data.dto.FavoriteDto;
import data.dto.FavoriteResponseDto;
import data.entity.FavoriteEntity;
import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.repository.FavoriteRepository;
import data.repository.GoodseulRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FavoriteService {

    public final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final GoodseulRepository goodseulRepository;
    private final JwtService jwtService;

    @Autowired
    public FavoriteService (FavoriteRepository favoriteRepository, UserRepository userRepository, GoodseulRepository goodseulRepository, JwtService jwtService) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.goodseulRepository= goodseulRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Object> insertFavorite (FavoriteDto dto) {
        UserEntity user = userRepository.findById(dto.getU_idx()).orElse(null);
        GoodseulEntity goodseul = goodseulRepository.findById(dto.getG_idx()).orElse(null);

        if(user == null || goodseul == null) {
            throw new RuntimeException("유저나 구슬님을 찾을 수 없어요");
        }

//        if(favoriteRepository.existsByUserEntity_idxAndGoodseulEntity_idx(dto.getU_idx(), dto.getG_idx())) {
//            return new ResponseEntity<>("이미 찜한 구슬님 입니다", HttpStatus.BAD_REQUEST);
//        }else {
//            return new ResponseEntity<>(dto, HttpStatus.OK);
//        }

        if(!favoriteRepository.existsByUserEntity_idxAndGoodseulEntity_idx(dto.getU_idx(), dto.getG_idx())) {
            FavoriteEntity savedFavorite = favoriteRepository.save(FavoriteEntity.toFavoriteEntity(dto, user, goodseul));
            Integer favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(dto.getG_idx());
            FavoriteResponseDto responseDto = new FavoriteResponseDto(savedFavorite, favoriteCount);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("이미 찜한 구슬님 입니다", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Object> deleteFavorite (HttpServletRequest request, @PathVariable Long g_idx) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();

        Optional<FavoriteEntity> favorite = favoriteRepository.findByUserEntity_idxAndGoodseulEntity_idx(idx, g_idx);

        if(favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            //        favoriteRepository.delete(favorite.get());
            Integer favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(g_idx);
            FavoriteResponseDto responseDto = new FavoriteResponseDto(favorite.get(), favoriteCount);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("찜하기 정보를 찾을 수 없습니다", HttpStatus.BAD_REQUEST);
        }

    }

    public Map<String, Object> getPageFavorite (int page, int size, String sortProperty, String sortDirection, HttpServletRequest request) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<FavoriteEntity> result = favoriteRepository.findByUserEntity_idx(idx, pageable);

        List<FavoriteResponseDto> favoriteDto = result.getContent().stream().map(favorite -> {
            Integer favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(favorite.getGoodseulEntity().getIdx());
            return new FavoriteResponseDto (favorite, favoriteCount);
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("favorites", favoriteDto);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }



}
