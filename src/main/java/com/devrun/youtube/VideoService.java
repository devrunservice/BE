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
            videoDto.setFileName(video.getFileName());
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
    
    
    public VideoDto getVideoDetails(Long videoId) throws NotFoundException {
	    Video video = videoRepository.findById(videoId)
	        .orElseThrow(() -> new NotFoundException());
	    
	    VideoDto videoDto = new VideoDto();
	    videoDto.setVideoId(video.getVideoId());
	    return videoDto;
	}

	
    public VideoDto getVideoPageData(Long videoId) throws NotFoundException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException());

        VideoDto pageDataDto = new VideoDto();
        pageDataDto.setVideoId(video.getVideoId());
        pageDataDto.setVideoTitle(video.getVideoTitle());

        // 관련 영상의 링크
        String videoLink = video.getVideoLink();
        pageDataDto.setVideoLink(videoLink);

        // 이전 강의 영상을 확인하고 링크 설정
        Video previousVideo = videoRepository.findPreviousVideoByVideoIdLessThan(videoId);
        if (previousVideo != null) {
            String previousVideoLink = previousVideo.getVideoLink();
            pageDataDto.setVideoLink(previousVideoLink);
        }

        // 다음 강의 영상을 확인하고 링크 설정
        Video nextVideo = videoRepository.findNextVideoByVideoIdGreaterThan(videoId);
        if (nextVideo != null) {
            String nextVideoLink = nextVideo.getVideoLink();
            pageDataDto.setVideoLink(nextVideoLink);
        }

        return pageDataDto;
    }
    
}