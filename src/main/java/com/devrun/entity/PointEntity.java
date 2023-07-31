package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Comment;

import lombok.Data;

@Data
@Entity
@Table(name = "Point")
public class PointEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pno;

	//외래 키가 대상이 되는 테이블에 있는 경우
	@OneToOne
	@JoinColumn(name = "user_no") //외래키 컬럼명
    private MemberEntity memberEntity; //주 테이블의 PK값



	// UserNo에 접근하는 메소드
	public int getUserNo() {
	    if (this.memberEntity != null) {
	        return this.memberEntity.getUserNo();
	    } else {
	        return -1; // 혹은 적절한 기본값
	    }
	}
	
	@Column(name = "mypoint", nullable = true, length = 10)
	@Comment("구매자 이메일")
	private int mypoint;
	
}