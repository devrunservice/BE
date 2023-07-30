package com.devrun.youtube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LectureregistController {

    private final LectureService lectureService;

    @Autowired
    public LectureregistController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping("/lectureregist")
    public void createLecture(@RequestBody CreateLectureRequestDto requestDto) {
        lectureService.saveLecture(requestDto);
    }
}
