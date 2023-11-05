package com.devrun.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devrun.entity.MyLecture;
import com.devrun.entity.MyLectureProgress;
import com.devrun.repository.MylectureProgressRepository;
import com.devrun.youtube.Video;
import com.devrun.youtube.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyLectureProgressService {

	private final MylectureProgressRepository mylectureProgressRepository;
	private final VideoRepository videoRepository;



}
