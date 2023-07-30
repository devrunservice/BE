package com.devrun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.PointDTO;
import com.devrun.entity.PointEntity;
import com.devrun.repository.PointRepository;

@RestController
public class PointController {
	
	@Autowired
	private PointRepository pointRepository;
	
	@PostMapping("/applyPoint")
	public ResponseEntity<?> applyPoint(@RequestBody PointDTO pointDTO) {		
		
		
	    int userno = pointDTO.getUserNo();
	    int amount = pointDTO.getAmount();
	    int userPoint = pointDTO.getUserPoint(); 

	    // 사용자의 포인트 정보를 조회
	    PointEntity pointEntity = pointRepository.findByUserNo(userno);
	    
	    if (pointEntity == null) {
	        // 사용자의 포인트 정보가 없을 경우 에러 응답 반환
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Point information not found for the user");
	    }

	    int nowPoint  = pointEntity.getMypoint();
	    
	    if (nowPoint < userPoint) {
	        // 사용자의 포인트가 부족하여 처리할 수 없는 경우 에러 응답 반환
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient points");
	    }

	    // 사용자의 포인트에서 지불할 금액만큼 차감
	    int updatedPoint = nowPoint - userPoint;
	    pointEntity.setMypoint(updatedPoint);
	    pointRepository.save(pointEntity);
	    
	    // 금액에 포인트 사용
	    int amountPaid = amount - userPoint;

	    // 지불한 금액을 응답으로 반환
	    return ResponseEntity.ok(amountPaid);
	}

	




}
