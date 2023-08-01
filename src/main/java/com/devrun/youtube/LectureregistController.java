package com.devrun.youtube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LectureregistController {
    private final LectureService lectureService;
    private final FileUploadController fileUploadController;

    @Autowired
    public LectureregistController(LectureService lectureService, FileUploadController fileUploadController) {
        this.lectureService = lectureService;
        this.fileUploadController = fileUploadController;
    }

    @PostMapping("/lectureregist")
    public void createLecture(@RequestBody CreateLectureRequestDto requestDto) {
        System.err.println(requestDto);
        lectureService.saveLecture(requestDto);

        try {
            fileUploadController.uploadVideo(requestDto.getVideoFile());
           
            System.err.println(requestDto);
            // 업로드 성공 시 동영상 정보가 이미 강의 등록에 필요한 requestDto에 반영되어 있음
            lectureService.saveLecture(requestDto);
        } catch (Exception e) {
            System.err.println(requestDto);

            // 예외 처리
            // ...
        }
    }
}
