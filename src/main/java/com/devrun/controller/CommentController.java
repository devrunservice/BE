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
    public ResponseEntity<?> editComment(@PathVariable int commentNo, @RequestBody CommentDTO commentDTO) {
        String newContent = commentDTO.getContent();
        Comment updatedComment = commentService.updateComment(commentNo, newContent);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment.toDTO());
        }
        return ResponseEntity.badRequest().body("Comment not found");
    }

}