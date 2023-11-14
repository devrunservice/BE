package com.devrun.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.devrun.dto.MylectureReviewDTO;
import com.devrun.dto.ReviewRequest;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.entity.MylectureReview;
import com.devrun.repository.MyLectureReviewRepository;
import com.devrun.repository.MylectureRepository;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MylectureReviewService {
	
	private final MyLectureReviewRepository reviewRepository;
	private final MylectureRepository mylectureRepository;
	private final LectureService lectureService;
	private final MyLectureService myLectureService;

	public void saveReview(MemberEntity userEntity, ReviewRequest reviewRequest) {
		Lecture lecture = lectureService.findByLectureID(reviewRequest.getLectureId());
		MyLecture myLectureList = myLectureService.verifyUserHasLecture(userEntity , lecture);
		if(myLectureList.getLectureProgress() == 100) {
			MylectureReview mylectureReview = new MylectureReview();
			mylectureReview.setLecture(myLectureList.getLecture());
			mylectureReview.setReviewContent(reviewRequest.getReviewContent());
			mylectureReview.setReviewRating(reviewRequest.getReviewRating());
			mylectureReview.setUserNo(userEntity);
			reviewRepository.save(mylectureReview);
		} else {
			throw new NoSuchElementException("This User isn't complete this Lecture!");
		}
		
	}
	

	public List<MylectureReviewDTO> reviewList(Long lectureId , int page) {
		page = page <= 1 ? 0 : page - 1;
		PageRequest pageRequest = PageRequest.of(page, 10, Direction.DESC, "reviewDate");
		Lecture lecture = lectureService.findByLectureID(lectureId);
		Page<MylectureReview> pageReview =reviewRepository.findAllByLecture(lecture , pageRequest);
		List<MylectureReviewDTO> dtos = new ArrayList<MylectureReviewDTO>();
		for(MylectureReview my : pageReview) {
			MylectureReviewDTO dto = new MylectureReviewDTO();
			dto.setProfileimgsrc(my.getUserNo().getProfileimgsrc());
			dto.setReviewContent(my.getReviewContent());
			dto.setReviewDate(my.getReviewDate());
			dto.setReviewRating(my.getReviewRating());
			dto.setUserId(my.getUserNo().getId());
			dtos.add(dto);
		}
		
		return dtos;
	}
	
	
	
	
	
}
