package com.devrun.dto;

import java.util.Date;

import lombok.Data;

@Data
public class VideoSearch {
    private String videoId;
    private Date uploadDate;
    private String fileName;
    private String totalPlayTime;
    private String videoLink;
    private String videoTitle;
    private String videofile;
    private int SectionNumber;
    private String SectionTitle;
    
}