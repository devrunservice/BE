package com.devrun.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@ApiModel(description = "강의 작성 양식입니다.")
@Data
@RequiredArgsConstructor
public class NoteRequest {
	
	@ApiModelProperty(value = "비디오 ID" , example = "w8-X2DED94A" , required = true)
	private String videoId;
	
	@ApiModelProperty(value = "노트 제목" , example = "너구리를 맛있게 먹으려면" , required = true)
	private String noteTitle;
	
	@ApiModelProperty(value = "노트 내용" , example = "마법의 가루를 넣는다." , required = true)
	private String noteContent;

}
