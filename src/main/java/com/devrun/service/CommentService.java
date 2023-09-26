package com.devrun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.CommentDTO;
import com.devrun.dto.CommentDTO.Status;
import com.devrun.entity.Comment;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.Notice;
import com.devrun.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 댓글 작성
    public Comment insertComment(CommentDTO commentDTO, Notice notice, MemberEntity memberEntity) {
        Comment comment = new Comment();
        comment.setNotice(notice);
        comment.setMemberEntity(memberEntity);
        comment.setContent(commentDTO.getContent());
        if (commentDTO.getParentCommentNo() != 0) {
            Comment parentComment = commentRepository.findByCommentNo(commentDTO.getParentCommentNo());
            comment.setParentComment(parentComment);
        }
        return commentRepository.save(comment);
    }

    // 댓글 목록 가져오기 (Status가 ACTIVE인 것만)
    public List<Comment> getActiveCommentsByNotice(Notice notice) {
        return commentRepository.findByNoticeAndStatus(notice, Status.ACTIVE);
    }

    // 댓글 수정
    public Comment updateComment(int commentNo, String newContent) {
        Comment comment = commentRepository.findByCommentNo(commentNo);
        if (comment == null) {
        	// RuntimeException은 제어할 수 없거나, 더 자세한 정보를 제공하지 않아도 되는 경우에 사용
            throw new RuntimeException("Comment not found");
        }
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }
    
    // 댓글 삭제 (Status를 INACTIVE로 변경)
    public Comment deleteComment(int commentNo, String memberId) {
        Comment comment = commentRepository.findByCommentNo(commentNo);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }

        // 회원 검증
        if (!comment.getMemberEntity().getId().equals(memberId)) {
            throw new RuntimeException("Unauthorized member");
        }

        comment.setStatus(Status.INACTIVE);
        return commentRepository.save(comment);
    }

}