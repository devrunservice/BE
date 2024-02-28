package com.devrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.devrun.dto.PointHistoryDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;
import com.devrun.repository.PointHis;
import com.devrun.repository.PointHistoryRepository;
import com.devrun.repository.PointRepository;

@Service
public class PointHistoryService {
	@Autowired
	private PointRepository pointRepository;
	@Autowired
	private PointHistoryRepository pointHistoryRepository;
	
	public PointHistoryDTO getPoint(MemberEntity member, int page, int size) {
		int usrno = member.getUserNo(); 
		
		PageRequest pageRequest = PageRequest.of(page -1, size);        

	    Page<PointHis> PointhistoryPage = pointHistoryRepository.findAllbyPointHistoryEntity(usrno,pageRequest);

	    if (PointhistoryPage.isEmpty()) {
	    	throw new IllegalArgumentException("결제 정보가 없습니다.");
	    }
	    PointEntity PointEntity = pointRepository.findByMemberEntity_userNo(usrno);
	    int mypoint = PointEntity.getMypoint();
	    
		return new PointHistoryDTO(mypoint, PointhistoryPage);
	}
    
   }

