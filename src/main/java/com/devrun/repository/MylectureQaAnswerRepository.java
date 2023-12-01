package com.devrun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.dto.CommentDTO.Status;
import com.devrun.entity.MylectureQa;
import com.devrun.entity.MylectureQaAnswer;
import com.devrun.entity.Notice;

@Repository
public interface MylectureQaAnswerRepository extends JpaRepository<MylectureQaAnswer, Integer> {

	List<MylectureQaAnswer> findByQaNoAndStatus(MylectureQa qa, Status active);

	Long countByQaNoAndStatus(MylectureQa q, Status active);

}
