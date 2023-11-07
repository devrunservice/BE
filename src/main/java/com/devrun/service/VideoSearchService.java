package com.devrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.devrun.youtube.Video;
import com.devrun.repository.VideoSearchRepository;

@Service
public class VideoSearchService {
	
	@Autowired
    private VideoSearchRepository videoSearchRepository;
	
	
	
	 public Video getVideoDetails(Long videoId) throws NotFoundException {
		    Video video = videoSearchRepository.findById(videoId)
		        .orElseThrow(() -> new NotFoundException());
		    
		    Video videoDto = new Video();
		    videoDto.setVideoId(video.getVideoId());
		    return videoDto;
		}

		
	    public Video getVideoPageData(Long videoId) throws NotFoundException {
	        Video video = videoSearchRepository.findById(videoId)
	                .orElseThrow(() -> new NotFoundException());

	        Video pageDataDto = new Video();
	        pageDataDto.setVideoId(video.getVideoId());
	        pageDataDto.setVideoTitle(video.getVideoTitle());

	        // 관련 영상의 링크
	        String videoLink = video.getVideoLink();
	        pageDataDto.setVideoLink(videoLink);

	        // 이전 강의 영상을 확인하고 링크 설정
	        Video previousVideo = videoSearchRepository.findPreviousVideoByVideoIdLessThan(videoId);
	        if (previousVideo != null) {
	            String previousVideoLink = previousVideo.getVideoLink();
	            pageDataDto.setVideoLink(previousVideoLink);
	        }

	        // 다음 강의 영상을 확인하고 링크 설정
	        Video nextVideo = videoSearchRepository.findNextVideoByVideoIdGreaterThan(videoId);
	        if (nextVideo != null) {
	            String nextVideoLink = nextVideo.getVideoLink();
	            pageDataDto.setVideoLink(nextVideoLink);
	        }

	        return pageDataDto;
	    }
}
