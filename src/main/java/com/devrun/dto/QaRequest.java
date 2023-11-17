package com.devrun.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "질문 작성 양식")
public class QaRequest {
	@ApiModelProperty(value = "강의 ID" , example = "1" , required = true)
	private Long lectureId;
	@ApiModelProperty(value = "비디오 ID" , example = "1" , required = true)
	private String videoId;
	@ApiModelProperty(value = "질문 제목" , example = "너구리를 맛있게 먹으려면?" , required = true)
	private String questionTitle;
	@ApiModelProperty(value = "질문 내용" , example = "어떻게 하죠?" , required = true)
	private String questionContent;
}
