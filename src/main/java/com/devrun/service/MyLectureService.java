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
import com.devrun.dto.MyLectureNoteDTO;
import com.devrun.dto.MyLectureNoteDTO2;
import com.devrun.dto.MycouresDTO;
import com.devrun.dto.MylectureDTO;
import com.devrun.dto.MylectureDTO2;
import com.devrun.dto.NoteRequest;
import com.devrun.dto.NoteUpdateRequest;
import com.devrun.dto.QaDTO;
import com.devrun.dto.QaRequest;
import com.devrun.dto.SectionInfo;
import com.devrun.dto.VideoInfo;
import com.devrun.dto.lectureNoteDetailDTO;
import com.devrun.dto.lectureNoteListDTO;
import com.devrun.dto.lectureNoteListDTO2;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.entity.MyLectureProgress;
import com.devrun.entity.MylectureNote;
import com.devrun.entity.MylectureQa;
import com.devrun.exception.RestApiException;
import com.devrun.exception.UserErrorCode;
import com.devrun.repository.MylectureNoteRepository;
import com.devrun.repository.MylectureProgressRepository;
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

	public void registLecture(MemberEntity userEntity, Lecture lecture) {
		MyLecture myLecture = new MyLecture();
		myLecture.setMemberentity(userEntity);
		myLecture.setLecture(lecture);
		myLecture.setLectureExpiryDate(null);
		mylectureRepository.save(myLecture);
		registVideo(myLecture);
	}

	public void registVideo(MyLecture mylecture) {
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

	public void refundLecture(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		mylectureRepository.delete(myLecture);

	}

	public MycouresDTO findMycoures(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);

		return convertMyLectureToMycouresDTO(myLecture);
	}

	public Map<String, Object> progress(MemberEntity userEntity, String videoid, int currenttime) {
		Video videoentity = videoRepository.findByVideoId(videoid);
		MyLecture mylecture = verifyUserHasLecture(userEntity, videoentity.getLecture());
		List<MyLectureProgress> mylectureProgressEntity = mylectureProgressRepository.findByMyLecture(mylecture);
		int wholePlayTime = 0;
		int wholeVideoTime = 0;
		for (MyLectureProgress myLectureProgress : mylectureProgressEntity) {
			if (myLectureProgress.getVideo().getVideoId().equals(videoid)) {
				int totalplaytime = myLectureProgress.getVideo().getTotalPlayTime();
				if (currenttime > totalplaytime) {
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
		mylecture.setLectureProgress(lectureProgress);
		mylectureRepository.save(mylecture);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user name", userEntity.getName());
		map.put("video id", videoid);
		map.put("lectureProgress", lectureProgress);
		map.put("status", "ok");

		return map;

	}

	public MyLecture verifyUserHasLecture(MemberEntity userEntity, Lecture lecture) {
		Optional<MyLecture> optional = mylectureRepository.findByMemberentityAndLecture(userEntity, lecture);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new RestApiException(UserErrorCode.USERHASNOTLECTURE);
		}
	}

	public MycouresDTO convertMyLectureToMycouresDTO(MyLecture myLecture) {
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

	public MylectureDTO2 mylecturelist(MemberEntity userEntity, int page, String option) {
		page = page <= 1 ? 0 : page;
		PageRequest pageRequest = PageRequest.of(page, 9, Direction.DESC, "lastviewdate");
		if(option.equals("Completed")) {
		Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentityAndLectureProgressEquals(userEntity, 100 ,pageRequest);
		return convertMylectureDTO(pageMylecture);
		} else if(option.equals("Inprogress")) {
		Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentityAndLectureProgressLessThan(userEntity, 100 ,pageRequest);
		return convertMylectureDTO(pageMylecture);
		} else {
		Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentity(userEntity,pageRequest);
		return convertMylectureDTO(pageMylecture);
		}
	}
	
	public MylectureDTO2 convertMylectureDTO(Page<MyLecture> pageMylecture){
		List<MylectureDTO> mylectureDTOList = new ArrayList<MylectureDTO>();
		for (MyLecture mylecture : pageMylecture) {
			MylectureDTO dto = new MylectureDTO(mylecture);
			mylectureDTOList.add(dto);
		}
		
		MylectureDTO2 dtolist = new MylectureDTO2(mylectureDTOList, pageMylecture.getTotalPages());
		
		
		return dtolist;
	}
	
	public MyLectureNoteDTO2 packageDto(Page<MyLecture> myLectureList , int TotalPages) {
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
	
	public MyLectureNoteDTO2 myNotelist(MemberEntity userEntity, int page) {
		page = page <= 1 ? 0 : page;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "lastviewdate");
		Page<MyLecture> myLectureList = mylectureRepository.findByMemberentity(userEntity, pageRequest);
		
		
		return packageDto(myLectureList , myLectureList.getTotalPages());
	}

	public lectureNoteListDTO2 noteDetaiList(MemberEntity userEntity, Long lectureId, int page) {
		page = page <= 1 ? 0 : page;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "date");
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		List<MyLectureProgress> myprogressList = mylectureProgressRepository.findByMyLecture(myLecture);
		List<lectureNoteListDTO> noteDetilas = new ArrayList<lectureNoteListDTO>();
		Optional<Page<MylectureNote>> myNoteList = mylectureNoteRepository.findByMyLectureProgressInAndNoteDeleteFalse(myprogressList, pageRequest);
		if (myNoteList.isPresent()) {
		} else {
			throw new NoSuchElementException("There are no note");
		}
		int cutsize = 100;
		for (MylectureNote my : myNoteList.get()) {
			lectureNoteListDTO noteDetail = new lectureNoteListDTO();
			noteDetail.setChapter(my.getChapter());
			String ContentPreview = my.getNoteContext().replaceAll("<[^>]*>","");
			if(cutsize < ContentPreview.length()) {
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
	
	public void myNoteDelete(MemberEntity userEntity , Long noteNo) {
		MylectureNote mylectureNote = mylectureNoteRepository.findByNoteNo(noteNo);
		mylectureNote.setNoteDelete(true);
		mylectureNoteRepository.save(mylectureNote);
	}

	public void QaSave(MemberEntity userEntity, QaRequest qaRequest) {
		Lecture lecture = lectureService.findByLectureID(qaRequest.getLectureId());
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		MylectureQa mylectureQa = new MylectureQa();
		mylectureQa.setMento(lecture.getMentoId());
		mylectureQa.setMyLecture(myLecture);
		mylectureQa.setQuestionContent(qaRequest.getQuestionContent());
		mylectureQa.setQuestionTitle(qaRequest.getQuestionTitle());
		mylectureQaRepository.save(mylectureQa);
	}

	public List<QaDTO> Qalist(MemberEntity userEntity, int page) {
		page = page <= 1 ? 0 : page;
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.Direction.DESC, "lastviewdate");
		Page<MyLecture> mylecture = mylectureRepository.findByMemberentity(userEntity, pageRequest);
		List<MylectureQa> qaList = mylectureQaRepository.findByMyLectureIn(mylecture.toList());
		List<QaDTO> qaDtos = new ArrayList<QaDTO>();
		for (MylectureQa q : qaList) {
			QaDTO qaDto = new QaDTO();
			qaDto.setLectureQaNo(q.getLectureQaNo());
			qaDto.setLectureTitle(q.getMyLecture().getLecture().getLectureName());
			qaDto.setMentoId(q.getMento().getId());
			qaDto.setQuestionTitle(q.getQuestionTitle());
			qaDto.setQuestionContent(q.getQuestionContent());
			qaDto.setQuestionDate(q.getQuestionDate());
			qaDtos.add(qaDto);
		}
		return qaDtos;

	}

	public MylectureDTO2 checkLectureComplete(MemberEntity userEntity, int page) {
		page = page <= 1 ? 0 : page;
		PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.DESC, "lastviewdate");
		Page<MyLecture> mylecture = mylectureRepository.findByMemberentityAndLectureProgressEquals(userEntity, 100, pageRequest);
		return convertMylectureDTO(mylecture);
	}

	public CertificateDto printCertificates(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture my = verifyUserHasLecture(userEntity, lecture);
		if(my.getLectureProgress() == 100) {
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

}
