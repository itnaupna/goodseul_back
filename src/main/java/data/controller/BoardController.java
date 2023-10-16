package data.controller;

import data.dto.BoardDto;
import data.service.BoardService;
import data.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final UserService userService;
    private final BoardService boardService;

    //게시판 작성
    @PostMapping("/lv0/board")
    public ResponseEntity<?> insertBoard(HttpServletRequest request, @RequestBody  BoardDto boardDto){
        boardService.boardWrite(request,boardDto);
        return ResponseEntity.ok(boardDto);
    }

    //게시판 리스트
    @GetMapping("/lv0/boardlist")
    public ResponseEntity<?> boardList(@RequestParam(value = "page", defaultValue = "0")Integer page, @RequestParam String category){
        Page<BoardDto> boardPage = boardService.boardList(category, page);
        List<BoardDto> boardList = boardPage.getContent();

        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }

    @PatchMapping("/lv0/board/{idx}")
    public ResponseEntity<?> boardUpdate(@PathVariable Long idx, @RequestBody BoardDto boardDto) {
        boardService.boardUpdate(idx, boardDto);
        return ResponseEntity.ok(boardDto);
    }
    @DeleteMapping("/lv0/board/{idx}")
    public ResponseEntity<?> boardDelete(@PathVariable Long idx) {
        boardService.boardDelete(idx);
        return ResponseEntity.ok("삭제 완료");
    }

    @GetMapping("/lv0/boardsearch")
    public ResponseEntity<?> searchBoard(@RequestParam("category") String category, @RequestParam(value = "keyword",defaultValue = "") String keyword,
                                         @RequestParam(value = "page", defaultValue = "0")Integer page){
        Page<BoardDto> boards = boardService.searchByCategoryAndKeyword(category, keyword, page);
        List<BoardDto> boardlist = boards.getContent();
        return ResponseEntity.ok(boardlist);

    }
}













