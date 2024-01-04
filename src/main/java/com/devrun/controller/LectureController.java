package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.LectureIntroduceDTO;
import com.devrun.dto.VideoDetailsDto;
import com.devrun.dto.lectureDetailDto;
import com.devrun.entity.MemberEntity;
import com.devrun.service.LecuturesearchService;
import com.devrun.service.MemberService;
import com.devrun.service.VideoSearchService;
import com.devrun.util.JWTUtil;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureService;
import com.devrun.youtube.Video;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {
	private final LectureService lectureService;
    private final LecuturesearchService lecuturesearchService;
    private final VideoSearchService videoSearchService;
    private final MemberService memberService;
   
@GetMapping("/{lectureId}")
public lectureDetailDto getLectureDetails(HttpServletRequest request , @PathVariable Long lectureId) throws NotFoundException {
	Lecture lecture = lecuturesearchService.getLectureDetails(lectureId);
	lectureDetailDto dto = null;
	if(request.getHeader("Access_token") != null) {
		String userAccessToken = request.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		dto = lectureService.getlecturedetail(userEntity, lectureId);
	}
	else 
	{
		MemberEntity userEntity = null;
		dto = lectureService.getlecturedetail(userEntity, lectureId);
	}
    return dto;
}

@GetMapping("/{lectureId}/detail")
public Lecture getLectureDetailsMapping(@PathVariable Long lectureId) throws NotFoundException {
	Lecture lecturedetail = lecuturesearchService.getLectureDetailsMapping(lectureId);
    return lecturedetail;
}


@GetMapping("/videos/{videoId}")
public Video getVideoDetails(@PathVariable Long videoId) throws NotFoundException {
    Video video = videoSearchService.getVideoDetails(videoId);
    return video;
}


@GetMapping("/videos/{videoId}/Playpage")
public VideoDetailsDto getVideoPageData(@PathVariable Long videoId) throws NotFoundException {
    Video currentVideo = videoSearchService.getVideoDetails(videoId);
    Video previousVideo = videoSearchService.getPreviousVideo(videoId);
    Video nextVideo = videoSearchService.getNextVideo(videoId);

    VideoDetailsDto videoDetailsDto = new VideoDetailsDto();
    videoDetailsDto.setCurrentVideo(currentVideo);
    videoDetailsDto.setPreviousVideo(previousVideo);
    videoDetailsDto.setNextVideo(nextVideo);

    return videoDetailsDto;
}

@PostMapping("/detailupdate")
@ApiOperation(value = "강의 상세 소개 수정")
public LectureIntroduceDTO lecturedetailupdate(HttpServletRequest request,@RequestBody LectureIntroduceDTO introDto) {
	String Accesstoken = request.getHeader("Access_token");
	String userid = JWTUtil.getUserIdFromToken(Accesstoken);
	return lectureService.getlecturedetailupdate(userid, introDto);
}

}