package com.devrun.youtube;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> uploadVideo(@RequestParam("videoFile") MultipartFile videoFile) throws Exception {
        if (videoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("동영상 파일을 선택해주세요.");
        }

        try {
            // 동영상 파일을 업로드하고 YouTube에 업로드된 비디오의 ID를 받아옴
            VideoInfo videoinfo = youTubeUploader.uploadVideo(videoFile, null);
            
           
            // 업로드된 동영상 정보를 데이터베이스에 저장
            Video video = new Video();
            
            video.setVideoId(videoinfo.getvideoId());
            video.setFileName(videoFile.getOriginalFilename());
            video.setUploadDate(new Date());
            
            // 저장할 비디오 정보에 추가 정보를 설정
            // 예를 들어 비디오 제목, 재생 시간, 비디오 링크 등 추가 가능
            video.setVideoTitle("예시 비디오 제목");
            video.setTotalPlayTime("0"); // 재생 시간 초기값 0 설정
            video.setVideoLink("https://www.youtube.com/watch?v=" + videoinfo.getvideoId());

            
            videoRepository.save(video);

            return ResponseEntity.ok("동영상 업로드가 완료되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("동영상 업로드 중 오류가 발생했습니다.");
        }
    }
}
