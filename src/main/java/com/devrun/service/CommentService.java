package com.devrun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.CommentDTO;
import com.devrun.entity.Comment;
import com.devrun.entity.Notice;
import com.devrun.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 댓글 작성
    public Comment insertComment(CommentDTO commentDTO, Notice notice) {
        Comment comment = new Comment();
        comment.setNotice(notice);
        comment.setContent(commentDTO.getContent());
        if (commentDTO.getParentCommentNo() != 0) {
            Comment parentComment = commentRepository.findByCommentNo(commentDTO.getParentCommentNo());
            comment.setParentComment(parentComment);
        }
        return commentRepository.save(comment);
    }

    // 댓글 목록 가져오기
    public List<Comment> getCommentsByNotice(Notice notice) {
        return commentRepository.findByNotice(notice);
    }

    // 댓글 수정
    public Comment updateComment(int commentNo, String newContent) {
        Comment comment = commentRepository.findByCommentNo(commentNo);
        if (comment != null) {
            comment.setContent(newContent);
            return commentRepository.save(comment);
        }
        return null;
    }

}