package com.devrun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.devrun.dto.CertificateDto;
import com.devrun.dto.CommentDTO;
import com.devrun.dto.CommentDTO.Status;
import com.devrun.dto.MyLectureNoteDTO;
import com.devrun.dto.MyLectureNoteDTO2;
import com.devrun.dto.MycouresDTO;
import com.devrun.dto.MylectureDTO;
import com.devrun.dto.MylectureDTO2;
import com.devrun.dto.NoteRequest;
import com.devrun.dto.NoteUpdateRequest;
import com.devrun.dto.QaCommentQuest;
import com.devrun.dto.QaCommentUpdateDto;
import com.devrun.dto.QaDetailDTO;
import com.devrun.dto.QaListDTO;
import com.devrun.dto.QaListDTOs;
import com.devrun.dto.QaRequest;
import com.devrun.dto.QaUpdateRequest;
import com.devrun.dto.SectionInfo;
import com.devrun.dto.VideoInfo;
import com.devrun.dto.lectureNoteDetailDTO;
import com.devrun.dto.lectureNoteListDTO;
import com.devrun.dto.lectureNoteListDTO2;
import com.devrun.entity.MylectureQa.removed;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.entity.MyLectureProgress;
import com.devrun.entity.MylectureNote;
import com.devrun.entity.MylectureQa;
import com.devrun.entity.MylectureQaAnswer;
import com.devrun.exception.CommonErrorCode;
import com.devrun.exception.RestApiException;
import com.devrun.exception.UserErrorCode;
import com.devrun.repository.MylectureNoteRepository;
import com.devrun.repository.MylectureProgressRepository;
import com.devrun.repository.MylectureQaAnswerRepository;
import com.devrun.repository.MylectureQaRepository;
import com.devrun.repository.MylectureRepository;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureSection;
import com.devrun.youtube.LectureService;
import com.devrun.youtube.Video;
import com.devrun.youtube.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyLectureService {

	private final MylectureRepository mylectureRepository;
	private final MylectureProgressRepository mylectureProgressRepository;
	private final LectureService lectureService;
	private final VideoRepository videoRepository;
	private final MylectureNoteRepository mylectureNoteRepository;
	private final MylectureQaRepository mylectureQaRepository;
	private final MylectureQaAnswerRepository mylectureQaAnswerRepository;
	/**
	 * 유저의 수강내역 데이터베이스에 해당 강의를 등록합니다.
	 * @param userEntity
	 * @param lecture
	 */
	public void registLecture(MemberEntity userEntity, Lecture lecture) {
		MyLecture myLecture = new MyLecture();
		myLecture.setMemberentity(userEntity);
		myLecture.setLecture(lecture);
		myLecture.setLectureExpiryDate(null);
		mylectureRepository.save(myLecture);
		registVideo(myLecture);
	}
	
	/**
	 * 유저의 진도율 데이터베이스에 해당 강의의 영상들을 등록합니다.
	 * @param mylecture
	 */
	private void registVideo(MyLecture mylecture) {
		List<Video> videoList = videoRepository.findByLecture(mylecture.getLecture());
		List<MyLectureProgress> saveList = new ArrayList<MyLectureProgress>();
		for (Video v : videoList) {
			MyLectureProgress mylectureprogress = new MyLectureProgress();
			mylectureprogress.setMyLecture(mylecture);
			mylectureprogress.setLastviewdate(null);
			mylectureprogress.setVideo(v);
			saveList.add(mylectureprogress);
		}
		mylectureProgressRepository.saveAll(saveList);
	}

	/**
	 * 유저의 수강 목록 데이터베이스에서 해당 강의를 제거합니다.
	 * @param userEntity
	 * @param lectureId
	 */
	public void refundLecture(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		mylectureRepository.delete(myLecture);

	}
	
	/**
	 * 해당 유저가 수강중인 강의 중 한개의 강의에 대한 세부 정보를 반환합니다.
	 * @param userEntity
	 * @param lectureId
	 * @return MycouresDTO
	 */
	public MycouresDTO findMycoures(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);

		return convertMyLectureToMycouresDTO(myLecture);
	}
	
	/**
	 * 진도율 데이터베이스에 현재의 진도율을 갱신합니다.
	 * @param userEntity
	 * @param videoid
	 * @param currenttime
	 * @return
	 */
	public Map<String, Object> progress(MemberEntity userEntity, String videoid, int currenttime) {
		Video videoentity = videoRepository.findByVideoId(videoid);
		MyLecture mylecture = verifyUserHasLecture(userEntity, videoentity.getLecture());
		List<MyLectureProgress> mylectureProgressEntity = mylectureProgressRepository.findByMyLecture(mylecture);
		int wholePlayTime = 0;
		int wholeVideoTime = 0;
		for (MyLectureProgress myLectureProgress : mylectureProgressEntity) {
			if (myLectureProgress.getVideo().getVideoId().equals(videoid) &&
					myLectureProgress.getTimecheck() <= currenttime) {
				int totalplaytime = myLectureProgress.getVideo().getTotalPlayTime();
				if (currenttime >= totalplaytime - 10) {
					currenttime = totalplaytime;
				}
				int progressInt = (int) ((double) currenttime / (double) totalplaytime * 100);
				myLectureProgress.setProgress(progressInt);
				myLectureProgress.setTimecheck(currenttime);
				mylectureProgressRepository.save(myLectureProgress);
			}
			wholePlayTime += myLectureProgress.getTimecheck();
			wholeVideoTime += myLectureProgress.getVideo().getTotalPlayTime();

		}
		int lectureProgress = (int) ((double) wholePlayTime / (double) wholeVideoTime * 100);
		if(mylecture.getLectureProgress() != 100) {
			mylecture.setLectureProgress(lectureProgress);
			mylectureRepository.save(mylecture);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user name", userEntity.getName());
		map.put("video id", videoid);
		map.put("lectureProgress", lectureProgress);
		map.put("status", "ok");

		return map;

	}
	
	/**
	 * 해당 유저가 선택한 강의를 수강하고 있는 지 검증합니다.
	 * @param userEntity
	 * @param lecture
	 * @return MyLecture
	 */
	public MyLecture verifyUserHasLecture(MemberEntity userEntity, Lecture lecture) {
		MyLecture myLecnture = mylectureRepository.findByMemberentityAndLecture(userEntity, lecture).orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
		return myLecnture;
	}	

	/**
	 * 하나의 강의에 대한 진도율 데이터(총진도율과 전체 시청 시간)를 계산하고, 해당 강의의 섹션 정보, 영상 정보를 반환합니다. 
	 * @param myLecture
	 * @return MycouresDTO
	 */
	private MycouresDTO convertMyLectureToMycouresDTO(MyLecture myLecture) {
		List<MyLectureProgress> myCouresList = mylectureProgressRepository.findByMyLecture(myLecture);
		int wholeStudyTime = 0;
		int wholeLectureTime = 0;
		for (MyLectureProgress myprogress : myCouresList) {
			wholeLectureTime += myprogress.getVideo().getTotalPlayTime();
			wholeStudyTime += myprogress.getTimecheck();

		}

		List<MycouresDTO> mycouresList = new ArrayList<MycouresDTO>();
		MycouresDTO mycouresDTO = new MycouresDTO(myLecture);

		List<SectionInfo> sectionInfolist = new ArrayList<SectionInfo>();
		for (LectureSection section : myLecture.getLecture().getLectureSections()) {
			SectionInfo sectioninfo = new SectionInfo(section);
			List<VideoInfo> videoInfolist = new ArrayList<VideoInfo>();
			for (MyLectureProgress myprogress : myCouresList) {
				if (myprogress.getVideo().getLectureSection().equals(section)) {
					VideoInfo videoinfo = new VideoInfo(myprogress);
					videoInfolist.add(videoinfo);
				}
			}
			sectioninfo.setVideoInfo(videoInfolist);
			sectionInfolist.add(sectioninfo);
		}
		mycouresDTO.setSectionInfo(sectionInfolist);
		mycouresDTO.setWholeStudyTime(wholeStudyTime);
		mycouresDTO.setWholeRemainingTime(wholeLectureTime);
		mycouresList.add(mycouresDTO);

		return mycouresList.get(0);

	}
	
	/**
	 * 유저가 수강 중인 강의의 목록을 반환합니다.
	 * @param userEntity
	 * @param page
	 * @param option
	 * @return MylectureDTO2
	 */
	public MylectureDTO2 mylecturelist(MemberEntity userEntity, int page, String option) {
		page = page <= 1 ? 0 : page - 1;
		PageRequest pageRequest = PageRequest.of(page, 9, Direction.DESC, "lastviewdate");
		if (option.equals("Completed")) {
			Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentityAndLectureProgressEquals(userEntity,
					100, pageRequest);
			return convertMylectureDTO(pageMylecture);
		} else if (option.equals("Inprogress")) {
			Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentityAndLectureProgressLessThan(userEntity,
					100, pageRequest);
			return convertMylectureDTO(pageMylecture);
		} else {
			Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentity(userEntity, pageRequest);
			return convertMylectureDTO(pageMylecture);
		}
	}
	/**
	 * 수강 중인 강의 목록의 페이지네이션을 데이터를 추가합니다.
	 * @param pageMylecture
	 * @return
	 */
	private MylectureDTO2 convertMylectureDTO(Page<MyLecture> pageMylecture) {
		List<MylectureDTO> mylectureDTOList = new ArrayList<MylectureDTO>();
		for (MyLecture mylecture : pageMylecture) {
			MylectureDTO dto = new MylectureDTO(mylecture);
			mylectureDTOList.add(dto);
		}

		MylectureDTO2 dtolist = new MylectureDTO2(mylectureDTOList, pageMylecture.getTotalPages());

		return dtolist;
	}
	/**
	 * 강의 시청 중 작성한 노트와 페이지네이션 데이터를 추가합니다.
	 * @param myLectureList
	 * @param TotalPages
	 * @return
	 */
	private MyLectureNoteDTO2 packageDto(Page<MyLecture> myLectureList, int TotalPages) {
		List<MyLectureProgress> myprogressList = mylectureProgressRepository.findByMyLectureIn(myLectureList.toList());
		List<MylectureNote> myNoteList = mylectureNoteRepository
				.findByMyLectureProgressInAndNoteDeleteFalseOrderByDateDesc(myprogressList);

		List<MyLectureNoteDTO> lectureNoteDTOs = new ArrayList<MyLectureNoteDTO>();

		for (MyLecture my : myLectureList.toList()) {
			MyLectureNoteDTO myLectureNoteDto = new MyLectureNoteDTO();
			myLectureNoteDto.setLastStudyDate(my.getLastviewdate());
			myLectureNoteDto.setLectureId(my.getLecture().getLectureid());
			myLectureNoteDto.setLectureThumbnail(my.getLecture().getLectureThumbnail());
			myLectureNoteDto.setLectureTitle(my.getLecture().getLectureName());
			for (MylectureNote ml : myNoteList) {
				if (ml.getMyLectureProgress().getMyLecture().equals(my)) {
					myLectureNoteDto.setCount(myLectureNoteDto.getCount() + 1);
				}
			}
			lectureNoteDTOs.add(myLectureNoteDto);
		}

		MyLectureNoteDTO2 dtos = new MyLectureNoteDTO2();
		dtos.setDtolist(lectureNoteDTOs);
		dtos.setTotalPages(TotalPages);
		return dtos;

	}
	/**
	 * 강의 시청 중 작성한 모든 노트의 집계 데이터를 반환합니다.
	 * @param userEntity
	 * @param page
	 * @return
	 */
	public MyLectureNoteDTO2 myNotelist(MemberEntity userEntity, int page) {
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "lastviewdate");
		Page<MyLecture> myLectureList = mylectureRepository.findByMemberentity(userEntity, pageRequest);

		return packageDto(myLectureList, myLectureList.getTotalPages());
	}
	
	/**
	 * 선택한 강의에서 작성한 노트 데이터의 목록과 미리보기 데이터 , 페이지네이션 데이터를 반환합니다.
	 * @param userEntity
	 * @param lectureId
	 * @param page
	 * @return lectureNoteListDTO2
	 */
	public lectureNoteListDTO2 noteDetaiList(MemberEntity userEntity, Long lectureId, int page) {
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "date");
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		List<MyLectureProgress> myprogressList = mylectureProgressRepository.findByMyLecture(myLecture);
		List<lectureNoteListDTO> noteDetilas = new ArrayList<lectureNoteListDTO>();
		Optional<Page<MylectureNote>> myNoteList = mylectureNoteRepository
				.findByMyLectureProgressInAndNoteDeleteFalse(myprogressList, pageRequest);
		if (myNoteList.isPresent()) {
		} else {
			throw new NoSuchElementException("There are no note");
		}
		int cutsize = 100;
		for (MylectureNote my : myNoteList.get()) {
			lectureNoteListDTO noteDetail = new lectureNoteListDTO();
			noteDetail.setChapter(my.getChapter());
			String ContentPreview = my.getNoteContext().replaceAll("<[^>]*>", "");
			if (cutsize < ContentPreview.length()) {
				ContentPreview = ContentPreview.substring(0, cutsize);
			}
			noteDetail.setContentPreview(ContentPreview);
			noteDetail.setDate(my.getDate());
			noteDetail.setNoteTitle(my.getNoteTitle());
			noteDetail.setSubHeading(my.getSubheading());
			noteDetail.setNoteId(my.getNoteNo());
			noteDetilas.add(noteDetail);
		}
		lectureNoteListDTO2 list = new lectureNoteListDTO2();
		list.setDtolist(noteDetilas);
		list.setTotalPages(myNoteList.get().getTotalPages());
		return list;
	}
	
	/**
	 * 선택한 노트의 세부 데이터를 가져옵니다.
	 * @param userEntity
	 * @param noteNo
	 * @return lectureNoteDetailDTO
	 */
	public lectureNoteDetailDTO noteDetaiOpen(MemberEntity userEntity, Long noteNo) {
		MylectureNote mylectureNote = mylectureNoteRepository.findByNoteNo(noteNo);
		lectureNoteDetailDTO dto = new lectureNoteDetailDTO();
		dto.setChapter(mylectureNote.getChapter());
		dto.setContent(mylectureNote.getNoteContext());
		dto.setDate(mylectureNote.getDate());
		dto.setNoteId(mylectureNote.getNoteNo());
		dto.setNoteTitle(mylectureNote.getNoteTitle());
		dto.setSubHeading(mylectureNote.getSubheading());
		dto.setVideoId(mylectureNote.getMyLectureProgress().getVideo().getVideoId());
		return dto;
	}

	public void myNoteSave(MemberEntity userEntity, NoteRequest noteRequest) {
		Video video = videoRepository.findByVideoId(noteRequest.getVideoId());
		MyLecture myLecture = verifyUserHasLecture(userEntity, video.getLecture());
		MyLectureProgress progress = mylectureProgressRepository.findByMyLectureAndVideo(myLecture, video);
		MylectureNote mylectureNote = new MylectureNote();
		mylectureNote.setMyLectureProgress(progress);
		mylectureNote.setNoteContext(noteRequest.getNoteContent());
		mylectureNote.setNoteTitle(noteRequest.getNoteTitle());
		mylectureNote.setChapter(video.getLectureSection().getSectionNumber());
		mylectureNote.setSubheading(video.getLectureSection().getSectionTitle());
		mylectureNoteRepository.save(mylectureNote);

	}

	public lectureNoteDetailDTO myNoteUpdate(MemberEntity userEntity, NoteUpdateRequest noteUpdateRequest) {
		MylectureNote mylectureNote = mylectureNoteRepository.findByNoteNo(noteUpdateRequest.getNoteNo());
		verifyUserHasLecture(userEntity, mylectureNote.getMyLectureProgress().getMyLecture().getLecture());
		mylectureNote.setNoteContext(noteUpdateRequest.getNoteContent());
		mylectureNote.setNoteTitle(noteUpdateRequest.getNoteTitle());
		mylectureNoteRepository.save(mylectureNote);

		lectureNoteDetailDTO noteDetail = new lectureNoteDetailDTO();
		noteDetail.setChapter(mylectureNote.getChapter());
		noteDetail.setContent(mylectureNote.getNoteContext());
		noteDetail.setDate(mylectureNote.getDate());
		noteDetail.setNoteTitle(mylectureNote.getNoteTitle());
		noteDetail.setSubHeading(mylectureNote.getSubheading());
		noteDetail.setNoteId(mylectureNote.getNoteNo());
		return noteDetail;
	}

	public void myNoteDelete(MemberEntity userEntity, Long noteNo) {
		MylectureNote mylectureNote = mylectureNoteRepository.findByNoteNo(noteNo);
		mylectureNote.setNoteDelete(true);
		mylectureNoteRepository.save(mylectureNote);
	}
	/**
	 * 질문 게시물을 등록합니다.
	 * @param userEntity
	 * @param qaRequest
	 * @return
	 */
	public QaDetailDTO QaSave(MemberEntity userEntity, QaRequest qaRequest) {
		Lecture lecture = lectureService.findByLectureID(qaRequest.getLectureId());
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		Video video = videoRepository.findByVideoId(qaRequest.getVideoId());
		MylectureQa mylectureQa = new MylectureQa();
		mylectureQa.setUserNo(userEntity);
		mylectureQa.setLectureId(myLecture.getLecture());
		mylectureQa.setQuestionContent(qaRequest.getQuestionContent());
		mylectureQa.setQuestionTitle(qaRequest.getQuestionTitle());
		mylectureQa.setVideoId(video);
		mylectureQaRepository.save(mylectureQa);
		return QaDetailDTOBuilder(mylectureQa);
	}

	public MylectureDTO2 checkLectureComplete(MemberEntity userEntity, int page) {
		page = page <= 1 ? 0 : page - 1;
		PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.DESC, "lastviewdate");
		Page<MyLecture> mylecture = mylectureRepository.findByMemberentityAndLectureProgressEquals(userEntity, 100,
				pageRequest);
		return convertMylectureDTO(mylecture);
	}

	public CertificateDto printCertificates(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture my = verifyUserHasLecture(userEntity, lecture);
		if (my.getLectureProgress() == 100) {
			CertificateDto dto = new CertificateDto();
			dto.setBirthday(userEntity.getBirthday());
			dto.setLectureName(lecture.getLectureName());
			dto.setStart(my.getLectureStartDate());
			dto.setEnd(my.getLastviewdate());
			dto.setUserName(userEntity.getName());
			return dto;
		} else {
			throw new RestApiException(UserErrorCode.NOT_QUALIFIED);
		}

	}

	public String lastvideo(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		verifyUserHasLecture(userEntity, lecture);
		Optional<MyLecture> my = mylectureRepository.findByMemberentityAndLecture(userEntity, lecture);
		if (my.isPresent()) {
			PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("lastviewdate").descending().and(Sort.by("video").ascending()));
			Page<MyLectureProgress> one = mylectureProgressRepository.findByMyLecture(my.get(), pageRequest);
			return one.getContent().get(0).getVideo().getVideoId();
		} else {
			throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
		}
	}
	/**
	 * 해당 강의에 대한 질문게시글 목록을 반환합니다.
	 * @param lectureId
	 * @param page
	 * @return
	 */
	public QaListDTOs QalistBylecture(Long lectureId, int page) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.DESC, "questionDate");
		Page<MylectureQa> myqs = mylectureQaRepository.findByDeleteopAndLectureId(removed.DISABLE ,lecture, pageRequest);
		return QaListDTOsBuilder(myqs);
	}
	/**
	 * 특정 유저의 질문 목록을 반환합니다.
	 * @param userEntity
	 * @param page
	 * @param sort
	 * @return
	 */
	public QaListDTOs QalistByMemberHandler(MemberEntity userEntity, int page, String sort) {
		PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.DESC, "questionDate");

		if (sort.equals("completed")) {
			Page<MylectureQa> qas = mylectureQaRepository.findByDeleteopAndUserNoAndCountGreaterThan(removed.DISABLE,
					userEntity, 0, pageRequest);
			return QaListDTOsBuilder(qas);
		} else if (sort.equals("waiting")) {
			Page<MylectureQa> qas = mylectureQaRepository.findByDeleteopAndUserNoAndCountIs(removed.DISABLE, userEntity,
					0, pageRequest);
			return QaListDTOsBuilder(qas);
		} else {
			Page<MylectureQa> qas = mylectureQaRepository.findByDeleteopAndUserNo(removed.DISABLE, userEntity,
					pageRequest);
			return QaListDTOsBuilder(qas);
		}

	}
	/**
	 * 해당 유저의 질문을 검색하여 반환합니다.
	 * @param userEntity
	 * @param page
	 * @param sort
	 * @param keyword
	 * @return
	 */
	public QaListDTOs QalistBySearch(MemberEntity userEntity, int page, String sort, String keyword) {
		page = page <= 1 ? 0 : page - 1;
		PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.DESC, "questionDate");

		if (sort.equals("completed")) {
			Page<MylectureQa> qas = mylectureQaRepository
					.findByDeleteopAndUserNoAndCountGreaterThanAndQuestionTitleContainingOrUserNoAndCountGreaterThanAndQuestionContentContaining(
							removed.DISABLE, userEntity, 0, keyword, userEntity, 0, keyword, pageRequest);
			return QaListDTOsBuilder(qas);
		} else if (sort.equals("waiting")) {
			Page<MylectureQa> qas = mylectureQaRepository
					.findByDeleteopAndUserNoAndCountIsAndQuestionTitleContainingOrUserNoAndCountIsAndQuestionContentContaining(
							removed.DISABLE, userEntity, 0, keyword, userEntity, 0, keyword, pageRequest);
			return QaListDTOsBuilder(qas);
		} else {
			Page<MylectureQa> qas = mylectureQaRepository
					.findByDeleteopAndUserNoAndQuestionTitleContainingOrUserNoAndQuestionContentContaining(
							removed.DISABLE, userEntity, keyword, 0, userEntity, keyword, pageRequest);
			return QaListDTOsBuilder(qas);
		}

	}
	/**
	 * 해당 강의의 질문 데이터와 페이지네이션 데이터를 반환합니다.
	 * @param qas
	 * @return QaListDTOs
	 */
	private QaListDTOs QaListDTOsBuilder(Page<MylectureQa> qas) {
		int cutsize = 100;
		List<QaListDTO> qldtolist = new ArrayList<QaListDTO>();
		
		for (MylectureQa q : qas) {
			QaListDTO qldto = new QaListDTO();
			Long count = mylectureQaAnswerRepository.countByQaNoAndStatus(q, Status.ACTIVE);
			qldto.setAnswer(count.intValue());
			qldto.setQuestionId(q.getLectureQaNo());
			qldto.setQuestionLectureTitle(q.getLectureId().getLectureName());
			qldto.setQuestionTitle(q.getQuestionTitle());
			qldto.setQuestionDate(q.getQuestionDate());
			qldto.setStudentId(q.getUserNo().getId());
			String Preview = q.getQuestionContent().replaceAll("<[^>]*>", "");
			if (cutsize < Preview.length()) {
				Preview = Preview.substring(0, cutsize);
			}
			qldto.setQuestionContentPreview(Preview);
			qldtolist.add(qldto);
		}

		QaListDTOs list = new QaListDTOs();
		list.setDtolist(qldtolist);
		list.setTotalPages(qas.getTotalPages());
		list.setQuestionCount(qas.getTotalElements());
		return list;
	}
	/**
	 * 질문 게시글의 세부내용을 반환합니다.
	 * @param questionId
	 * @return
	 */
	public QaDetailDTO getQaDetail(Long questionId) {
		MylectureQa qaList = mylectureQaRepository.findByDeleteopAndLectureQaNo(removed.DISABLE,questionId);

		return QaDetailDTOBuilder(qaList);

	}
	
    // 댓글 목록 가져오기 (Status가 ACTIVE인 것만)
    public List<MylectureQaAnswer> getActiveCommentsByNotice(Long questionId) {
    	MylectureQa qa = mylectureQaRepository.findByDeleteopAndLectureQaNo(removed.DISABLE,questionId);
    	List<MylectureQaAnswer> qal = mylectureQaAnswerRepository.findByQaNoAndStatus(qa, Status.ACTIVE);
    	
        return qal; 
        
    }
    /**
     * 질문 데이터를 View에서 사용할 수 있도록 가공합니다.
     * @param q
     * @return QaDetailDTO
     */
	private QaDetailDTO QaDetailDTOBuilder(MylectureQa q) {
		QaDetailDTO qaDto = new QaDetailDTO();
		qaDto.setQuestionId(q.getLectureQaNo());
		qaDto.setLectureTitle(q.getLectureId().getLectureName());
		qaDto.setLectureId(q.getLectureId().getLectureid());
		qaDto.setStudentId(q.getUserNo().getId());
		qaDto.setQuestionTitle(q.getQuestionTitle());
		qaDto.setContent(q.getQuestionContent());
		qaDto.setVideoId(q.getVideoId().getVideoId());
		qaDto.setDate(q.getQuestionDate());
		return qaDto;
	}
	/**
	 * 작성한 질문을 갱신합니다.
	 * @param userEntity
	 * @param qaRequest
	 * @return
	 */
	public QaDetailDTO QaUpdate(MemberEntity userEntity, QaUpdateRequest qaRequest) {
		MylectureQa q = QaCRUDConfirm(qaRequest.getQuestionId());
		if (q.getUserNo().equals(userEntity)) {
			q.setQuestionContent(qaRequest.getQuestionContent());
			q.setQuestionTitle(qaRequest.getQuestionTitle());
			MylectureQa savedq = mylectureQaRepository.save(q);
			return QaDetailDTOBuilder(savedq);
		} else {
			throw new RestApiException(UserErrorCode.POSSESSION);
		}
	}
	/**
	 * 해당 질문을 삭제합니다.
	 * @param userEntity
	 * @param lectureQaNo
	 * @return
	 */
	public String QaDelete(MemberEntity userEntity, Long lectureQaNo) {
		MylectureQa q = QaCRUDConfirm(lectureQaNo);
		if (q.getUserNo().equals(userEntity)) {
			q.setDeleteop(removed.ENABLE);
			mylectureQaRepository.save(q);
			return "delete complete";
		} else {
			throw new RestApiException(UserErrorCode.POSSESSION);
		}
	}

	/**
	 * 댓글을 작성합니다.
	 * @param userEntity
	 * @param commentQuest
	 * @return
	 */
	public CommentDTO writeComment(MemberEntity userEntity, QaCommentQuest commentQuest) {
		MylectureQa q = QaCRUDConfirm(commentQuest.getQuestionId());
		MylectureQaAnswer entity = new MylectureQaAnswer();
		entity.setQaNo(q);
		entity.setMemberEntity(userEntity);
		entity.setContent(commentQuest.getContent());
		//entity.setParentComment(entity);
		mylectureQaAnswerRepository.save(entity);
		return commentDTOpackger(entity);
	}

	public CommentDTO commentDTOpackger(MylectureQaAnswer entity) {
		CommentDTO commentDTO = entity.toDTO();
		return commentDTO;
	}

	/**
	 * 해당 질문이 실제 존재하는 지 검증합니다.
	 * @param lectureQaNo
	 * @return
	 */
	private MylectureQa QaCRUDConfirm(Long lectureQaNo) {
		Optional<MylectureQa> q = mylectureQaRepository.findById(lectureQaNo);
		if (q.isPresent()) {
			return q.get();
		} else {
			throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
		}
	}
	
	public MylectureQaAnswer QaCommentConfirm(int commentId) {
		Optional<MylectureQaAnswer> comment = mylectureQaAnswerRepository.findById(commentId);
		if (comment.isPresent())
			return comment.get();
		else {
			throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
		}
	}
	/**
	 * 작성한 댓글을 삭제합니다.
	 * @param userEntity
	 * @param commentId
	 * @return
	 */
	public String QaCommentDelete(MemberEntity userEntity, int commentId) {
		MylectureQaAnswer comment = QaCommentConfirm(commentId);
		if (comment.getMemberEntity().equals(userEntity)) {
			comment.setStatus(Status.INACTIVE);
			mylectureQaAnswerRepository.save(comment);
			return "delete complete";
		} else {
			throw new RestApiException(UserErrorCode.POSSESSION);
		}
	}
	/**
	 * 작성한 댓글을 수정합니다.
	 * @param userEntity
	 * @param commentId
	 * @param dto
	 * @return
	 */
	public CommentDTO QaCommentUpdate(MemberEntity userEntity, int commentId, QaCommentUpdateDto dto) {
		MylectureQaAnswer comment = QaCommentConfirm(commentId);
		if (comment.getMemberEntity().equals(userEntity)) {
			comment.setContent(dto.getContent());
			mylectureQaAnswerRepository.save(comment);
			return commentDTOpackger(comment);
		} else {
			throw new RestApiException(UserErrorCode.POSSESSION);
		}
	}
}
