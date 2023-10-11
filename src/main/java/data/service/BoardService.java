package data.service;

import data.dto.BoardDto;
import data.entity.BoardEntity;
import data.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    
    //게시판 리스트
    public Page<BoardDto> boardList(String category, int page){
        PageRequest pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.ASC,"idx"));
        return boardRepository.findBoardByCategory(category, pageable);
    }

    //게시판 작성
    public void boardWrite(BoardDto boardDto) throws Exception{
        BoardEntity board = BoardEntity.builder()
                .subject(boardDto.getSubject())
                .content(boardDto.getContent())
                .category(boardDto.getCategory())

                .build();
        boardRepository.save(board);
    }
}
