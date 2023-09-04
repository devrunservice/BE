package com.devrun.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NoticeDTO {
	
	private int noticeNo, viewCount, userNo;
    private String title, content;
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