package com.devrun.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
		myLectureService.verifyUserHasLecture(userEntity, lecture);
		List<MyLecture> myLectureList = myLectureService.verifyUserHasLecture(userEntity , lecture);
		if(myLectureList.size() == 1) {
			MylectureReview mylectureReview = new MylectureReview(
					myLectureList.get(0),
					reviewRequest.getReviewContent(), 
					reviewRequest.getReviewRating()
					);
			reviewRepository.save(mylectureReview);
		}
		
	}
	
	public void removeReview(MemberEntity userEntity, Long mylectureReviewNo) {
		MylectureReview myReview = verifyUserHasReview(userEntity , mylectureReviewNo);
		reviewRepository.delete(myReview);
		
	}
	
	public MylectureReview verifyUserHasReview(MemberEntity userEntity, Long mylectureReviewNo) {
		Optional<List<MyLecture>> mylecture = mylectureRepository.findByMemberentity(userEntity);
		Optional<MylectureReview> optional = reviewRepository.findByMyLectureAndMylectureReviewNo(mylecture.get().get(0), mylectureReviewNo);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new NoSuchElementException("This user did not write this review.");
		}
	}
	
	
	
	
	
}