package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;

@Repository
public interface MemberEntityRepository extends JpaRepository<MemberEntity, Long> {

	int countById(String id);

	MemberEntity findById(String id);

	String deleteById(String id);

	PointEntity save(PointEntity point);

	MemberEntity findByName(String name);
	
}
