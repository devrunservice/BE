package com.devrun.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.PointHistoryDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;
import com.devrun.repository.PointHis;
import com.devrun.repository.PointHistoryRepository;
import com.devrun.repository.PointRepository;
import com.devrun.service.MemberService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class PointhistoryController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private PointHistoryRepository pointHistoryRepository;
	@Autowired
	private PointRepository pointRepository;
	@GetMapping("/PointHistory")
	@ApiOperation("포인트 히스토리, user_no로 조회하여 포인트 히스토리 불러옵니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value= "필요한 페이지" , paramType = "header",dataTypeClass = Integer.class, example = "1"),
		@ApiImplicitParam(name = "size", value= "각 페이지에 표시할 항목 수", paramType = "header",dataTypeClass = Integer.class, example = "10")
	})
	public ResponseEntity<?> pointhistory(@RequestParam("page") int page, @RequestParam("size") int size){
		
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity member = memberService.findById(userid);
		
		int usrno = member.getUserNo(); 
		
		PageRequest pageRequest = PageRequest.of(page -1, size);        

        Page<PointHis> PointhistoryPage = pointHistoryRepository.findAllbyPointHistoryEntity(usrno,pageRequest);

        if (PointhistoryPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("결제 정보가 없습니다.");
        }
	    PointEntity PointEntity = pointRepository.findByMemberEntity_userNo(usrno);
        int mypoint = PointEntity.getMypoint();
        
        PointHistoryDTO HistoryDTO = new PointHistoryDTO(mypoint, PointhistoryPage);
        return ResponseEntity.ok(HistoryDTO);	
        
       }
		
		
	}

