package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, Long> {

}
