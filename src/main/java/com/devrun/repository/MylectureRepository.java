package com.devrun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.youtube.Lecture;

public interface MylectureRepository extends JpaRepository<MyLecture, Long> {

	Optional<List<MyLecture>> findByMemberentityAndLecture(MemberEntity userEntity, Lecture lecture);

	Optional<List<MyLecture>> findByMemberentity(MemberEntity userEntity);

}
