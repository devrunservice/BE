package com.devrun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devrun.repository.MylectureProgressRepository;
import com.devrun.repository.MylectureRepository;
import com.devrun.youtube.LectureRepository;
import com.devrun.youtube.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyLectureProgressService {

	private final MylectureProgressRepository mylectureProgressRepository;

	public List<Map<String, Object>> mylecturelist(String accessToken) {
		List<Map<String, Object>> testmodels = new ArrayList<Map<String, Object>>();

		for (int i = 1; i < 10; i++) {
			Map<String, Object> testmodel = new HashMap<String, Object>();
			testmodel.put("title", "강의 제목 " + i);
			testmodel.put("mentoName", "강사이름 " + i);
			testmodel.put("thumbnail",
					"https://devrun-dev-bucket.s3.ap-northeast-2.amazonaws.com/public.lecture.images/20231001");
			testmodel.put("progressRate", (int) (Math.random() * 101));
			testmodel.put("rating", String.format("%.1f", (int) (Math.random() * 11) * 0.5));
			testmodel.put("expiryDate", "2023-" + String.format("%02d", (int) ((Math.random() * 12) + 1)) + "-"
					+ String.format("%02d", (int) ((Math.random() * 31) + 1)));
			testmodels.add(testmodel);
		}
		return testmodels;
	}

	public Object mymm() {
		return mylectureProgressRepository.findAll();
	}

}
