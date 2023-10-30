package com.devrun.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

	
	public CreateLectureRequestDto getLectureDetails(Long lectureId) throws NotFoundException {
	    Lecture lecture = lectureRepository.findById(lectureId)
	        .orElseThrow(() -> new NotFoundException());

	    CreateLectureRequestDto lectureDto = new CreateLectureRequestDto();
	    lectureDto.setLectureName(lecture.getLectureName());
	    lectureDto.setLectureIntro(lecture.getLectureIntro());
	    lectureDto.setLecturePrice(lecture.getLecturePrice());
	    lectureDto.setLectureStart(lecture.getLectureStart());
	    lectureDto.setLectureEdit(lecture.getLectureEdit());
	    
	    return lectureDto;
	}

<<<<<<< HEAD
	public Lecture findByLectureID(Long lectureId) {
		Optional<Lecture> lecutre = lectureRepository.findById(lectureId);
		
		if (lecutre.isPresent()) {
			return lecutre.get();
			} else {
			throw new NoSuchElementException("Lecture Not Found!"); 
			}
	}
=======
	
	 public CreateLectureRequestDto getLectureDetailsMapping(Long lectureId) {
	        Lecture lecture = lectureRepository.findById(lectureId)
	                .orElseThrow(() -> new NotFoundException());

	        CreateLectureRequestDto detailsDto = new CreateLectureRequestDto();
	        detailsDto.setLectureName(lecture.getLectureName());
	        detailsDto.setLectureIntro(lecture.getLectureIntro());
	        detailsDto.setLecturePrice(lecture.getLecturePrice());
	        detailsDto.setLectureStart(lecture.getLectureStart());
	        detailsDto.setLectureEdit(lecture.getLectureEdit());
	        detailsDto.setLectureThumbnail(lecture.getLectureThumbnail());

	        // 강의 카테고리 정보 매핑
	        LectureCategory lectureCategory = lecture.getLectureCategory();
	        LecturecategoryDto categoryDto = new LecturecategoryDto();
	        categoryDto.setCategoryNo(lectureCategory.getCategoryNo());
	        categoryDto.setLectureBigCategory(lectureCategory.getLectureBigCategory());
	        categoryDto.setLectureMidCategory(lectureCategory.getLectureMidCategory());
	        detailsDto.setLectureCategory(categoryDto);

	        // 강의 섹션 정보 매핑
	        List<LectureSection> lectureSections = sectionRepository.findByLectureSection(lecture);
	        List<LectureSectionDto> sectionDtos = new ArrayList<>();
	        for (LectureSection lectureSection : lectureSections) {
	            LectureSectionDto sectionDto = new LectureSectionDto();
	            sectionDto.setSectionNumber(lectureSection.getSectionNumber());
	            sectionDto.setSectionTitle(lectureSection.getSectionTitle());

	            // 섹션에 속한 비디오 정보 매핑
	            List<Video> videos = videoRepository.findByLectureSection(lectureSection);
	            List<VideoDto> videoDtos = new ArrayList<>();
	            for (Video video : videos) {
	                VideoDto videoDto = new VideoDto();
	                videoDto.setFileName(video.getFileName());
	                videoDto.setVideoId(video.getVideoId());
	                videoDto.setVideoLink(video.getVideoLink());
	                videoDto.setVideoTitle(video.getVideoTitle());
	                videoDtos.add(videoDto);
	            }
	            sectionDto.setVideoDtos(videoDtos);
	            sectionDtos.add(sectionDto);
	        }
	        detailsDto.setLectureSectionDtos(sectionDtos);

	        return detailsDto;
	    }
//	 public CreateLectureRequestDto getLectureDetailsMapping(Long lectureId) {
//	        Lecture lecture = lectureRepository.findById(lectureId)
//	                .orElseThrow(() -> new NotFoundException());
//
//	        CreateLectureRequestDto detailsDto = new CreateLectureRequestDto();
//	        detailsDto.setLectureName(lecture.getLectureName());
//	        detailsDto.setLectureIntro(lecture.getLectureIntro());
//	        detailsDto.setLecturePrice(lecture.getLecturePrice());
//	        detailsDto.setLectureStart(lecture.getLectureStart());
//	        detailsDto.setLectureEdit(lecture.getLectureEdit());
//	        detailsDto.setLectureThumbnail(lecture.getLectureThumbnail());
//
//	        // 강의 카테고리 정보 매핑
//	        LectureCategory lectureCategory = lecture.getLectureCategory();
//	        LecturecategoryDto categoryDto = new LecturecategoryDto();
//	        categoryDto.setCategoryNo(lectureCategory.getCategoryNo());
//	        categoryDto.setLectureBigCategory(lectureCategory.getLectureBigCategory());
//	        categoryDto.setLectureMidCategory(lectureCategory.getLectureMidCategory());
//	        detailsDto.setLectureCategory(categoryDto);
//
//	        // 강의 섹션 정보 매핑
//	        List<LectureSection> lectureSections = lectureSectionRepository.findByLecture(lecture);
//	        List<LectureSectionDto> sectionDtos = new ArrayList<>();
//	        for (LectureSection lectureSection : lectureSections) {
//	            LectureSectionDto sectionDto = new LectureSectionDto();
//	            sectionDto.setSectionNumber(lectureSection.getSectionNumber());
//	            sectionDto.setSectionTitle(lectureSection.getSectionTitle());
//
//	            // 섹션에 속한 비디오 정보 매핑
//	            List<Video> videos = videoRepository.findByLectureSection(lectureSection);
//	            List<VideoDto> videoDtos = new ArrayList<>();
//	            for (Video video : videos) {
//	                VideoDto videoDto = new VideoDto();
//	                videoDto.setUploadDate(video.getUploadDate());
//	                videoDto.setFileName(video.getFileName());
//	                videoDto.setVideoId(video.getVideoId());
//	                videoDto.setTotalPlayTime(video.getTotalPlayTime());
//	                videoDto.setVideoLink(video.getVideoLink());
//	                videoDto.setVideoTitle(video.getVideoTitle());
//	                videoDtos.add(videoDto);
//	            }
//	            sectionDto.setVideoDtos(videoDtos);
//	            sectionDtos.add(sectionDto);
//	        }
//	        detailsDto.setLectureSectionDtos(sectionDtos);
//
//	        return detailsDto;
//	    }

>>>>>>> refs/heads/merge
	
}
