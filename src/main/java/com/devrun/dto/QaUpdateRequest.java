package com.devrun.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "질문 수정 양식")
public class QaUpdateRequest {
	@ApiModelProperty(value = "질문 ID" , example = "1" , required = true)
	private Long questionId;
	@ApiModelProperty(value = "수정할 질문 제목" , example = "너구리를 맛있게 먹으려면?" , required = true)
	private String questionTitle;
	@ApiModelProperty(value = "수정할 질문 내용" , example = "어떻게 하죠?" , required = true)
	private String questionContent;

}
