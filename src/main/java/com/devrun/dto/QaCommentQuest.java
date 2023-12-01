package com.devrun.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "답변 댓글 양식")
public class QaCommentQuest {
	@ApiModelProperty(value = "질문글 Id" , example = "1")
	private Long questionId;
	@ApiModelProperty(value = "댓글 내용" , example = "답변 댓글 샘플입니다.")
	private String content;
}
