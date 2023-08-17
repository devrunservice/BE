package com.devrun.youtube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createLecture(@RequestBody CreateLectureRequestDto requestDto) {
        try {
            // 강의 및 비디오 정보를 데이터베이스에 저장
            lectureService.saveLecture(requestDto);

            // 동영상 업로드를 시작하기 위해 업로드 페이지로 리다이렉션
            return ResponseEntity.ok("강의 및 비디오 정보가 저장되었습니다. 동영상을 업로드하세요.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }
}

