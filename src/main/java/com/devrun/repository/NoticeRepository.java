package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	Notice findByNoticeNo(int noticeNo);

}
