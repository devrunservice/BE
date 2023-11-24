package com.devrun.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "질답 댓글 수정 양식")
public class QaCommentUpdateDto {
	private String content;
}
