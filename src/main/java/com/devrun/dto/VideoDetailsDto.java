package com.devrun.dto;

import com.devrun.youtube.Video;

import lombok.Data;

@Data
public class VideoDetailsDto {
    private Video currentVideo;
    private Video previousVideo;
    private Video nextVideo;

}