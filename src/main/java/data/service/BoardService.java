package data.service;

import data.dto.BoardDto;
import data.dto.BoardListDto;
import data.dto.CommentRequestDto;
import data.dto.CommentResponseDto;
import data.entity.BoardEntity;
import data.entity.CommentEntity;
import data.entity.UserEntity;
import data.exception.*;
import data.repository.BoardRepository;
import data.repository.CommentRepository;
import data.repository.OfferRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static data.dto.BoardDto.toBoardDto;


@Service
@Slf4j
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final JwtService jwtService;
    private final OfferRepository offerRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    public BoardService(BoardRepository boardRepository, JwtService jwtService, OfferRepository offerRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.jwtService = jwtService;
        this.offerRepository = offerRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    //게시판 작성
    public void boardWrite(HttpServletRequest request,BoardDto boardDto) {
        if (Objects.equals(boardDto.getSubject(), "") || Objects.equals(boardDto.getContent(), "")) {
            throw new SubjectContentNotFoundException();
        }
        String nickname = jwtService.extractNickname(jwtService.extractAccessToken(request).orElseThrow(TokenException::new))
                .orElseThrow(DuplicateNicknameException::new);

        Long userIdx = jwtService.extractIdxFromRequest(request);
        UserEntity user = userRepository.findByIdx(userIdx)
                .orElseThrow(UserNotFoundException::new);

        BoardEntity board = BoardEntity.builder()
                .subject(boardDto.getSubject())
                .content(boardDto.getContent())
                .category(boardDto.getCategory())
                .user(user)
                .tag(boardDto.getTag())
                .nickname(nickname)
                .build();
        boardRepository.save(board);
    }

    //게시판 수정
    public void boardUpdate(Long idx, BoardDto boardDto){
        BoardEntity board = boardRepository.findByIdx(idx)
                .orElseThrow(BoardNotFoundException::new);
        if (Objects.equals(boardDto.getSubject(), "") || Objects.equals(boardDto.getContent(), "")) {

            throw new SubjectContentNotFoundException();
        }
        board.setSubject(boardDto.getSubject());
        board.setContent(boardDto.getContent());
        board.setTag(board.getTag());
        board.setCategory(board.getCategory());
    }

//    //댓글 수정
//    public void commentUpdate(Long idx, CommentRequestDto dto){
//        CommentEntity comment = commentRepository.findByIdx(idx)
//                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
//        if(dto.getContent()== null){
//            throw new IllegalArgumentException("댓글의 내용은 필수입니다");
//        }
//        comment.setContent(dto.getContent());
//    }

    //게시판 삭제
    public void boardDelete(Long idx){
        BoardEntity board = boardRepository.findByIdx(idx)
                        .orElseThrow(BoardNotFoundException::new);
        boardRepository.deleteAllByIdx(idx);
    }

    //댓글 삭제
    public void commentDelete(Long idx){
        CommentEntity comment = commentRepository.findByIdx(idx)
                .orElseThrow(CommentNotFoundException::new);
        commentRepository.deleteByIdx(idx);
    }

    //게시판 상세보기
    public BoardDto boardDetail(Long idx){
        BoardEntity board = boardRepository.findByIdx(idx)
                .orElseThrow(BoardNotFoundException::new);
        BoardDto dto = BoardDto.toBoardDto(board);

        List<CommentEntity> commentEntity = board.getComments();
        List<CommentResponseDto> commentDtos = commentEntity.stream()
                .map(comment ->{
                    CommentResponseDto commentDto = new CommentResponseDto(comment);
                    commentDto.setContent(comment.getContent());
                    commentDto.setNickname(comment.getUser().getNickname());
                    commentDto.setCreateDate(comment.getCreateDate());
                    return commentDto;
                })
                .collect(Collectors.toList());
        dto.setComments(commentDtos);
        return dto;
    }

    //게시판 검색 + 리스트 + 페이징
    public List<BoardListDto> searchByCategoryAndKeyword(String category, String keyword, int page) {
        PageRequest pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.ASC, "idx"));
        List<BoardEntity> boards = boardRepository.findByCategoryAndSubjectContaining(category, keyword, pageable).getContent();
        List<BoardListDto> resultList = new ArrayList<>();

        for(BoardEntity boardEntity : boards){
            BoardDto boardDto = BoardDto.toBoardDto(boardEntity);
            BoardListDto boardListDto = new BoardListDto();
            boardListDto.setBoardDto(boardDto);
            boardListDto.setUserProfile(userRepository.findByIdx(boardDto.getUserId()).orElseThrow(GoodseulNotFoundException::new).getUserProfile());
            resultList.add(boardListDto);
        }
        return resultList;
    }

    //댓글 생성
    @Transactional
    public Long CommentSave(HttpServletRequest request, Long idx, CommentRequestDto dto) {
        try {
            String nickname = jwtService.extractNickname(jwtService.extractAccessToken(request).orElseThrow(TokenException::new))
                    .orElseThrow(DuplicateNicknameException::new);

            UserEntity user = userRepository.findByNickname(nickname)
                    .orElseThrow(UserNotFoundException::new);

            BoardEntity board = boardRepository.findByIdx(idx)
                    .orElseThrow(BoardNotFoundException::new);
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                throw new CommnetContentNotFoundException();
            }
            dto.setUser(user);
            dto.setBoards(board);

            CommentEntity comment = dto.toCommentEntity();
            commentRepository.save(comment);

            return dto.getIdx();

        } catch (Exception e) {
            throw new RuntimeException("댓글 저장 중 오류가 발생했습니다.", e);
        }
    }
}
