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

	public MycouresDTO findMycoures(MemberEntity userEntity, Long lectureId) {
		Lecture lecture = lectureService.findByLectureID(lectureId);
		List<MyLecture> myLectureList = verifyUserHasLecture(userEntity, lecture);

		return convertMyLectureToMycouresDTO(myLectureList);
	}

	public Map<String, Object> progress(MemberEntity userEntity, String videoid, int currenttime) {
		Video videoentity = videoRepository.findByVideoId(videoid);
		List<MyLecture> mylecture = verifyUserHasLecture(userEntity, videoentity.getLecture());
		if (mylecture.size() == 1) {
			List<MyLectureProgress> mylectureProgressEntity = mylectureProgressRepository
					.findByMyLecture(mylecture.get(0));
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
			mylecture.get(0).setLectureProgress(lectureProgress);
			mylectureRepository.save(mylecture.get(0));

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("user name", userEntity.getName());
			map.put("video id", videoid);
			map.put("lectureProgress", lectureProgress);
			map.put("status", "ok");

			return map;
		} else {
			throw new NoSuchElementException("Detected Two or more Lecture!");
		}

	}

	public List<MyLecture> verifyUserHasLecture(MemberEntity userEntity, Lecture lecture) {
		Optional<List<MyLecture>> optional = mylectureRepository.findByMemberentityAndLecture(userEntity, lecture);
		if (optional.isPresent() && optional.get().size() >= 1) {
			return optional.get();
		} else {
			throw new NoSuchElementException("This User isn't taking this Lecture!");
		}
	}

	public MycouresDTO convertMyLectureToMycouresDTO(List<MyLecture> myLectureList) {
		List<MyLectureProgress> myCouresList = mylectureProgressRepository.findByMyLectureIn(myLectureList);
		int wholeStudyTime = 0;
		int wholeLectureTime = 0;
		for (MyLectureProgress myprogress : myCouresList) {
			wholeLectureTime += myprogress.getVideo().getTotalPlayTime();
			wholeStudyTime += myprogress.getTimecheck();

		}

		List<MycouresDTO> mycouresList = new ArrayList<MycouresDTO>();
		for (MyLecture myLecture : myLectureList) {
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
		}

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

	public List<MyLectureNoteDTO> myNotelist(MemberEntity userEntity , int page) {
		page = page<=1 ? 0 : page; 
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.Direction.DESC , "lastviewdate");
		Page<MyLecture> myLectureList = mylectureRepository.findByMemberentity(userEntity , pageRequest);
		Optional<List<MylectureNote>> myNoteList = mylectureNoteRepository.findByMyLectureInOrderByCreateDateDesc(myLectureList.toList());
		if (myNoteList.isPresent()) {
		} else {
			throw new NoSuchElementException("This User isn't taking this note!");
		}
		List<MyLectureNoteDTO> lectureNoteDTOs = new ArrayList<MyLectureNoteDTO>();
		for (MyLecture l : myLectureList.toList()) {
			int count = 0;
			int chapter = 0;
			String subhead = null;
			MyLectureNoteDTO myLectureNoteDto = new MyLectureNoteDTO();
			List<lectureNoteDetailDTO> noteDetailDTOs = new ArrayList<lectureNoteDetailDTO>();
			for (MylectureNote n : myNoteList.get()) {
				if (n.getMyLecture().equals(l)) {
					lectureNoteDetailDTO lecturenoteDetailDTO = new lectureNoteDetailDTO();
					lecturenoteDetailDTO.setContent(n.getNoteContext());
					lecturenoteDetailDTO.setDate(n.getCreateDate());
					lecturenoteDetailDTO.setLastModifiedDate(n.getModiDate());
					lecturenoteDetailDTO.setNoteId(n.getNoteNo());
					lecturenoteDetailDTO.setNoteTitle(n.getNoteTitle());
					noteDetailDTOs.add(lecturenoteDetailDTO);
					count++;
					chapter=n.getChapter();
					subhead=n.getSubheading();
				}
			}
			myLectureNoteDto.setLectureNoteDetailDTOList(noteDetailDTOs);
			myLectureNoteDto.setLectureTitle(l.getLecture().getLectureName());
			myLectureNoteDto.setCount(count);
			myLectureNoteDto.setChapter(chapter);
			myLectureNoteDto.setSubHeading(subhead);
			lectureNoteDTOs.add(myLectureNoteDto);
		}
		return lectureNoteDTOs;
	}

	public void myNoteSave(MemberEntity userEntity, NoteRequest noteRequest) {
		Lecture lecture = lectureService.findByLectureID(noteRequest.getLectureId());

		List<MyLecture> myLectureList = verifyUserHasLecture(userEntity, lecture);
		Optional<List<MylectureNote>> sdf = mylectureNoteRepository.findByMyLectureIn(myLectureList);
		if (sdf.get().size() >= 3) {
			throw new NoSuchElementException("This User can not more create Note");
		} else {
			MylectureNote mylectureNote = new MylectureNote();
			mylectureNote.setMyLecture(myLectureList.get(0));
			mylectureNote.setNoteContext(noteRequest.getNoteContent());
			mylectureNote.setNoteTitle(noteRequest.getNoteTitle());
			mylectureNote.setChapter(noteRequest.getChapter());
			mylectureNote.setSubheading(noteRequest.getSubheading());
			mylectureNoteRepository.save(mylectureNote);
		}

	}

	public void QaSave(MemberEntity userEntity, QaRequest qaRequest) {
		Lecture lecture = lectureService.findByLectureID(qaRequest.getLectureId());
		List<MyLecture> myLectureList = verifyUserHasLecture(userEntity, lecture);
		MylectureQa mylectureQa = new MylectureQa();
		mylectureQa.setMento(lecture.getMentoId());
		mylectureQa.setMyLecture(myLectureList.get(0));
		mylectureQa.setQuestionContent(qaRequest.getQuestionContent());
		mylectureQa.setQuestionTitle(qaRequest.getQuestionTitle());
		mylectureQaRepository.save(mylectureQa);
	}
	
	public List<QaDTO> Qalist(MemberEntity userEntity , int page) {
		page = page<=1 ? 0 : page; 
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.Direction.DESC , "lastviewdate");
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
}
