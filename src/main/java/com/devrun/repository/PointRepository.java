package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.devrun.entity.PointEntity;

@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {


	PointEntity findByUserNo(int userno);


}
