package com.devrun.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProgressInfo {
	@ApiModelProperty(value = "시청 누적 시간(초 단위)" , example = "0" , required = true)
	private int currenttime;
	@ApiModelProperty(value = "비디오 ID" , example = "w8-X2DED94A" , required = true)
	private String videoid;
}
