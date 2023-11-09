package com.devrun.youtube;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public VideoDto getVideoDtoByVideoNo(Long videoNo) {
        Optional<Video> videoOptional = videoRepository.findById(videoNo);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            VideoDto videoDto = new VideoDto();
            //videoDto.setUploadDate(video.getUploadDate());
            videoDto.setVideoId(video.getVideoId());
            //videoDto.setTotalPlayTime(video.getTotalPlayTime());
            videoDto.setVideoLink(video.getVideoLink());
            videoDto.setVideoTitle(video.getVideoTitle());
            // 필요한 다른 속성을 설정합니다.
            return videoDto;
        } else {
            return null;
        }        
    }
    
    
   
    
}