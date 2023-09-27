package com.devrun.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.dto.NoticeDTO.Status;
import com.devrun.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	Notice findByNoticeNo(int noticeNo);
	
	Page<Notice> findByStatus(Status status, Pageable pageable);

	long countByStatus(Status active);
}
