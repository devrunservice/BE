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

import com.devrun.dto.MyLectureNoteDTO;
import com.devrun.dto.MycouresDTO;
import com.devrun.dto.MylectureDTO;
import com.devrun.dto.NoteRequest;
import com.devrun.dto.NoteUpdateRequest;
import com.devrun.dto.QaDTO;
import com.devrun.dto.QaRequest;
import com.devrun.dto.SectionInfo;
import com.devrun.dto.VideoInfo;
import com.devrun.dto.lectureNoteDetailDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.entity.MyLectureProgress;
import com.devrun.entity.MylectureNote;
import com.devrun.entity.MylectureQa;
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

	public void registLecture(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
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
			throw new NoSuchElementException("This User isn't taking this Lecture!");
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

	public List<MylectureDTO> mylecturelist(MemberEntity userEntity, int page) {
		if (page <= 1) {
			page = 0;
		}
		PageRequest pageRequest = PageRequest.of(page, 9, Direction.DESC, "lastviewdate");
		Page<MyLecture> pageMylecture = mylectureRepository.findByMemberentity(userEntity, pageRequest);
		List<MylectureDTO> mylectureDTOList = new ArrayList<MylectureDTO>();
		for (MyLecture mylecture : pageMylecture) {
			MylectureDTO dto = new MylectureDTO(mylecture);
			mylectureDTOList.add(dto);
		}
		return mylectureDTOList;

	}

	public List<MyLectureNoteDTO> myNotelist(MemberEntity userEntity, int page) {
		page = page <= 1 ? 0 : page;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "lastviewdate");
		Page<MyLecture> myLectureList = mylectureRepository.findByMemberentity(userEntity, pageRequest);
		List<MylectureNote> myNoteList = mylectureNoteRepository
				.findByMyLectureInAndNoteDeleteFalseOrderByDateDesc(myLectureList.toList());

		List<MyLectureNoteDTO> lectureNoteDTOs = new ArrayList<MyLectureNoteDTO>();

		for (MyLecture mylecture : myLectureList.toList()) {
			MyLectureNoteDTO myLectureNoteDto = new MyLectureNoteDTO();
			myLectureNoteDto.setLastStudyDate(mylecture.getLastviewdate());
			myLectureNoteDto.setLectureId(mylecture.getLecture().getLectureid());
			myLectureNoteDto.setLectureThumbnail(mylecture.getLecture().getLectureThumbnail());
			myLectureNoteDto.setLectureTitle(mylecture.getLecture().getLectureName());
			for (MylectureNote ml : myNoteList) {
				if (ml.getMyLecture().equals(mylecture)) {
					myLectureNoteDto.setCount(myLectureNoteDto.getCount() + 1);
				}
			}
			lectureNoteDTOs.add(myLectureNoteDto);
		}
		return lectureNoteDTOs;
	}

	public List<lectureNoteDetailDTO> noteDetaiList(MemberEntity userEntity, Long lectureId, int page) {
		page = page <= 1 ? 0 : page;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "date");
		Lecture lecture = lectureService.findByLectureID(lectureId);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		List<lectureNoteDetailDTO> noteDetilas = new ArrayList<lectureNoteDetailDTO>();
		Optional<Page<MylectureNote>> myNoteList = mylectureNoteRepository.findByMyLectureAndNoteDeleteFalse(myLecture, pageRequest);
		if (myNoteList.isPresent()) {
		} else {
			throw new NoSuchElementException("There are no note");
		}
		for (MylectureNote my : myNoteList.get()) {
			lectureNoteDetailDTO noteDetail = new lectureNoteDetailDTO();
			noteDetail.setChapter(my.getChapter());
			noteDetail.setContent(my.getNoteContext());
			noteDetail.setDate(my.getDate());
			noteDetail.setNoteTitle(my.getNoteTitle());
			noteDetail.setSubHeading(my.getSubheading());
			noteDetail.setNoteId(my.getNoteNo());
			noteDetilas.add(noteDetail);
		}
		return noteDetilas;
	}

	public void myNoteSave(MemberEntity userEntity, NoteRequest noteRequest) {
		Video video = videoRepository.findByVideoId(noteRequest.getVideoId());

		MyLecture myLecture = verifyUserHasLecture(userEntity, video.getLecture());
		MylectureNote mylectureNote = new MylectureNote();
		mylectureNote.setMyLecture(myLecture);
		mylectureNote.setNoteContext(noteRequest.getNoteContent());
		mylectureNote.setNoteTitle(noteRequest.getNoteTitle());
		mylectureNote.setChapter(video.getLectureSection().getSectionNumber());
		mylectureNote.setSubheading(video.getLectureSection().getSectionTitle());
		mylectureNoteRepository.save(mylectureNote);

	}

	public lectureNoteDetailDTO myNoteUpdate(MemberEntity userEntity, NoteUpdateRequest noteUpdateRequest) {
		MylectureNote mylectureNote = mylectureNoteRepository.findByNoteNo(noteUpdateRequest.getNoteNo());
		verifyUserHasLecture(userEntity, mylectureNote.getMyLecture().getLecture());
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

	public String checkLectureComplete(MemberEntity userEntity, Long lectureNo) {
		Lecture lecture = lectureService.findByLectureID(lectureNo);
		MyLecture myLecture = verifyUserHasLecture(userEntity, lecture);
		if (myLecture.getLectureProgress() == 100) {
			//유저 이름
			return "This user has taken all the lectures.";
		} else {
			return "This user has not taken all of the lectures.";
		}

	}
}
