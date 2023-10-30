package com.devrun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.MycouresDTO;
import com.devrun.dto.QueryLectureByKeywordDTO;
import com.devrun.dto.SectionInfo;
import com.devrun.dto.VideoInfo;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.entity.MyLectureProgress;
import com.devrun.repository.MylectureProgressRepository;
import com.devrun.repository.MylectureRepository;
import com.devrun.util.JWTUtil;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureSection;
import com.devrun.youtube.LectureSectionRepository;
import com.devrun.youtube.LectureService;
import com.devrun.youtube.Video;
import com.devrun.youtube.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyLectureService {

	private final MylectureRepository mylectureRepository;
	private final MylectureProgressRepository mylectureProgressRepository;
	private final LectureService lectureService;
	private final VideoRepository videoRepository;
	private final LectureSectionRepository lectureSectionRepository;

	public List<MycouresDTO> findMycoures(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		List<MyLecture> myLectureList = verifyUserHasLecture(userEntity, lecture);

		return convertMyLectureToMycouresDTO(myLectureList);
	}	

	public Map<String, Object> progress(MemberEntity userEntity, String videoid, int currenttime) {
		Video videoentity = videoRepository.findByVideoId(videoid);
		Optional<List<MyLecture>> mylecture = mylectureRepository.findByMemberentityAndLecture(userEntity, videoentity.getLecture());
		MyLectureProgress mylectureProgressEntity = mylectureProgressRepository.findByMyLectureAndVideo(userEntity, videoentity);
		int totalplaytime = mylectureProgressEntity.getVideo().getTotalPlayTime();
		int progressInt = (int)((double)currenttime / (double)totalplaytime * 100);	
		mylectureProgressEntity.setProgress(progressInt);
		mylectureProgressEntity.setTimecheck(currenttime);
		mylectureProgressRepository.save(mylectureProgressEntity);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user name : ", userEntity.getName());
		map.put("video id : ", videoid);
		map.put("video Total Play Time", totalplaytime);
		map.put("progress", progressInt);

		return map;

	}
	
	public List<MyLecture> verifyUserHasLecture(MemberEntity userEntity, Lecture lecture) {
		Optional<List<MyLecture>> optional = mylectureRepository.findByMemberentityAndLecture(userEntity, lecture);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new NoSuchElementException("This User isn't taking this Lecture!");
		}
	}

	public List<MycouresDTO> convertMyLectureToMycouresDTO(List<MyLecture> myLectureList) {
		List<MyLectureProgress> myCouresList = mylectureProgressRepository.findByMyLectureIn(myLectureList);
		List<MycouresDTO> mycouresList = new ArrayList<MycouresDTO>();
		for (MyLecture myLecture : myLectureList) {
			MycouresDTO mycouresDTO = new MycouresDTO(myLecture);

			List<SectionInfo> sectionInfolist = new ArrayList<SectionInfo>();
			for (LectureSection section : myLecture.getLecture().getLectureSections()) {
				SectionInfo sectioninfo = new SectionInfo(section);
				List<VideoInfo> videoInfolist = new ArrayList<VideoInfo>();
				for (MyLectureProgress myprogress : myCouresList) {
					if (myprogress.getMyLecture().equals(myLecture)) {
						VideoInfo videoinfo = new VideoInfo(myprogress);
						videoInfolist.add(videoinfo);

					}
				}
				sectioninfo.setVideoInfo(videoInfolist);
				sectionInfolist.add(sectioninfo);
			}
			mycouresDTO.setSectionInfo(sectionInfolist);
			mycouresList.add(mycouresDTO);
		}

		return mycouresList;

	}
}
