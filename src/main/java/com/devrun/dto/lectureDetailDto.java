package com.devrun.dto;

import java.util.ArrayList;
import java.util.List;

import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureCategory;
import com.devrun.youtube.LectureSection;
import com.devrun.youtube.Video;

import lombok.Data;

@Data
public class lectureDetailDto {
	private lecture lecture;
	private List<sections> lectureSections;
	private LectureCategory lectureCategory;

	@Data
	public class lecture {

		private Long lectureId;
		private int lecturePrice, buyCount, mentoNo;
		private double lectureRating;
		private String mentoId, lectureName, lectureIntro, lectureFullIntro;
		private boolean purchaseStatus = false;
		private double progress;
	}
	
	public void setLecture(Lecture l) {
		lecture le = new lecture();
		
		le.setLectureId(l.getLectureid()); 
		le.setLecturePrice(l.getLecturePrice()); 
		le.setBuyCount(l.getBuyCount());
		le.setMentoNo(l.getMentoId().getUserNo());
		le.setLectureRating(l.getLectureRating());
		le.setMentoId(l.getMentoId().getId()); 
		le.setLectureName(l.getLectureName()); 
		le.setLectureIntro(l.getLectureIntro()); 
		le.setLectureFullIntro(null);
		le.setProgress(0);
		
		this.lecture = le;
	}

	@Data
	public class sections {
		private Long sectionId;
		private int sectionNumber;
		private String sectionTitle;
		private List<video> videos;

		public void setVideos(List<Video> videos2) {
			List<video> list = new ArrayList<video>();
			for (Video v : videos2) {
				video vi = new video();
				vi.setTotalPlayTime(v.getTotalPlayTime());
				vi.setVideoId(v.getVideoId());
				vi.setVideoNo(v.getVideoNo());
				vi.setVideoTitle(v.getVideoTitle());
				list.add(vi);
			}
			this.videos = list;
		}

	}

	public void setLectureSections(List<LectureSection> lectureSections) {
		List<sections> list = new ArrayList<lectureDetailDto.sections>();
		for (LectureSection s : lectureSections) {
			sections section = new sections();
			section.setSectionId(s.getSectionid());
			section.setSectionNumber(s.getSectionNumber());
			section.setSectionTitle(s.getSectionTitle());
			section.setVideos(s.getVideos());
			list.add(section);
		}
		this.lectureSections = list;
	}

	public void setLectureCategory(LectureCategory lectureCategory2) {
		this.lectureCategory = lectureCategory2;
	}

	public void setLectureFullIntro(String content) {
		this.lecture.setLectureFullIntro(content);
		
	}
}

@Data
class video {
	private String videoId;
	private String videoTitle;
	private Long videoNo;
	private int totalPlayTime;
}

@Data
class lectureCategory {
	private String lectureBigCategory, lectureMidCategory;
}