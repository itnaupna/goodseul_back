package data.service;

import data.dto.FavoriteResponseDto;
import data.dto.GoodseulResponseDto;
import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.exception.UserNotFoundException;
import data.repository.FavoriteRepository;
import data.repository.GoodseulRepository;
import data.repository.UserRepository;
import jwt.setting.config.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OnlineUserService {

    private final GoodseulRepository goodseulRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    private final Map<Long, UserEntity> onlineGoodsuleUsers = new ConcurrentHashMap<>();

    public OnlineUserService(UserRepository userRepository, GoodseulRepository goodseulRepository, UserRepository userRepository1, FavoriteRepository favoriteRepository) {
        this.goodseulRepository = goodseulRepository;
        this.userRepository = userRepository1;
        this.favoriteRepository = favoriteRepository;
    }

    public void addAllUser(UserEntity userEntity) {
        if (userEntity != null && userEntity.getRole() == Role.GOODSEUL) {
            onlineGoodsuleUsers.put(userEntity.getIdx(), userEntity);
        }
    }

    public void removeUser(Long idx) {
        UserEntity userEntity = userRepository.findById(idx).orElseThrow(UserNotFoundException::new);
        logOnlineGoodsuleUsers();
        if (userEntity.getRole() == Role.GOODSEUL) {
            onlineGoodsuleUsers.remove(idx);
            logOnlineGoodsuleFavorite();
            logOnlineGoodsuleUsers();
        }
    }

    public List<GoodseulResponseDto> getOnlineUsers() {
        List<GoodseulResponseDto> list = new ArrayList<>();

        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            Long userId = entry.getKey();
            UserEntity user = entry.getValue();
            Optional<GoodseulEntity> goodseul = goodseulRepository.findByIdx(user.getIsGoodseul().getIdx());
            log.info("goodseul: {}",goodseul);
            if(goodseul.isPresent()) {
                GoodseulEntity goodseulEntity = goodseul.get();
                int favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(goodseulEntity.getIdx());
                list.add(new GoodseulResponseDto(goodseulEntity.getIdx(), goodseulEntity.getGoodseulName(), user.getUserProfile(), goodseulEntity.getIsPremium(),
                        favoriteCount, goodseulEntity.getGoodseulInfo(), user.getLocation(), goodseulEntity.getSkill()));
            } else {
                throw new EntityNotFoundException();
            }
        }
        Collections.shuffle(list);
        return list.size() > 6 ? list.subList(0, 6) : list;
    }
    public List<GoodseulResponseDto> getOnlinePremiumUsers() {
        List<GoodseulResponseDto> list = new ArrayList<>();

        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            UserEntity user = entry.getValue();
            if (user.getIsGoodseul().getIsPremium() > 0) {
                Optional<GoodseulEntity> goodseul = goodseulRepository.findByIdx(user.getIsGoodseul().getIdx());
                if (goodseul.isPresent()) {
                    GoodseulEntity goodseulEntity = goodseul.get();
                    int favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(goodseulEntity.getIdx());
                    list.add(new GoodseulResponseDto(goodseulEntity.getIdx(), goodseulEntity.getGoodseulName(), user.getUserProfile(), goodseulEntity.getIsPremium(),
                            favoriteCount, goodseulEntity.getGoodseulInfo(), user.getLocation(), goodseulEntity.getSkill()));
                } else {
                    throw new EntityNotFoundException();
                }
            }
        }
        Collections.shuffle(list);
        return list.size() > 6 ? list.subList(0, 6) : list;
    }
    public List<GoodseulResponseDto> getOnlineFavoriteUsers() {
        List<GoodseulResponseDto> resultList = new ArrayList<>();

        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            UserEntity user = entry.getValue();
            Optional<GoodseulEntity> goodseul = goodseulRepository.findByIdx(user.getIsGoodseul().getIdx());
            log.info("goodseul: {}",goodseul);

            if(goodseul.isPresent()) {
                GoodseulEntity goodseulEntity = goodseul.get();
                Integer favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(goodseulEntity.getIdx());
                resultList.add(new GoodseulResponseDto(goodseulEntity.getIdx(), goodseulEntity.getGoodseulName(), user.getUserProfile(), goodseulEntity.getIsPremium(),
                        favoriteCount, goodseulEntity.getGoodseulInfo(), user.getLocation(), goodseulEntity.getSkill()));
            } else {
                throw new EntityNotFoundException();
            }
        }

        resultList.sort(Comparator.comparing(GoodseulResponseDto::getFavoriteCount).reversed());

        return resultList.size() > 6 ? resultList.subList(0, 6) : resultList;
    }
    public void logOnlineGoodsuleUsers() {
        log.info("현재 접속 중인 구슬 유저:");
        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            Long userId = entry.getKey();
            UserEntity user = entry.getValue();
            log.info("User ID: {}, Profile: {}, Nickname: {}, IsGoodseul: {}", userId, user.getUserProfile(), user.getNickname(), user.getIsGoodseul().getIdx());
        }
    }
    public void logOnlineGoodsulePremium() {
        log.info("현재 접속 중인 프리미엄 구슬 유저:");
        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            UserEntity user = entry.getValue();
            if (user.getIsGoodseul().getIsPremium() > 0) {
                Long userId = entry.getKey();
                log.info("User ID: {}, Profile: {}, Nickname: {}, IsGoodseul: {}", userId, user.getUserProfile(), user.getNickname(), user.getIsGoodseul().getIdx());
            }
        }
    }

    public void logOnlineGoodsuleFavorite() {
        log.info("현재 접속 중인 구슬 유저 좋아요 순:");
        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            Long userId = entry.getKey();
            UserEntity user = entry.getValue();

            Long goodseulId = user.getIsGoodseul().getIdx();
            Integer favoriteCount = favoriteRepository.countFavoriteEntitiesByGoodseulEntity_idx(goodseulId);

            log.info("User ID: {}, Profile: {}, Nickname: {}, IsGoodseul: {}, FavoriteCount: {}",
                    userId, user.getUserProfile(), user.getNickname(), goodseulId, favoriteCount);
        }
    }

}
