package com.devrun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.Notice;
import com.devrun.repository.MemberEntityRepository;
import com.devrun.repository.NoticeRepository;

@Service
public class NoticeService {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;

	public void insert(Notice notice) {
		noticeRepository.save(notice);
	}

	public Notice findByNoticeNo(int noticeNo) {
		return noticeRepository.findByNoticeNo(noticeNo);
	}

	public List<Notice> getNoticeList() {
        return noticeRepository.findAll();
    }

	public MemberEntity findById(String id) {
		return memberEntityRepository.findById(id);
	}

}
