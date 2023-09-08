package com.devrun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.Comment;
import com.devrun.entity.Notice;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByNotice(Notice notice);

	Comment findByParentComment(int parentCommentNo);

	Comment findByCommentNo(int commentNo);

}
