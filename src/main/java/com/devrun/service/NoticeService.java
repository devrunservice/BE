package com.devrun.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devrun.dto.NoticeDTO;
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

	// 공지사항 페이징
	public Page<NoticeDTO> getAllNotices(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findAll(pageable);
        return notices.map(notice -> {
            NoticeDTO dto = new NoticeDTO();
            dto.setNoticeNo(notice.getNoticeNo());
            dto.setViewCount(notice.getViewCount());
            dto.setUserNo(notice.getMemberEntity().getUserNo());
            dto.setTitle(notice.getTitle());
            dto.setContent(notice.getContent());
            dto.setId(notice.getMemberEntity().getId());
            dto.setCreatedDate(notice.getCreatedDate());
            dto.setModifiedDate(notice.getModifiedDate());
            dto.setStatus(notice.getStatus());
            
            return dto;
        });
    }
	
	// 공지사항 읽기
	public Notice getNoticeByNoticeNo(int noticeNo) {
	    return noticeRepository.findByNoticeNo(noticeNo);
	}
	
	public void updateNotice(int noticeNo, String newTitle, String newContent) {
		Notice notice = noticeRepository.findByNoticeNo(noticeNo);
		if (notice == null) {
		    throw new IllegalArgumentException("해당 글번호의 공지사항이 없습니다.");
		}
		notice.setTitle(newTitle);
		notice.setContent(newContent);
		notice.setModifiedDate(new Date());
		noticeRepository.save(notice);
	}

}
