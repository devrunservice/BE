package com.devrun.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.devrun.dto.CommentDTO;
import com.devrun.entity.Comment;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.Notice;
import com.devrun.service.CommentService;
import com.devrun.service.MemberService;
import com.devrun.service.NoticeService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class CommentController {

	@Autowired
    private CommentService commentService;

    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private MemberService memberService;

    // 댓글 작성
//     {
//    "noticeNo": 1,
//    "content": "내용"
//     }
    @PostMapping("/comment/write")
    @ApiOperation(value = "댓글 작성", notes = "공지사항에 댓글을 작성합니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "noticeNo", value = "공지사항 번호", required = true, paramType = "body", dataType = "Integer"),
        @ApiImplicitParam(name = "content", value = "댓글 내용", required = true, paramType = "body", dataType = "String"),
        @ApiImplicitParam(name = "id", value = "작성자 아이디", required = true, paramType = "body", dataType = "String")
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "댓글이 성공적으로 작성되었습니다."),
        @ApiResponse(code = 400, message = "잘못된 공지사항 번호입니다.")
    })
    public ResponseEntity<?> writeComment(@RequestBody CommentDTO commentDTO) {
        Notice notice = noticeService.findByNoticeNo(commentDTO.getNoticeNo());
        MemberEntity memberEntity = memberService.findById(commentDTO.getId());
        System.out.println("memberEntity : " + commentDTO.getId());
        if (notice != null) {
            Comment comment = commentService.insertComment(commentDTO, notice, memberEntity);
            return ResponseEntity.ok(comment.toDTO());
        }
        return ResponseEntity.badRequest().body("Notice not found");
    }

    // 댓글 목록 출력
    @GetMapping("/comments/{noticeNo}")
    @ApiOperation(value = "댓글 목록 출력", notes = "공지사항 번호에 따른 댓글 목록을 반환합니다.")
    @ApiImplicitParam(name = "noticeNo", value = "공지사항 번호", required = true, paramType = "path", dataType = "Integer")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "댓글 목록을 성공적으로 반환했습니다."),
        @ApiResponse(code = 400, message = "잘못된 공지사항 번호입니다.")
    })
    public ResponseEntity<?> listComments(@PathVariable int noticeNo) {
        Notice notice = noticeService.findByNoticeNo(noticeNo);
        if (notice != null) {
            List<Comment> comments = commentService.getCommentsByNotice(notice);
            List<CommentDTO> commentDTOs = comments.stream().map(Comment::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(commentDTOs);
        }
        return ResponseEntity.badRequest().body("Notice not found");
    }

    // 댓글 수정
    @PutMapping("/comment/edit/{commentNo}")
    @ApiOperation(value = "댓글 수정", notes = "특정 댓글을 수정합니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentNo", value = "수정할 댓글 번호", required = true, paramType = "path", dataType = "Integer"),
        @ApiImplicitParam(name = "content", value = "수정할 댓글 내용", required = true, paramType = "body", dataType = "String")
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "댓글이 성공적으로 수정되었습니다."),
        @ApiResponse(code = 400, message = "잘못된 댓글 번호입니다.")
    })
    public ResponseEntity<?> editComment(@PathVariable int commentNo, @RequestBody CommentDTO commentDTO) {
        String newContent = commentDTO.getContent();
        Comment updatedComment = commentService.updateComment(commentNo, newContent);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment.toDTO());
        }
        return ResponseEntity.badRequest().body("Comment not found");
    }

}