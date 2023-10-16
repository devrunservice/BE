package com.devrun.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.repository.MylectureRepository;
import com.devrun.util.JWTUtil;
import com.devrun.youtube.Video;
import com.devrun.youtube.VideoRepository;


@Service
public class MyLectureProgressService {
	
	@Autowired
	private MylectureRepository mylectureRepository;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private VideoRepository videoRepository;
	
	public Map<String , Object> progress(String accesstoken , String videoid , int currenttime) {
		
		  
		  //String userid = JWTUtil.getUserIdFromToken(accesstoken);
		  //System.out.println("user id : "+ userid);
		  MemberEntity memberentity = memberService.findById("seokhwan1");
		  System.out.println("user id : "+ memberentity.getId());
		  Video videoentity = videoRepository.findByVideoId(videoid);
		  System.out.println("video id : "+ videoentity.getVideoId());
		  MyLecture mylectureEntity = mylectureRepository.findByMemberentityAndVideo(memberentity,videoentity);
		  double totalplaytime = mylectureEntity.getVideo().getTotalPlayTime();
		  System.out.println("totalplaytime : "+ totalplaytime);
		  System.out.println("currenttime : "+ currenttime);
		  double progressInt = currenttime / totalplaytime * 100.0;
		  String progressString = String.format("%.2f", progressInt) + "%";
		  mylectureEntity.setProgress(progressString);
		  mylectureRepository.save(mylectureEntity);
		 
		  Map<String , Object> map = new HashMap<String, Object>();
		  map.put("user name : ", "seokhwan1");
		  map.put("video id : ", videoid);
		  map.put("video Total Play Time", totalplaytime);
		  map.put("progress", progressString);
		  
		  return map;
		
		
		
	}

}
