package data.service;

import data.dto.OfferDto;
import data.entity.OfferEntity;
import data.entity.UserEntity;
import data.exception.UserNotFoundException;
import data.repository.OfferRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

@Service
@Slf4j
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OfferService(OfferRepository offerRepository, UserRepository userRepository, JwtService jwtService) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String setOffer(HttpServletRequest request,OfferDto dto) {
        long idx = jwtService.extractIdxFromRequest(request);

        UserEntity user = userRepository.findByIdx(idx).orElseThrow(UserNotFoundException::new);

        OfferEntity offerEntity = OfferEntity.offerDtoToEntity(dto,user);
        offerEntity.setWriteDate(new Timestamp(System.currentTimeMillis()));
        log.info(offerEntity.toString());

        offerRepository.save(offerEntity);
        return "견적 요청서 작성 완료";
    }

    public List<OfferDto> getMyOfferList(HttpServletRequest request,int page) {
        long idx = jwtService.extractIdxFromRequest(request);
        log.info("idx : " + idx);

        List<OfferDto> dtoList = new LinkedList<>();

//        if(userRepository.findByIdx(idx).get().getIsGoodseul() != 0) {
//            log.error("잘못된 요청입니다.");
//        } else {
            PageRequest pageRequest = PageRequest.of(page,5, Sort.by("writeDate").descending());
            Page<OfferEntity> list = offerRepository.findAllByUserIdx(idx,pageRequest);

            for(OfferEntity entity : list) {
                dtoList.add(OfferDto.offerEntityToDto(entity));
            }
//        }
        return dtoList;
    }

    public List<OfferDto> getWeekOfferList(HttpServletRequest request,int page) {
        long idx = jwtService.extractIdxFromRequest(request);
        log.info("idx : " + idx);

        List<OfferDto> dtoList = new LinkedList<>();

        if(userRepository.findByIdx(idx).isPresent()) {
            if(userRepository.findByIdx(idx).get().getIsGoodseul().getIdx() == 0) {
                log.error("잘못된 요청입니다.");
                throw new IllegalArgumentException("잘못된 요청입니다.");
            }
        } else {
            PageRequest pageRequest = PageRequest.of(page,10, Sort.by("write_date").descending());
            Page<OfferEntity> list = offerRepository.findPostsWrittenInTheLastWeek(pageRequest);

            for(OfferEntity entity : list) {
                dtoList.add(OfferDto.offerEntityToDto(entity));
            }
        }

        return dtoList;

    }

    public void deleteOffer(HttpServletRequest request, int offerIdx) {
        long userIdx = jwtService.extractIdxFromRequest(request);
        if(offerRepository.findByOfferIdx(offerIdx).isPresent()) {
            if(offerRepository.findByOfferIdx(offerIdx).get().getUser().getIdx() != userIdx) {
                log.error("요청을 시도한 사용자와 실제 견적 요청자와 불일치.");
            } else {
                offerRepository.deleteById(offerIdx);
            }
        }
    }

    public void updateWriteDate(HttpServletRequest request, int offerIdx) {
        long userIdx = jwtService.extractIdxFromRequest(request);
        if(offerRepository.findByOfferIdx(offerIdx).isPresent()) {
            if(offerRepository.findByOfferIdx(offerIdx).get().getUser().getIdx() != userIdx) {
                log.error("요청을 시도한 사용자와 실제 견적 요청자와 불일치.");
            } else {
                OfferEntity offerEntity = offerRepository.findByOfferIdx(offerIdx).get();
                offerEntity.setWriteDate(new Timestamp(System.currentTimeMillis()));
                offerRepository.save(offerEntity);
            }
        }
    }
}
