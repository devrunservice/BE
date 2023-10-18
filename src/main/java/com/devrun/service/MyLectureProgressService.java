package com.devrun.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.QueryLectureByKeywordDTO;
import com.devrun.dto.TotalProgress;
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

	public Map<String, Object> progress(String accesstoken, String videoid, int currenttime) {

		 
		String userid = JWTUtil.getUserIdFromToken(accesstoken);
		MemberEntity memberentity = memberService.findById(userid);
		Video videoentity = videoRepository.findByVideoId(videoid);
		MyLecture mylectureEntity = mylectureRepository.findByMemberentityAndVideo(memberentity, videoentity);
		int totalplaytime = mylectureEntity.getVideo().getTotalPlayTime();
		int progressInt = (int)((double)currenttime / (double)totalplaytime * 100);		
		mylectureEntity.setProgress(progressInt);
		mylectureEntity.setTimecheck(currenttime);
		mylectureRepository.save(mylectureEntity);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user name : ", "seokhwan1");
		map.put("video id : ", videoid);
		map.put("video Total Play Time", totalplaytime);
		map.put("progress", progressInt);

		return map;

	}

	public List<Map<String, Object>> mylecturelist(String accessToken) {
		List<Map<String, Object>> testmodels = new ArrayList<Map<String, Object>>();
		
		for (int i = 1; i < 10; i++) {
			Map<String, Object> testmodel = new HashMap<String, Object>();
			testmodel.put("lectureName", "강의 제목 " + i);
			testmodel.put("lectureThumbnail",
					"https://devrun-dev-bucket.s3.ap-northeast-2.amazonaws.com/public.lecture.images/20231001");
			testmodel.put("lectureProgress", (int) (Math.random() * 101));
			testmodel.put("lectureRating", String.format("%.1f", (double) Math.random() * 5.0));
			testmodel.put("lectureExpiryDate",
					"2023-" + String.format("%02d" ,(int) ((Math.random() * 12) + 1)) + "-" + String.format("%02d" ,(int) ((Math.random() * 31) + 1)));
			testmodels.add(testmodel);
		}
		return testmodels;
	}
	
	public List<TotalProgress> mylecturelistReal(String accessToken) {
		
		//String userid = JWTUtil.getUserIdFromToken(accessToken);
		MemberEntity memberentity = memberService.findById("seokhwan2");
		List<TotalProgress> tp=mylectureRepository.gettotalprogress(memberentity);
		
		return tp;
	}

}
