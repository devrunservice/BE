package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;

@Data
@Entity
@Table(name = "Pointhistory")
public class PointHistoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int hno;	

	@Column(name = "pointupdown", nullable = true, length = 10)
	@Comment("포인트 증가/감소량 ")
	private int pointupdown;
	
	@Column(name = "explanation" ,nullable = true, length = 50)
	@Comment("포인트 차감 혹은 획득 경로")
	private String explanation;
	
	@Column(name = "updatetime")
	@Comment("updatetime")
	private String updatetime; 	

	@OneToOne
	@JoinColumn(name = "user_no") //외래키 컬럼명
    private MemberEntity memberEntity; //주 테이블의 PK값
	
	 // 기존의 setUserNo 메서드 대신 setMemberEntity 메서드를 사용
    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }
	

}