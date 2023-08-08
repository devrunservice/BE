package com.devrun.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturecategoryRepository extends JpaRepository<LectureCategory, Long> {
    @Override
    <S extends LectureCategory> S save(S entity);
}
