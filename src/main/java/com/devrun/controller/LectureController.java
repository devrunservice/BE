package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureService;
import com.devrun.youtube.Video;

import io.swagger.annotations.ApiOperation;

import com.devrun.dto.LectureIntroduceDTO;
import com.devrun.dto.VideoDetailsDto;
import com.devrun.service.LecuturesearchService;
import com.devrun.service.VideoSearchService;
import com.devrun.util.JWTUtil;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {
	private final LectureService lectureService;
    private final LecuturesearchService lecuturesearchService;
    private final VideoSearchService videoSearchService;
    

    public LectureController(LectureService lectureService, LecuturesearchService lecuturesearchService ,VideoSearchService videoSearchService ) {
        this.lecuturesearchService = lecuturesearchService;
		this.videoSearchService = videoSearchService;
		this.lectureService = lectureService;
    }



    
@GetMapping("/{lectureId}")
public Lecture getLectureDetails(@PathVariable Long lectureId) throws NotFoundException {
	Lecture lecture = lecuturesearchService.getLectureDetails(lectureId);
    return lecture;
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

@GetMapping("/detailtest/{lectureid}")
@ApiOperation(value = "강의 상세 소개 컨텐츠")
public LectureIntroduceDTO lecturedetailopen(@PathVariable(name = "id" , required = true) Long lectureId) {
	return lectureService.getlecturedetail(lectureId);
}

@PostMapping("/detailupdate")
@ApiOperation(value = "강의 상세 소개 수정")
public LectureIntroduceDTO lecturedetailupdate(HttpServletRequest request,@RequestBody LectureIntroduceDTO introDto) {
	String Accesstoken = request.getHeader("Access_token");
	String userid = JWTUtil.getUserIdFromToken(Accesstoken);
	return lectureService.getlecturedetailupdate(userid, introDto);
}

}