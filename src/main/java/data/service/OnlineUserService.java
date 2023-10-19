package data.service;

import data.dto.GoodseulResponseDto;
import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.repository.GoodseulRepository;
import data.repository.UserRepository;
import jwt.setting.config.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OnlineUserService {

    private final GoodseulRepository goodseulRepository;
    private final UserRepository userRepository;

    private Map<Long, UserEntity> onlineGoodsuleUsers = new ConcurrentHashMap<>();
    public OnlineUserService(UserRepository userRepository, GoodseulRepository goodseulRepository, UserRepository userRepository1) {
        this.goodseulRepository = goodseulRepository;
        this.userRepository = userRepository1;
    }

    public void addUser(UserEntity userEntity) {
        if (userEntity != null && userEntity.getRole() == Role.GOODSEUL) {
            onlineGoodsuleUsers.put(userEntity.getIdx(), userEntity);
        }
    }

    public void removeUser(Long idx) {
        UserEntity userEntity = userRepository.findById(idx).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));
        logOnlineGoodsuleUsers();
        if (userEntity.getRole() == Role.GOODSEUL) {
            onlineGoodsuleUsers.remove(idx);
            logOnlineGoodsuleUsers();
        }
    }
    public List<GoodseulResponseDto> getOnlineUsers() {
        List<GoodseulResponseDto> list = new ArrayList<>();

        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            Long userId = entry.getKey();
            UserEntity user = entry.getValue();
            Optional<GoodseulEntity> goodseul = goodseulRepository.findByIdx(user.getIsGoodseul().getIdx());
            if(goodseul.isPresent()) {
                GoodseulEntity goodseulEntity = goodseul.get();
                list.add(new GoodseulResponseDto(goodseulEntity.getIdx(), goodseulEntity.getGoodseulName(), user.getUserProfile()));
            } else {
                throw new EntityNotFoundException();
            }
        }
        return new ArrayList<>(list);
    }
    public void logOnlineGoodsuleUsers() {
        log.info("현재 접속 중인 구슬 유저:");
        for (Map.Entry<Long, UserEntity> entry : onlineGoodsuleUsers.entrySet()) {
            Long userId = entry.getKey();
            UserEntity user = entry.getValue();
            log.info("User ID: {}, Profile: {}, Nickname: {}, IsGoodseul: {}", userId, user.getUserProfile(), user.getNickname(), user.getIsGoodseul().getIdx());
        }
    }

}
