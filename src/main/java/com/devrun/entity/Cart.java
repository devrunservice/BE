package com.devrun.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.devrun.dto.LectureInfo;
import com.devrun.youtube.Lecture;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartno;


    @ManyToOne(fetch = FetchType.LAZY) //한명의 회원은 여러개의 장바구니를 가질 수 있기 때문에 장바구니는 회원과 다대일 관계
    @JoinColumn(name = "user_no")   //장바구니 테이블에서 회원 테이블로 접근하여 회원의 정보를 얻을 수 있으나, 그 역은 불가능하다.
    private MemberEntity memberEntity;


    @ManyToOne(fetch = FetchType.LAZY) //장바구니에 같은 강의가 여러번 담길 수 있기 때문에 장바구니와 강의는 다대일 관계
    @JoinColumn(name = "lectureid") //장바구니 테이블에서 강의 테이블로 접근하여 강의 정보를 얻을 수 있으나, 그 역은 불가능하다.
    private Lecture lecture;

    @Enumerated(EnumType.STRING)
    private removed deleteop;
    
    public enum removed{
    	ENABLE,
    	DISABLE
    }
    
    public LectureInfo getLectureInfo() {
		return new LectureInfo(
				this.lecture.getLectureThumbnail(),	
				this.lecture.getLectureName(),	
				this.lecture.getLectureIntro(), 
				this.lecture.getLecturePrice(),
				this.lecture.getLectureid(),		
				this.cartno
				);
    	
    }



	public removed getDeleteop() {
		// TODO Auto-generated method stub
		return this.deleteop;
	}
	
	public removed setDeleteop(removed status) {
		
		return this.deleteop = status;		
	}
	

}
