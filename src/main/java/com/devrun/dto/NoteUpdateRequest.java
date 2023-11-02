package com.devrun.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoteUpdateRequest {
	
	@ApiModelProperty(value = "수정할 노트 ID" , example = "1" , required = true)
	private Long noteNo;
	
	@ApiModelProperty(value = "수정할 노트 제목" , example = "널굴륑~" , required = true)
	private String noteTitle;

	@ApiModelProperty(value = "수정할 노트 내용" , example = "널굴륑~마싯어!" , required = true)
	private String noteContent;
}
