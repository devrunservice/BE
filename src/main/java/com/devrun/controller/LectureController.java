package com.devrun.controller;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.youtube.CreateLectureRequestDto;
import com.devrun.youtube.LectureService;
import com.devrun.youtube.VideoDto;
import com.devrun.youtube.VideoService;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;
    private final VideoService videoService;
    

    public LectureController(LectureService lectureService ,VideoService videoService ) {
        this.lectureService = lectureService;
		this.videoService = videoService;
    }



    
@GetMapping("/lectures/{lectureId}")
public CreateLectureRequestDto getLectureDetails(@PathVariable Long lectureId) throws NotFoundException {
	CreateLectureRequestDto lecture = lectureService.getLectureDetails(lectureId);
    return lecture;
}

@GetMapping("/lectures/{lectureId}/detail")
public CreateLectureRequestDto getLectureDetailsMapping(@PathVariable Long lectureId) {
	CreateLectureRequestDto lecturedetail = lectureService.getLectureDetailsMapping(lectureId);
    return lecturedetail;
}


@GetMapping("/videos/{videoId}")
public VideoDto getVideoDetails(@PathVariable Long videoId) throws NotFoundException {
    VideoDto video = videoService.getVideoDetails(videoId);
    return video;
}


@GetMapping("/videos/{videoId}/Playpage")
public VideoDto getVideoPageData(@PathVariable Long videoId) throws NotFoundException {
	VideoDto Playpage = videoService.getVideoPageData(videoId);
    return Playpage;
}


}