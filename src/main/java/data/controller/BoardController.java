package data.controller;

import data.dto.BoardDto;
import data.dto.CommentRequestDto;
import data.service.BoardService;
import data.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@Api(tags = "게시판 API")
@RequestMapping("/api/lv1/board")
public class BoardController {
    private final UserService userService;
    private final BoardService boardService;

    //게시판 작성
    @PostMapping
    @ApiOperation(value = "게시판 글 작성 API", notes = "게시판에 새로운 글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "작성 성공"),
            @ApiResponse(code = 401, message = "인증 오류 (올바른 토큰 정보가 없음)"),
            @ApiResponse(code = 400, message = "게시글의 제목과 내용은 필수입니다"),
            @ApiResponse(code = 404, message = "사용자 정보를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "게시글 작성 중 오류가 발생했습니다.")
    })
    public ResponseEntity<?> insertBoard(HttpServletRequest request, @RequestBody BoardDto boardDto) {
        try {
            boardService.boardWrite(request, boardDto);
            return ResponseEntity.ok(boardDto);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 작성 중 오류가 발생했습니다.");
        }
    }

//게시판 수정
    @PatchMapping("/{idx}")
    @ApiOperation(value = "게시판 글 수정 API", notes = "지정된 idx의 게시판 글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 게시글 정보"),
            @ApiResponse(code = 404, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "게시글 수정 중 오류가 발생했습니다.")
    })
    public ResponseEntity<?> boardUpdate(
            @ApiParam(value = "수정할 게시글의 idx", required = true) @PathVariable Long idx,
            @ApiParam(value = "수정될 게시글의 정보", required = true) @RequestBody BoardDto boardDto) {
        try {
            boardService.boardUpdate(idx, boardDto);
            return ResponseEntity.ok(boardDto);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 오류가 발생했습니다.");
        }
    }

    //게시판 삭제
    @DeleteMapping("/{idx}")
    @ApiOperation(value = "게시판 글 삭제 API", notes = "지정된 idx의 게시판 글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 404, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "게시글 삭제 중 오류가 발생했습니다.")
    })
    public ResponseEntity<String> boardDelete(
            @ApiParam(value = "삭제할 게시글의 idx", required = true) @PathVariable Long idx) {
        try {
            boardService.boardDelete(idx);
            return ResponseEntity.ok("삭제 완료");
        } catch (EntityNotFoundException e) {
            // 게시글이 존재하지 않을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }

    //댓글 삭제 
    @DeleteMapping("/comment/{idx}")
    @ApiOperation(value = "댓글 삭제 API", notes = "지정된 idx의 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 성공"),
            @ApiResponse(code = 404, message = "댓글을 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "댓글 삭제 중 오류가 발생했습니다.")
    })
    public ResponseEntity<String> commentDelete(
            @ApiParam(value = "삭제할 댓글의 idx", required = true) @PathVariable Long idx) {
        try{
            boardService.commentDelete(idx);
            return ResponseEntity.ok("삭제 완료");
        }catch (EntityNotFoundException e) {
            // 댓글이 존재하지 않을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류가 발생했습니다.");
        }

    }
    //게시판 리스트
    @GetMapping("/list")
    @ApiOperation(value = "게시판 리스트 조회 API", notes = "카테고리와 키워드에 따라 게시판 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "작성 성공"),
            @ApiResponse(code = 500, message = "게시글 조회 중 오류가 발생했습니다.")
    })
    public ResponseEntity<?> searchBoard(
            @ApiParam(value = "조회할 게시판의 카테고리", required = true) @RequestParam("category") String category,
            @ApiParam(value = "검색할 키워드", required = false) @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @ApiParam(value = "페이징 번호", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page) {
        try {
            Page<BoardDto> boards = boardService.searchByCategoryAndKeyword(category, keyword, page);
            List<BoardDto> boardlist = boards.getContent();
            return ResponseEntity.ok(boardlist);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 조회 중 오류가 발생했습니다.");
        }
    }


    //댓글 생성
    @PostMapping("/comment/{idx}")
    @ApiOperation(value = "게시판 댓글 생성 API", notes = "특정 게시판에 댓글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 생성 성공"),
            @ApiResponse(code = 401, message = "인증 오류 (올바른 토큰 정보가 없음)"),
            @ApiResponse(code = 400, message = "댓글 내용은 비어 있을 수 없습니다"),
            @ApiResponse(code = 404, message = "사용자 정보를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "댓글 작성 중 오류가 발생했습니다.")
    })
    public ResponseEntity<?> commentSave(
            @ApiParam(value = "댓글을 작성할 게시글의 idx", required = true) @PathVariable Long idx,
            @ApiParam(value = "작성할 댓글의 정보", required = true) @RequestBody CommentRequestDto dto,
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request) {
        try {
            return ResponseEntity.ok(boardService.CommentSave(request, idx, dto));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/detail/{idx}")
    @ApiOperation(value = "게시판 상세 조회 API", notes = "idx에 따른 게시판의 상세 내용을 조회합니다.")
    public BoardDto detailBoard(
            @ApiParam(value = "조회할 게시글의 idx", required = true) @PathVariable Long idx) {
        return boardService.boardDetail(idx);
    }
}













