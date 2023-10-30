package com.devrun.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReviewRequest {
	
	@ApiModelProperty(value = "강의 ID" , example = "1" , required = true)
	private Long lectureId;
	
	@ApiModelProperty(value = "후기 내용" , example = "김영한 짱짱맨" , required = true)
	private String reviewContent;
	
	@ApiModelProperty(value = "후기 평점" , example = "1.5" , required = true)
	private float reviewRating;
	
}
