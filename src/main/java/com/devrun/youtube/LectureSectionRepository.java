package com.devrun.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureSectionRepository extends JpaRepository<LectureSection, Long> {
}
