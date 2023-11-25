package com.devrun.youtube;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.print.attribute.HashAttributeSet;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devrun.dto.LectureIntroduceDTO;
import com.devrun.dto.QueryLectureByKeywordDTO;
import com.devrun.dto.QueryLectureByKeywordDTO2;
import com.devrun.entity.LectureIntroduce;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.exception.CommonErrorCode;
import com.devrun.exception.RestApiException;
import com.devrun.exception.UserErrorCode;
import com.devrun.repository.LectureIntroduceRepository;
import com.devrun.repository.MylectureRepository;
import com.devrun.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureService {
	private final MylectureRepository mylectureRepository;
	private final LectureRepository lectureRepository;
	private final LectureSectionRepository sectionRepository;
	private final LecturecategoryRepository categoryRepository;
	private final VideoRepository videoRepository;
	private final LecutureCategoryService categoryService;
	private final LectureIntroduceRepository introduceRepository;
	private final MemberService memberService;

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

	public Lecture saveLecture(MemberEntity mento, CreateLectureRequestDto requestDto, String lectureThumnailUrl) {

		Lecture lecture = new Lecture();
		lecture.setMentoId(mento);
		lecture.setLectureCategory(requestDto.getLectureCategory());
		lecture.setLectureIntro(requestDto.getLectureIntro());
		lecture.setLectureName(requestDto.getLectureName());
		lecture.setLecturePrice(requestDto.getLecturePrice());
		lecture.setLectureTag(requestDto.getLectureTag());
		lecture.setLectureStatus("ACTIVE");
		lecture.setLectureRating(0);
		lecture.setLectureThumbnail(lectureThumnailUrl);
		Lecture savedlecture = lectureRepository.save(lecture);
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

	public void saveVideo(Lecture savedlecture, LectureSection savedlectureSeciton, VideoDto videoDto) {
		Video savevideo = new Video();
		savevideo.setLecture(savedlecture);
		savevideo.setLectureSection(savedlectureSeciton);
		savevideo.setVideoTitle(videoDto.getVideoTitle());
		savevideo.setVideoLink(videoDto.getVideoLink());
		savevideo.setTotalPlayTime(videoDto.getTotalPlayTime());
		videoRepository.save(savevideo);
	}

	/*
	 * 데이터베이스에 저장된 강의 entity 중 강의명, 강의 소갯글, 강사명 속성에 특정한 검색어를 포함하는 entity의 일부 속성(강의명
	 * , 강의 소개글, 강사명, 강의 평점, 강의 가격, 썸네일 URI , 카테고리 분류 중-소 , 속성) 집합을 가져옴
	 */
	public QueryLectureByKeywordDTO2 searchLectures(String bigcategory, String midcategory, String keyword,
			PageRequest pageRequest) {
		if (isCategoryEmpty(bigcategory, midcategory)) {
			return handleEmptyCategories(keyword, pageRequest);
		} else if (midcategory.isEmpty()) {
			return searchByBigCategory(bigcategory, keyword, pageRequest);
		} else {
			return searchByBigAndMidCategory(bigcategory, midcategory, keyword, pageRequest);
		}
	}

	private boolean isCategoryEmpty(String bigcategory, String midcategory) {
		return bigcategory.isEmpty() && midcategory.isEmpty();
	}

	private QueryLectureByKeywordDTO2 handleEmptyCategories(String keyword, PageRequest pageRequest) {
		if (keyword.isEmpty()) {
			return QueryLectureByKeyword(keyword, pageRequest);
		}
		List<MemberEntity> members = memberService.findByIdContains(keyword);
		if (members.isEmpty()) {
			return QueryLectureByKeyword(keyword, pageRequest);
		} else {
			return QueryLectureByKeyword(keyword, members, pageRequest);
		}
	}

	private QueryLectureByKeywordDTO2 searchByBigCategory(String bigcategory, String keyword, PageRequest pageRequest) {
		List<LectureCategory> categories = categoryService.findcategory(bigcategory);
		if (keyword.isBlank()) {
			return findLecturesWithCategroys(categories, keyword, pageRequest);
		}
		List<MemberEntity> members = memberService.findByIdContains(keyword);
		if (members.isEmpty()) {
			return findLecturesWithCategroys(categories, keyword, pageRequest);
		} else {
			return findLecturesWithCategroysWithUser(categories, keyword, members, pageRequest);
		}
	}

	private QueryLectureByKeywordDTO2 findLecturesWithCategroysWithUser(List<LectureCategory> categories,
			String keyword, List<MemberEntity> members, PageRequest pageRequest) {
		Page<Lecture> l1 = lectureRepository.findCategoryInAndKeywordIn(categories, keyword, members, pageRequest);
		return packageingDto(l1);
	}

	private QueryLectureByKeywordDTO2 searchByBigAndMidCategory(String bigcategory, String midcategory, String keyword,
			PageRequest pageRequest) {
		LectureCategory category = categoryService.findcategory(bigcategory, midcategory);
		if (keyword.isBlank()) {
			return findLecturesWithCategroy(category, keyword, pageRequest);
		}
		List<MemberEntity> members = memberService.findByIdContains(keyword);
		if (members.isEmpty()) {
			return findLecturesWithCategroy(category, keyword, pageRequest);
		} else {
			return findLecturesWithCategroysWithUser(category, keyword, members, pageRequest);
		}
	}

	private QueryLectureByKeywordDTO2 findLecturesWithCategroysWithUser(LectureCategory category, String keyword,
			List<MemberEntity> members, PageRequest pageRequest) {
		Page<Lecture> l1 = lectureRepository.findCategoryInAndKeywordIn(category, keyword, members, pageRequest);
		return packageingDto(l1);
	}

	// 카테고리 유무

	// 카테고리가 진짜 있는지?
	// 검색어 유무
	// 검색어가 사람인지?

	// 유저가 없는 경우
	public QueryLectureByKeywordDTO2 QueryLectureByKeyword(String keyword, PageRequest pageable) {
		Page<Lecture> l1 = lectureRepository.findByLectureNameContainsOrLectureIntroContains(keyword, pageable);
		return packageingDto(l1);
	};

	// 유저가 있는 경우
	public QueryLectureByKeywordDTO2 QueryLectureByKeyword(String keyword, List<MemberEntity> m1, Pageable pageable) {

		Page<Lecture> l1 = lectureRepository.findByLectureNameContainsOrLectureIntroContainsOrMentoIdIn(keyword, m1,
				pageable);
		return packageingDto(l1);
	}

	public QueryLectureByKeywordDTO2 findLecturesWithCategroys(List<LectureCategory> categorys, String keyword,
			PageRequest pageRequest) {
		Page<Lecture> l1 = lectureRepository.findLecturesWithCategroy(categorys, keyword, pageRequest);
		return packageingDto(l1);
	}

	public QueryLectureByKeywordDTO2 findLecturesWithCategroy(LectureCategory category, String keyword,
			PageRequest pageRequest) {
		Page<Lecture> l1 = lectureRepository.findLecturesWithCategroy(category, keyword, pageRequest);
		return packageingDto(l1);
	}

	public QueryLectureByKeywordDTO2 packageingDto(Page<Lecture> l1) {
		List<QueryLectureByKeywordDTO> list0 = convertLectureToDTO(l1);
		QueryLectureByKeywordDTO2 list = new QueryLectureByKeywordDTO2();
		list.setDtolist(list0);
		list.setTotalelements(l1.getTotalElements());
		list.setTotalpages(l1.getTotalPages());
		return list;
	}

	public List<QueryLectureByKeywordDTO> convertLectureToDTO(Page<Lecture> lectureList) {
		if(lectureList.isEmpty()) {throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);}
		return lectureList.stream().map(QueryLectureByKeywordDTO::new).collect(Collectors.toList());
	}

	public CreateLectureRequestDto getLectureDetails(Long lectureId) throws NotFoundException {
		Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new NotFoundException());

		CreateLectureRequestDto lectureDto = new CreateLectureRequestDto();
		lectureDto.setLectureName(lecture.getLectureName());
		lectureDto.setLectureIntro(lecture.getLectureIntro());
		lectureDto.setLecturePrice(lecture.getLecturePrice());
		lectureDto.setLectureStart(lecture.getLectureStart());
		lectureDto.setLectureEdit(lecture.getLectureEdit());

		return lectureDto;
	}

	public Lecture findByLectureID(Long lectureId) {
		Optional<Lecture> lecutre = lectureRepository.findByLectureid(lectureId);

		if (lecutre.isPresent()) {
			return lecutre.get();
		} else {
			throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
		}
	}

	public QueryLectureByKeywordDTO2 findAll(PageRequest pageRequest) {
		Page<Lecture> l1 = lectureRepository.findAll(pageRequest);
		return packageingDto(l1);
	}

	public LectureIntroduceDTO getlecturedetail(Long lectureId) {
		Lecture lecture = findByLectureID(lectureId);
		LectureIntroduce li = introduceRepository.findByLecture(lecture);
		LectureIntroduceDTO dto = new LectureIntroduceDTO();
		dto.setContent(li.getContent());
		dto.setLectureId(li.getLecture().getLectureid());
		return dto;
	}

	public QueryLectureByKeywordDTO2 checkAlreadyHasLecture(MemberEntity userEntity, QueryLectureByKeywordDTO2 p1) {
		List<MyLecture> userhas = mylectureRepository.findByMemberentity(userEntity);
		if(!userhas.isEmpty()) {
			List<Long> idlist = new ArrayList<Long>();
			for(MyLecture myLecture : userhas) {
				idlist.add(myLecture.getLecture().getLectureid());
			}
			userhas.clear();
			for( QueryLectureByKeywordDTO p : p1.getDtolist()) {
				if(idlist.contains(p.getLectureId())) {
					p.setPurchaseStatus(true);
				} else {
					continue;
				}
			}
			
			return p1;
		} else {
			return p1;
		}
	}

	public LectureIntroduceDTO getlecturedetailupdate(String userid, LectureIntroduceDTO request) {
		MemberEntity user = memberService.findById(userid);
		Lecture lecture = findByLectureID(request.getLectureId());
		Optional<MyLecture> check = mylectureRepository.findByMemberentityAndLecture(user, lecture);
		if(check.isPresent()) {
		LectureIntroduce li = introduceRepository.findByLecture(lecture);
		li.setContent(request.getContent());
		introduceRepository.save(li);
		LectureIntroduceDTO dto = new LectureIntroduceDTO();
		dto.setContent(li.getContent());
		dto.setLectureId(li.getLecture().getLectureid());
		return dto;
		} else {
			throw new RestApiException(UserErrorCode.USERHASNOTLECTURE);
		}
	}

	public void fullintrosave(Lecture savedlecture, String lectureFullIntro) {
		LectureIntroduce intro = new LectureIntroduce();
		intro.setLecture(savedlecture);
		intro.setContent(lectureFullIntro);
		introduceRepository.save(intro);
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
}
