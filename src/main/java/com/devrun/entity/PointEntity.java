package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
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
	@Column(name = "user_no", length = 5)
	@Comment("유저 구분 번호")
	private int user_no;	
	
	@Column(name = "mypoint", nullable = true, length = 10)
	@Comment("구매자 이메일")
	private int mypoint;
	
	@MapsId
    @OneToOne
    @JoinColumn(name = "user_no")
    private MemberEntity memberEntity;
	    
   

}
