package com.devrun.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private int commentNo, noticeNo, parentCommentNo, userNo;
    private String content, id, profileimgsrc;
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
