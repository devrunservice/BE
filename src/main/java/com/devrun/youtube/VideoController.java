package com.devrun.youtube;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/{videoNo}")
    public ResponseEntity<VideoDto> getVideoById(@PathVariable Long videoNo) {
        VideoDto videoDto = videoService.getVideoDtoByVideoNo(videoNo);
        if (videoDto != null) {
            return ResponseEntity.ok(videoDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}