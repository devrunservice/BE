package com.devrun.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor			// 모든 필드를 인수로 받는 생성자만 생성
@NoArgsConstructor			// 기본 생성자가 생성
public class NoticeDTO {
	
	private int noticeNo, viewCount;
    private String title, content, id;
    private Date createdDate, modifiedDate;
    private Status status;
    
    @Getter
	@AllArgsConstructor
	public enum Status{
		
		ACTIVE("활성"),
		INACTIVE("비활성");
		
		private final String description;	//status.ACTIVE.description로 "활동"이라는 설명을 불러올 수 있다
	}
}