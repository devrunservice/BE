package com.devrun.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    // 필요한 커스텀 쿼리나 메소드 작성
}