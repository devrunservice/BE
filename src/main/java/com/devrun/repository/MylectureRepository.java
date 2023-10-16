package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.youtube.Video;

public interface MylectureRepository extends JpaRepository<MyLecture, Long>{

	MyLecture findByMemberentityAndVideo(MemberEntity memberentity, Video videoentity);	

}
