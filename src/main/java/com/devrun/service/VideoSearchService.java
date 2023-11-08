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
	 
	 public Video getPreviousVideo(Long videoId) {
		    return videoSearchRepository.findById(videoId - 1).orElse(null);
		}

		public Video getNextVideo(Long videoId) {
		    return videoSearchRepository.findById(videoId + 1).orElse(null);
		}
}
