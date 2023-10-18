package com.devrun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devrun.dto.TotalProgress;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.youtube.Video;

public interface MylectureRepository extends JpaRepository<MyLecture, Long>{

	MyLecture findByMemberentityAndVideo(MemberEntity memberentity, Video videoentity);
	
	@Query(value = "SELECT user_no,lecture_no,SUM(timecheck) / (SELECT sum(video.total_play_time) FROM video WHERE video.lecture_id = lecture_no) as totalprogress FROM my_lecture JOIN video ON my_lecture.video_no = video.video_no GROUP BY lecture_no HAVING user_no = :userno",nativeQuery = true)
	List<Object> gettotalprogress(@Param(value = "userno") MemberEntity userno);


}