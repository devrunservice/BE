package com.devrun.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.PointEntity;

@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {


	PointEntity findByMemberEntity_userNo(int usrno);


   


	//findBy+(fk키의 주인 entity의 필드명에서 첫글자 대문자)+_+(fk키의 주인 entity의 식별자(PK값)필드명에서 첫글자 대문자)
}
