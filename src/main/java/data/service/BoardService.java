package data.service;

import data.dto.BoardDto;
import data.dto.CommentRequestDto;
import data.dto.CommentResponseDto;
import data.entity.BoardEntity;
import data.entity.CommentEntity;
import data.entity.UserEntity;
import data.repository.BoardRepository;
import data.repository.CommentRepository;
import data.repository.OfferRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
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
    
    //게시판 리스트
    public Page<BoardDto> boardList(String category, int page){
        PageRequest pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.ASC,"idx"));
        return boardRepository.findBoardByCategory(category, pageable);
    }

    //게시판 작성
    public void boardWrite(HttpServletRequest request,BoardDto boardDto) {
        String nickname = (jwtService.extractNickname(jwtService.extractAccessToken(request).get()).get());
        Long userIdx = jwtService.extractIdx(jwtService.extractAccessToken(request).orElseThrow(() -> new RuntimeException("Access Token이 존재하지 않습니다.")))
                .orElseThrow(() -> new RuntimeException("사용자 인덱스를 찾을 수 없습니다."));
        UserEntity user = userRepository.findByIdx(userIdx)
                .orElseThrow(() -> new RuntimeException("사용자를 데이터베이스에서 찾을 수 없습니다."));
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
        Optional<BoardEntity> boardOptional = boardRepository.findByIdx(idx);
        if (!boardOptional.isPresent()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
        BoardEntity board = boardOptional.get();
        board.setSubject(boardDto.getSubject());
        board.setContent(boardDto.getContent());
        board.setTag(board.getTag());
        board.setCategory(board.getCategory());
    }

    //게시판 삭제
    public void boardDelete(Long idx){
        boardRepository.deleteAllByIdx(idx);
    }

    //게시판 상세보기
    public BoardDto boardDetail(Long idx){
        Optional<BoardEntity> boardOptional = boardRepository.findByIdx(idx);
        if (!boardOptional.isPresent()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
        BoardEntity board = boardOptional.get();
        BoardDto dto = new BoardDto();
        dto.setIdx(board.getIdx());
        dto.setSubject(board.getSubject());
        dto.setContent(board.getContent());
        dto.setNickname(board.getNickname());
        dto.setWriteDate(board.getWriteDate());

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

    public Page<BoardDto> searchByCategoryAndKeyword(String category, String keyword, int page){
        PageRequest pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.ASC,"idx"));
        Page<BoardEntity> boards = boardRepository.findByCategoryAndSubjectContaining(category, keyword, pageable);
        return boards.map(board -> toBoardDto(board));
    }

    //댓글 생성
    @Transactional
    public Long CommentSave(HttpServletRequest request, Long idx, CommentRequestDto dto){
        String nickname = (jwtService.extractNickname(jwtService.extractAccessToken(request).get()).get());
        Optional<UserEntity> userOptional = userRepository.findByNickname(nickname);

        if(!userOptional.isPresent()){
            throw new RuntimeException("사용자를 찾을 수 없습니다");
        }
        UserEntity user = userOptional.get();
        Optional<BoardEntity> boardOptional = boardRepository.findByIdx(idx);
        if (!boardOptional.isPresent()){
            throw new RuntimeException("게시물을 찾을 수 없습니다");
        }
        BoardEntity board = boardOptional.get();
        dto.setUser(user);
        dto.setBoards(board);

        CommentEntity comment = dto.toCommentEntity();
        commentRepository.save(comment);

        return dto.getIdx();
    }
}
