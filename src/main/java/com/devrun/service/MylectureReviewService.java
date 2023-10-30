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
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MylectureReviewService {
	
	private final MyLectureReviewRepository reviewRepository;
	private final LectureService lectureService;
	private final MyLectureService myLectureService;

	public void saveReview(MemberEntity userEntity, ReviewRequest reviewRequest) {
		Lecture lecture = lectureService.findByLectureID(reviewRequest.getLectureId());
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
		
		Optional<MylectureReview> optional = reviewRepository.findByMemberentityAndMylectureReviewNo(userEntity, mylectureReviewNo);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new NoSuchElementException("This user did not write this review.");
		}
	}
	
	
	
}
