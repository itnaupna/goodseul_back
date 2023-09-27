package data.service;

import data.dto.OfferDto;
import data.entity.OfferEntity;
import data.entity.UserEntity;
import data.repository.OfferRepository;
import data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Slf4j
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    public OfferService(OfferRepository offerRepository, UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    public String setOffer(OfferDto dto) {
        Optional<UserEntity> user = userRepository.findByIdx(dto.getUserIdx());

        if(user.isPresent()) {
            log.info("User data 조회 성공");
        } else {
            log.error("User data 존재하지않음.");
            return "User data 존재하지않음.";
        }

        OfferEntity offerEntity = OfferEntity.offerDtoToEntity(dto,user.get());
        offerEntity.setWriteDate(new Timestamp(System.currentTimeMillis()));
        log.info(offerEntity.toString());

        offerRepository.save(offerEntity);
        return "견적 요청서 작성 완료";
    }
}
