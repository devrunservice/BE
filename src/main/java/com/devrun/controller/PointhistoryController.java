package com.devrun.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.PointHistoryDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.service.PointHistoryService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class PointhistoryController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private PointHistoryService pointHistoryService;

	@GetMapping("/PointHistory")
	@ApiOperation("포인트 히스토리, user_no로 조회하여 포인트 히스토리 불러옵니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value= "필요한 페이지" , paramType = "header",dataTypeClass = Integer.class, example = "1"),
		@ApiImplicitParam(name = "size", value= "각 페이지에 표시할 항목 수", paramType = "header",dataTypeClass = Integer.class, example = "10")
	})
	public ResponseEntity<?> pointhistory(@RequestParam("page") int page, @RequestParam("size") int size){
		
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity member = memberService.findById(userid);		
		PointHistoryDTO pointHistoryDTO = pointHistoryService.getPoint(member,page,size);
		return ResponseEntity.ok(pointHistoryDTO);	   
       }
		
		
	}

