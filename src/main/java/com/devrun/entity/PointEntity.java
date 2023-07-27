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
@Table(name = "Point")
public class PointEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pno;
	
	@OneToOne
    @JoinColumn(name = "userNo")
    private MemberEntity memberEntity;
	
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