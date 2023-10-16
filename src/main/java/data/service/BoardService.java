package data.service;

import data.dto.BoardDto;
import data.entity.BoardEntity;
import data.repository.BoardRepository;
import data.repository.OfferRepository;
import jwt.setting.settings.JwtService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static data.dto.BoardDto.toBoardDto;


@Service
@Slf4j
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final JwtService jwtService;
    private final OfferRepository offerRepository;
    public BoardService(BoardRepository boardRepository, JwtService jwtService, OfferRepository offerRepository) {
        this.boardRepository = boardRepository;
        this.jwtService = jwtService;
        this.offerRepository = offerRepository;
    }
    
    //게시판 리스트
    public Page<BoardDto> boardList(String category, int page){
        PageRequest pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.ASC,"idx"));
        return boardRepository.findBoardByCategory(category, pageable);
    }

    //게시판 작성
    public void boardWrite(HttpServletRequest request,BoardDto boardDto) {
        String nickname = (jwtService.extractNickname(jwtService.extractAccessToken(request).get()).get());
        BoardEntity board = BoardEntity.builder()
                .subject(boardDto.getSubject())
                .content(boardDto.getContent())
                .category(boardDto.getCategory())
                .tag(boardDto.getTag())
                .nickname(nickname)
                .build();
        boardRepository.save(board);
    }

    public void boardUpdate(Long idx, BoardDto boardDto){
        BoardEntity board = boardRepository.findByIdx(idx);
        board.setSubject(boardDto.getSubject());
        board.setContent(boardDto.getContent());
        board.setTag(board.getTag());
        board.setCategory(board.getCategory());
    }

    public void boardDelete(Long idx){
        boardRepository.deleteAllByIdx(idx);
    }

    public Page<BoardDto> searchByCategoryAndKeyword(String category, String keyword, int page){
        PageRequest pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.ASC,"idx"));
        Page<BoardEntity> boards = boardRepository.findByCategoryAndSubjectContaining(category, keyword, pageable);
        return boards.map(board -> toBoardDto(board));
    }
}
