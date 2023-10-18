package com.devrun.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.devrun.dto.QueryLectureByKeywordDTO;
import com.devrun.entity.MemberEntity;

@Service
public class LectureService {

	private final LectureRepository lectureRepository;
	private final LectureSectionRepository sectionRepository;
	private final LecturecategoryRepository categoryRepository;
	private final VideoRepository videoRepository;

	@Autowired
	public LectureService(LectureRepository lectureRepository, LectureSectionRepository sectionRepository,
			LecturecategoryRepository categoryRepository, VideoRepository videoRepository) {
		this.lectureRepository = lectureRepository;
		this.sectionRepository = sectionRepository;
		this.categoryRepository = categoryRepository;
		this.videoRepository = videoRepository;
	}

//    public Lecture saveLecture(CreateLectureRequestDto requestDto, String thumbnailUrl, LecturecategoryDto categoryDto) {
//    	
//    	
//    	
//    	// 앞단에서 선택한 옵션값인 categoryDto를 사용하여 검증 및 매핑된 Lecture Category 객체를 가져옵니다.
//        LectureCategory lectureCategory = convertToLectureCategoryEntity(categoryDto);
//
//        Lecture lecture = convertToLectureEntity(requestDto, lectureCategory, categoryDto);
//        lecture.setLectureThumbnail(thumbnailUrl);
//
//        List<LectureSection> sections = saveLectureSections(requestDto.getLectureSectionList(), lecture);
//        lecture.setLectureSection(sections);
//
//        List<Video> videos = saveVideos(requestDto.getVideoList(), lecture);
//        lecture.setVideos(videos);
//
//        // Set Lecture Tags
//        if (requestDto.getLectureTag() != null) {
//            lecture.setLectureTag(requestDto.getLectureTag());
//        }
//        
//        // 동영상 정보를 업데이트합니다.
//        List<VideoInfo> videoInfoList = new ArrayList<>();
//        saveVideoInfo(videoInfoList, lecture);
//        
//        return lecture;
//    }

	private LectureCategory convertToLectureCategoryEntity(LecturecategoryDto categoryDto) {
		// 앞단에서 선택한 옵션값인 categoryDto를 사용하여 DB에서 해당 카테고리를 조회합니다.
		LectureCategory lectureCategory = categoryRepository.findByCategoryNoAndLectureBigCategoryAndLectureMidCategory(
				categoryDto.getCategoryNo(), categoryDto.getLectureBigCategory(), categoryDto.getLectureMidCategory());

		if (lectureCategory == null) {
			// 선택한 카테고리가 존재하지 않는 경우에 대한 예외 처리를 수행합니다.
			System.err.println(categoryDto);
			throw new IllegalArgumentException("선택한 카테고리가 유효하지 않습니다.");
		}

		return lectureCategory;
	}

	private List<LectureSection> saveLectureSections(List<LectureSectionDto> sectionDtoList, Lecture lecture) {
		List<LectureSection> sections = new ArrayList<>();
		for (LectureSectionDto sectionDto : sectionDtoList) {
			LectureSection section = convertToLectureSectionEntity(sectionDto, lecture);
			sections.add(section);
		}
		return sectionRepository.saveAll(sections);
	}

	private LectureSection convertToLectureSectionEntity(LectureSectionDto sectionDto, Lecture lecture) {
		LectureSection section = new LectureSection();
		section.setSectionNumber(sectionDto.getSectionNumber());
		section.setSectionTitle(sectionDto.getSectionTitle());
		section.setLecture(lecture);
		return section;
	}

//    private List<Video> saveVideos(List<VideoDto> videoDtoList, Lecture lecture ) {
//        List<Video> videos = new ArrayList<>();
//        for (VideoDto videoDto : videoDtoList) {
//            Video video = convertToVideoEntity(videoDto, lecture);
//
//            Long lectureSectionId = videoDto.getLectureSectionId();
//            if (lectureSectionId != null) {
//                LectureSection section = sectionRepository.findById(lectureSectionId).orElse(null);
//                if (section != null) {
//                    video.setLectureSection(section);
//                }
//            }
//
//            videos.add(video);
//        }
//        return videoRepository.saveAll(videos);
//    }

	private Video convertToVideoEntity(VideoDto videoDto, Lecture lecture) {
		Video video = new Video();
		video.setUploadDate(videoDto.getUploadDate());
		video.setFileName(videoDto.getFileName());
		video.setVideoId(videoDto.getVideoId());
		// video.setTotalPlayTime(videoDto.getTotalPlayTime());
		video.setVideoLink(videoDto.getVideoLink());
		video.setVideoTitle(videoDto.getVideoTitle());

		video.setLecture(lecture);
		return video;
	}

//    private Lecture convertToLectureEntity(CreateLectureRequestDto requestDto, LectureCategory lectureCategory,LecturecategoryDto categoryDto ) {
//    	 // 앞단에서 선택한 옵션값인 categoryDto를 사용하여 검증 및 매핑된 Lecture Category 객체를 가져옵니다.
//        LectureCategory lecturecategory = convertToLectureCategoryEntity(categoryDto);
//        
//        Lecture lecture = new Lecture();
//
//        // CreateLectureRequestDto에서 필요한 데이터를 가져와서 Lecture 엔티티에 설정합니다.
//        lecture.setLectureName(requestDto.getLectureName());
//        lecture.setLectureIntro(requestDto.getLectureIntro());
//        lecture.setLecturePrice(requestDto.getLecturePrice());
//        lecture.setLectureTag(requestDto.getLectureTag());
//
//     // 매핑된 Category 객체를 Lecture 엔티티의 속성으로 설정합니다.
//        lecture.setLectureCategory(lecturecategory);
//
//     // LectureSection 설정
//     // CreateLectureRequestDto에 있는 섹션 정보 리스트를 가져와서 LectureSection 엔티티 객체들을 생성하고 Lecture 엔티티와 연결합니다.
//     List<LectureSection> sections = new ArrayList<>();
//     for (LectureSectionDto sectionDto : requestDto.getLectureSectionList()) {
//         LectureSection section = new LectureSection();
//         section.setSectionNumber(sectionDto.getSectionNumber());
//         section.setSectionTitle(sectionDto.getSectionTitle());
//
//         // Lecture 객체와 연결 (LectureSection 객체에 Lecture 객체 설정)
//         section.setLecture(lecture);
//
//         sections.add(section);
//     }
//     lecture.setLectureSection(sections);
//
//
//        // Lecture 엔티티를 데이터베이스에 저장합니다.
//        lecture = lectureRepository.save(lecture);
//
//        // 저장된 Lecture 엔티티를 반환합니다.
//        return lecture;
//    }

//    public void saveVideoInfo(List<VideoInfo> videoInfoList, Lecture lecture) {
//        for (VideoInfo videoInfo : videoInfoList) {
//            Video existingVideo = videoRepository.findByVideoId(videoInfo.getVideoId());
//            if (existingVideo != null) {
//                existingVideo.setUploadDate(videoInfo.getUploadDate());
//                existingVideo.setFileName(videoInfo.getFileName());
//                existingVideo.setTotalPlayTime(videoInfo.getTotalPlayTime());
//                existingVideo.setUrl(videoInfo.getUrl()); 
//
//                // 비디오 테이블에 저장된 정보를 업데이트합니다.
//                videoRepository.save(existingVideo);
//            }
//        }
//    }

	// 마지막 섹션 ID를 가져오는 메서드 추가
	public Long getLastSectionId() {
		return sectionRepository.findLastSectionId();
	}

	public Lecture saveLecture2(CreateLectureRequestDto requestDto, String lectureThumnailUrl) {

		Lecture savelecture = new Lecture();
		savelecture.setLectureName(requestDto.getLectureName());
		savelecture.setLecturePrice(requestDto.getLecturePrice());
		savelecture.setLectureIntro(requestDto.getLectureIntro());
		// savelecture.setLectureStart(requestDto.getLectureStart());
		savelecture.setLectureTag(requestDto.getLectureTag());
		savelecture.setLectureThumbnail(lectureThumnailUrl);
		savelecture.setLectureCategory(requestDto.getLectureCategory());
		Lecture savedlecture = lectureRepository.save(savelecture);
		return savedlecture;
	}

	public List<LectureSection> saveLectureSection(Lecture savedlecture, List<LectureSectionDto> sectionlist) {

				List<LectureSection> savedsectionlist = new ArrayList<LectureSection>();
		for (LectureSectionDto lectureSectionDto : sectionlist) {
			LectureSection lectureSection = new LectureSection();
			lectureSection.setLecture(savedlecture);
			lectureSection.setSectionNumber(lectureSectionDto.getSectionNumber());
			lectureSection.setSectionTitle(lectureSectionDto.getSectionTitle());
			LectureSection savedlectureSection = sectionRepository.save(lectureSection);
			savedsectionlist.add(savedlectureSection);
			
		}
		
		return savedsectionlist;
	}

	public void saveVideo(Lecture savedlecture, List<LectureSection> savedlectureSeciton, List<VideoDto> videolist) {

		for (VideoDto videoDto : videolist) {
			Video savevideo = new Video();
			savevideo.setLecture(savedlecture);
			savevideo.setLectureSection(savedlectureSeciton.get(0));
			savevideo.setVideoTitle(videoDto.getVideoTitle());
			savevideo.setVideoLink(videoDto.getVideoLink());
			videoRepository.save(savevideo);
		}

	}
	/*
	 * 데이터베이스에 저장된 강의 entity 중 강의명, 강의 소갯글, 강사명 속성에 특정한 검색어를 포함하는 entity의 일부 속성(강의명
	 * , 강의 소개글, 강사명, 강의 평점, 강의 가격, 썸네일 URI , 카테고리 분류 중-소 , 속성) 집합을 가져옴
	 */

	// 유저가 없는 경우
	public List<QueryLectureByKeywordDTO> QueryLectureByKeyword(String keyword, PageRequest pageable) {
		Page<Lecture> l1 = lectureRepository.findByLectureNameContainsOrLectureIntroContains(keyword, pageable);
		List<QueryLectureByKeywordDTO> list = convertLectureToDTO(l1);
		return list;
	};

	// 유저가 있는 경우
	public List<QueryLectureByKeywordDTO> QueryLectureByKeyword(String keyword, List<MemberEntity> m1,
			Pageable pageable) {

		Page<Lecture> l1 = lectureRepository.findByLectureNameContainsOrLectureIntroContainsOrMentoIdIn(keyword, m1,
				pageable);
		List<QueryLectureByKeywordDTO> list = convertLectureToDTO(l1);

		return list;
	}

	public List<QueryLectureByKeywordDTO> convertLectureToDTO(Page<Lecture> lectureList) {
		return lectureList.stream().map(QueryLectureByKeywordDTO::new).collect(Collectors.toList());
	}

	public List<QueryLectureByKeywordDTO> findLecturesWithCategroys(List<LectureCategory> categorys, String keyword,
			PageRequest pageRequest) {
		System.out.println(keyword);
		Page<Lecture> l1 = lectureRepository.findLecturesWithCategroy(categorys, keyword, pageRequest);
		for (Lecture lecture : l1) {
			System.out.println(lecture.getLectureName());

		}
		List<QueryLectureByKeywordDTO> list = convertLectureToDTO(l1);

		return list;
	}

	public List<QueryLectureByKeywordDTO> findLecturesWithCategroy(LectureCategory category, String keyword,
			PageRequest pageRequest) {
		Page<Lecture> l1 = lectureRepository.findLecturesWithCategroy(category, keyword, pageRequest);
		List<QueryLectureByKeywordDTO> list = convertLectureToDTO(l1);

		return list;
	}

}
