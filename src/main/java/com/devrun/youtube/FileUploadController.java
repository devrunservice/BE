package com.devrun.youtube;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final YouTubeUploader youTubeUploader;
    private final VideoRepository videoRepository;

    public FileUploadController(YouTubeUploader youTubeUploader, VideoRepository videoRepository) {
        this.youTubeUploader = youTubeUploader;
        this.videoRepository = videoRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideos(@RequestParam("videoFileList") List<MultipartFile> videoFileList) throws Exception {
        if (videoFileList.isEmpty()) {
            return ResponseEntity.badRequest().body("동영상 파일을 선택해주세요.");
        }

        List<VideoInfo> videoInfoList = new ArrayList<>();

        // 업로드된 동영상 정보를 가져와서 VideoInfo 리스트에 추가
        for (MultipartFile videoFile : videoFileList) {
            VideoInfo videoInfo = youTubeUploader.uploadVideo(videoFile, null);
            videoInfoList.add(videoInfo);
        }

        // 업로드된 비디오 정보를 기반으로 데이터베이스 업데이트
        for (VideoInfo videoInfo : videoInfoList) {
            String videoId = videoInfo.getvideoId();
            Video existingVideo = videoRepository.findByvideoNo(videoId);

            if (existingVideo != null) {
                // 기존 비디오 정보 업데이트
                existingVideo.setUploadDate(new Date());
                existingVideo.setTotalPlayTime("0"); // 재생 시간 초기값 0 설정
                existingVideo.setVideoLink("https://www.youtube.com/watch?v=" + videoId);
            } else {
                // 기존 비디오 정보가 없으면 새로운 비디오 정보 생성
                Video newVideo = new Video();
                newVideo.setVideoId(videoId);
                newVideo.setUploadDate(new Date());
                newVideo.setTotalPlayTime("0");
                newVideo.setVideoLink("https://www.youtube.com/watch?v=" + videoId);
                videoRepository.save(newVideo);
            }
        }

        return ResponseEntity.ok("동영상 업로드가 완료되었습니다.");
    }

}
